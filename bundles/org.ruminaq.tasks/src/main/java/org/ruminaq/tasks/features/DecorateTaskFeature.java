/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.features;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.platform.IPlatformImageConstants;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.graphiti.tb.IImageDecorator;
import org.eclipse.graphiti.tb.ImageDecorator;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.validation.ValidationStatusAdapter;

public class DecorateTaskFeature {

  public List<IDecorator> getDecorators(IFeatureProvider fp, Task task) {
    List<IDecorator> decorators = new LinkedList<>();

    decorators.addAll(validationDecorators(task));

    return decorators;
  }

  private Collection<? extends IDecorator> validationDecorators(Task task) {
    List<IDecorator> decorators = new LinkedList<>();
    int x_dec = -5;
    int y_dec = -5;

    ValidationStatusAdapter statusAdapter = (ValidationStatusAdapter) EcoreUtil
        .getRegisteredAdapter(task, ValidationStatusAdapter.class);
    if (statusAdapter == null)
      return decorators;
    final IImageDecorator decorator;
    final IStatus status = statusAdapter.getValidationStatus();
    switch (status.getSeverity()) {
      case IStatus.INFO:
        decorator = new ImageDecorator(
            IPlatformImageConstants.IMG_ECLIPSE_INFORMATION_TSK);
        break;
      case IStatus.WARNING:
        decorator = new ImageDecorator(
            IPlatformImageConstants.IMG_ECLIPSE_WARNING_TSK);
        break;
      case IStatus.ERROR:
        decorator = new ImageDecorator(
            IPlatformImageConstants.IMG_ECLIPSE_ERROR_TSK);
        break;
      default:
        decorator = null;
        break;
    }

    if (decorator != null) {
      decorator.setX(x_dec);
      decorator.setY(y_dec);
      decorator.setMessage(status.getMessage());
      decorators.add(decorator);
    }
    return decorators;
  }
}
