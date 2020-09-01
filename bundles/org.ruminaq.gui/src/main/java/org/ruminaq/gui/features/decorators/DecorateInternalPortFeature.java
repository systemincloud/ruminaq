/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.decorators;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.tb.BorderDecorator;
import org.eclipse.graphiti.tb.IBorderDecorator;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.graphiti.tb.ImageDecorator;
import org.eclipse.graphiti.util.IColorConstant;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.debug.InternalPortBreakpoint;
import org.ruminaq.gui.api.DecoratorExtension;
import org.ruminaq.gui.features.custom.InternalPortToggleBreakpointFeature;
import org.ruminaq.gui.image.Images;
import org.ruminaq.gui.model.diagram.InternalPortShape;
import org.ruminaq.model.ruminaq.InternalInputPort;
import org.ruminaq.model.ruminaq.InternalPort;
import org.ruminaq.util.EclipseUtil;
import org.ruminaq.validation.ValidationStatusAdapter;

@Component(property = { "service.ranking:Integer=5" })
public class DecorateInternalPortFeature implements DecoratorExtension {

  private static Optional<InternalPortShape> shapeFromPictogramElement(
      PictogramElement pe) {
    return Optional.of(pe).filter(InternalPortShape.class::isInstance)
        .map(InternalPortShape.class::cast);
  }

  private static Optional<InternalPort> modelFromPictogramElement(
      PictogramElement pe) {
    return shapeFromPictogramElement(pe).map(InternalPortShape::getModelObject)
        .filter(InternalPort.class::isInstance).map(InternalPort.class::cast);
  }

  @Override
  public boolean forPictogramElement(PictogramElement pe) {
    return modelFromPictogramElement(pe).isPresent();
  }

  @Override
  public Collection<IDecorator> getDecorators(PictogramElement pe,
      IFeatureProvider fp) {
    InternalPortShape shape = shapeFromPictogramElement(pe).orElseThrow();
    return modelFromPictogramElement(pe).map(ip -> {
      List<IDecorator> decorators = new LinkedList<>();
      decorators.addAll(validationDecorators(shape, ip, fp));
      decorators.addAll(breakpointDecorators(shape, ip, fp));
      return decorators;
    }).orElse(Collections.emptyList());
  }

  private Collection<? extends IDecorator> validationDecorators(
      InternalPortShape shape, InternalPort ip, IFeatureProvider fp) {
    List<IDecorator> decorators = new LinkedList<>();
    shape.getAnchors();

//      if (ac.getAnchors().size() > 0
//          && ac.getAnchors().get(0).getIncomingConnections().size() > 0) {
//        Object boo = fp.getBusinessObjectForPictogramElement(
//            ac.getAnchors().get(0).getIncomingConnections().get(0));
//
//        ValidationStatusAdapter statusAdapter = (ValidationStatusAdapter) EcoreUtil
//            .getRegisteredAdapter((EObject) boo, ValidationStatusAdapter.class);
//        if (statusAdapter == null)
//          return decorators;
//        final IBorderDecorator decorator;
//        final IStatus status = statusAdapter.getValidationStatus();
//        switch (status.getSeverity()) {
//          case IStatus.INFO:
//            decorator = new BorderDecorator(IColorConstant.BLUE, 2, 3);
//            break;
//          case IStatus.WARNING:
//            decorator = new BorderDecorator(IColorConstant.YELLOW, 2, 3);
//            break;
//          case IStatus.ERROR:
//            decorator = new BorderDecorator(IColorConstant.RED, 2, 3);
//            break;
//          default:
//            decorator = null;
//            break;
//        }
//
//        if (decorator != null) {
//          decorator.setMessage(status.getMessage());
//          decorators.add(decorator);
//        }
//      }
//    }

    return decorators;
  }

  private Collection<? extends IDecorator> breakpointDecorators(
      PictogramElement pe, InternalPort ip, IFeatureProvider fp) {
    List<IDecorator> decorators = new LinkedList<>();
    IResource resource = EclipseUtil.emfResourceToIResource(
        fp.getDiagramTypeProvider().getDiagram().eResource());
    InternalPortToggleBreakpointFeature.breakpointFromModel(resource, ip)
        .ifPresent(b -> {
          try {
            ImageDecorator bp = b.isEnabled()
                ? new ImageDecorator(Images.IMG_TOGGLE_BREAKPOINT_S)
                : new ImageDecorator(Images.IMG_TOGGLE_BREAKPOINT_D);
            bp.setX(1);
            bp.setY(1);
            decorators.add(bp);
          } catch (CoreException e) {
            e.printStackTrace();
          }

        });
    return decorators;
  }

}
