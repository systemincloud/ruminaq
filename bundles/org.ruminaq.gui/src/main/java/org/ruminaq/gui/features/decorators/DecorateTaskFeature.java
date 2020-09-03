/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.decorators;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IPlatformImageConstants;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.graphiti.tb.IImageDecorator;
import org.eclipse.graphiti.tb.ImageDecorator;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.DecoratorExtension;
import org.ruminaq.gui.model.diagram.TaskShape;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.validation.ValidationStatusAdapter;

/**
 * Service DecoratorExtension implementation.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=5" })
public class DecorateTaskFeature implements DecoratorExtension {

  private static final int POSITION_X = -5;

  private static final int POSITION_Y = -5;

  private static Optional<Task> modelFromPictogramElement(PictogramElement pe) {
    return Optional.of(pe).filter(TaskShape.class::isInstance)
        .map(TaskShape.class::cast).map(TaskShape::getModelObject)
        .filter(Task.class::isInstance).map(Task.class::cast);
  }

  @Override
  public boolean forPictogramElement(PictogramElement pe) {
    return modelFromPictogramElement(pe).isPresent();
  }

  /**
   * Add icon in top right corner if issue.
   */
  @Override
  public Collection<IDecorator> getDecorators(PictogramElement pe,
      IFeatureProvider fp) {
    return modelFromPictogramElement(pe)
        .map(task -> EcoreUtil.getRegisteredAdapter(task,
            ValidationStatusAdapter.class))
        .filter(ValidationStatusAdapter.class::isInstance)
        .map(ValidationStatusAdapter.class::cast)
        .map(ValidationStatusAdapter::getValidationStatus)
        .map((IStatus status) -> {
          IImageDecorator decorator = statusToDecorator(status);
          Optional.ofNullable(decorator).ifPresent((IImageDecorator d) -> {
            d.setX(POSITION_X);
            d.setY(POSITION_Y);
            d.setMessage(status.getMessage());
          });
          return decorator;
        }).stream().collect(Collectors.toList());
  }

  private static IImageDecorator statusToDecorator(IStatus status) {
    IImageDecorator decorator = switch (status.getSeverity()) {
      case IStatus.INFO -> new ImageDecorator(
          IPlatformImageConstants.IMG_ECLIPSE_INFORMATION_TSK);
      case IStatus.WARNING -> new ImageDecorator(
          IPlatformImageConstants.IMG_ECLIPSE_WARNING_TSK);
      case IStatus.ERROR -> new ImageDecorator(
          IPlatformImageConstants.IMG_ECLIPSE_ERROR_TSK);
      default -> null;
    };
    return decorator;
  }
}
