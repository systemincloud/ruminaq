/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.validation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.validation.marker.MarkerUtil;
import org.eclipse.emf.validation.model.EvaluationMode;
import org.eclipse.emf.validation.model.IConstraintStatus;
import org.eclipse.emf.validation.service.IBatchValidator;
import org.eclipse.emf.validation.service.ModelValidationService;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationEvent;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.ValidatorMessage;
import org.ruminaq.model.ruminaq.MainTask;

public class DefaultValidator extends AbstractValidator {

  public static final String VALIDATION_MARKER = "org.ruminaq.validation.marker"; //$NON-NLS-1$

  @Override
  public ValidationResult validate(ValidationEvent event, ValidationState state,
      IProgressMonitor monitor) {
    IResource file = event.getResource();
    if ((event.getKind() & IResourceDelta.REMOVED) != 0
        || file.isDerived(IResource.CHECK_ANCESTORS)
        || !(file instanceof IFile))
      return new ValidationResult();

    IFile modelFile = (IFile) file;

    modelFile = (IFile) file;
    try {
      modelFile.deleteMarkers(VALIDATION_MARKER, false,
          IProject.DEPTH_INFINITE);
    } catch (CoreException e1) {
    }

    ResourceSet resSet = new ResourceSetImpl();
    Resource resource = null;
    try {
      resource = resSet.getResource(URI.createPlatformResourceURI(
          modelFile.getFullPath().toString(), true), true);
    } catch (Exception e) {
    }
    if (resource == null)
      return new ValidationResult();

    MainTask mainTask = null;
    if (resource.getContents().size() > 0)
      mainTask = (MainTask) resource.getContents().get(1);
    if (mainTask == null)
      return new ValidationResult();

    ValidationResult result = new ValidationResult();
    IBatchValidator validator = (IBatchValidator) ModelValidationService
        .getInstance().newValidator(EvaluationMode.BATCH);
    validator.setIncludeLiveConstraints(true);
    processStatus(validator.validate(mainTask), modelFile, result);
    return result;
  }

  protected static void processStatus(IStatus status, IResource resource,
      ValidationResult result) {
    if (status.isMultiStatus()) {
      for (IStatus child : status.getChildren())
        processStatus(child, resource, result);
    } else if (!status.isOK())
      result.add(createValidationMessage(status, resource));
  }

  public static ValidatorMessage createValidationMessage(IStatus status,
      IResource resource) {
    ValidatorMessage message = ValidatorMessage.create(status.getMessage(),
        resource);
    switch (status.getSeverity()) {
      case IStatus.INFO:
        message.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
        break;
      case IStatus.WARNING:
        message.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
        break;
      case IStatus.ERROR, IStatus.CANCEL:
        message.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
        break;
    }

    if (status instanceof IConstraintStatus) {
      IConstraintStatus ics = (IConstraintStatus) status;
      message.setAttribute(EValidator.URI_ATTRIBUTE,
          EcoreUtil.getURI(ics.getTarget()).toString());
      message.setAttribute(MarkerUtil.RULE_ATTRIBUTE,
          ics.getConstraint().getDescriptor().getId());
      if (ics.getResultLocus().size() > 0) {
        StringBuffer relatedUris = new StringBuffer();
        for (EObject eobject : ics.getResultLocus()) {
          relatedUris.append(EcoreUtil.getURI(eobject).toString()).append(" ");
        }
        relatedUris.deleteCharAt(relatedUris.length() - 1);
        String uris = relatedUris.toString();
        message.setAttribute(EValidator.RELATED_URIS_ATTRIBUTE, uris);
      }
    }

    if (status instanceof StatusLocationDecorator)
      message.setAttribute(IMarker.LOCATION,
          ((StatusLocationDecorator) status).getLocation());
    message.setType(VALIDATION_MARKER);

    return message;
  }

  @Override
  public void clean(IProject project, ValidationState state,
      IProgressMonitor monitor) {
    super.clean(project, state, monitor);
    try {
      project.deleteMarkers(VALIDATION_MARKER, false,
          IProject.DEPTH_INFINITE);
    } catch (CoreException e) {
    }
  }

}
