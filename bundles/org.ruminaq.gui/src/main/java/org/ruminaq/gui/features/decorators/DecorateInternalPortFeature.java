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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.draw2d.Graphics;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.tb.BorderDecorator;
import org.eclipse.graphiti.tb.IBorderDecorator;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.graphiti.tb.ImageDecorator;
import org.eclipse.graphiti.util.IColorConstant;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.DecoratorExtension;
import org.ruminaq.gui.features.custom.InternalPortToggleBreakpointFeature;
import org.ruminaq.gui.image.Images;
import org.ruminaq.gui.model.diagram.InternalPortShape;
import org.ruminaq.gui.model.diagram.SimpleConnectionShape;
import org.ruminaq.model.ruminaq.InternalPort;
import org.ruminaq.util.EclipseUtil;
import org.ruminaq.util.Result;
import org.ruminaq.validation.ValidationStatusAdapter;

/**
 * Decorate InternalPoint with warnings, breakpoints, etc.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=5" })
public class DecorateInternalPortFeature implements DecoratorExtension {

  private static final int MARKER_WIDTH = 2;

  private static final int MARKER_STYLE = Graphics.LINE_DOT;

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
    return modelFromPictogramElement(pe).map((InternalPort ip) -> {
      List<IDecorator> decorators = new LinkedList<>();
      decorators.addAll(validationDecorator(shape));
      breakpointDecorator(ip, fp).ifPresent(decorators::add);
      return decorators;
    }).orElseGet(Collections::emptyList);
  }

  /**
   * Add color border.
   *
   * @param shape model object
   * @return BorderDecorator
   */
  private static List<IBorderDecorator> validationDecorator(
      InternalPortShape shape) {
    return Optional.of(shape).map(InternalPortShape::getAnchors)
        .map(EList::stream).orElseGet(Stream::empty).findFirst()
        .map(Anchor::getIncomingConnections).map(EList::stream)
        .orElseGet(Stream::empty)
        .filter(SimpleConnectionShape.class::isInstance)
        .map(SimpleConnectionShape.class::cast).findFirst()
        .map(SimpleConnectionShape::getModelObject).map(List::stream)
        .orElseGet(Stream::empty)
        .map(sc -> EcoreUtil.getRegisteredAdapter(sc,
            ValidationStatusAdapter.class))
        .filter(ValidationStatusAdapter.class::isInstance)
        .map(ValidationStatusAdapter.class::cast)
        .map(ValidationStatusAdapter::getValidationStatus)
        .map((IStatus status) -> {
          IBorderDecorator decorator = statusToDecorator(status);
          Optional.ofNullable(decorator)
              .ifPresent(d -> d.setMessage(status.getMessage()));
          return decorator;
        }).collect(Collectors.toList());
  }

  private static IBorderDecorator statusToDecorator(IStatus status) {
    return switch (status.getSeverity()) {
      case IStatus.INFO -> new BorderDecorator(IColorConstant.BLUE,
          MARKER_WIDTH, MARKER_STYLE);
      case IStatus.WARNING -> new BorderDecorator(IColorConstant.YELLOW,
          MARKER_WIDTH, MARKER_STYLE);
      case IStatus.ERROR -> new BorderDecorator(IColorConstant.RED,
          MARKER_WIDTH, MARKER_STYLE);
      default -> null;
    };
  }

  /**
   * Add small dot in the top right corner.
   *
   * @param ip model object
   * @param fp IFeatureProvider
   * @return ImageDecorator
   */
  private static Optional<IDecorator> breakpointDecorator(InternalPort ip,
      IFeatureProvider fp) {
    IResource resource = EclipseUtil.emfResourceToIResource(
        fp.getDiagramTypeProvider().getDiagram().eResource());
    return InternalPortToggleBreakpointFeature.breakpointFromModel(resource, ip)
        .map(b -> Result.attempt(b::isEnabled).orElse(null))
        .filter(Objects::nonNull).map((Boolean e) -> {
          ImageDecorator bp = new ImageDecorator(
              Images.IMG_TOGGLE_BREAKPOINT_S);
          if (!e.booleanValue()) {
            bp = new ImageDecorator(Images.IMG_TOGGLE_BREAKPOINT_D);
          }
          bp.setX(1);
          bp.setY(1);
          return bp;
        });
  }
}
