/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.contextbuttonpad;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.tb.ContextButtonEntry;
import org.eclipse.graphiti.tb.IContextButtonEntry;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.Images;
import org.ruminaq.gui.api.DomainContextButtonPadDataExtension;
import org.ruminaq.gui.features.contextbuttonpad.ContextButtonPadFlowSourceTool.Filter;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.gui.model.diagram.SimpleConnectionPointShape;
import org.ruminaq.model.ruminaq.FlowSource;
import org.ruminaq.util.ServiceFilter;
import org.ruminaq.util.ServiceFilterArgs;

/**
 * Start SimpleConnection on FlowSource and SimpleConnectionPoint.
 * 
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=5" })
@ServiceFilter(Filter.class)
public class ContextButtonPadFlowSourceTool
    implements DomainContextButtonPadDataExtension {

  /**
   * Only on SimpleConnectionPoint.
   */
  public static class Filter implements Predicate<ServiceFilterArgs> {

    @Override
    public boolean test(ServiceFilterArgs args) {
      Optional<PictogramElement> peOpt = Optional.ofNullable(args)
          .map(ServiceFilterArgs::getArgs).map(l -> l.get(1))
          .filter(IPictogramElementContext.class::isInstance)
          .map(IPictogramElementContext.class::cast)
          .map(IPictogramElementContext::getPictogramElement);
      return peOpt.filter(RuminaqShape.class::isInstance)
          .map(RuminaqShape.class::cast).map(RuminaqShape::getModelObject)
          .filter(FlowSource.class::isInstance).isPresent()
          || peOpt.filter(SimpleConnectionPointShape.class::isInstance)
              .isPresent();
    }
  }

  @Override
  public Collection<IContextButtonEntry> getContextButtonPad(
      IFeatureProvider fp, IPictogramElementContext context) {
    return Optional.of(context)
        .map(IPictogramElementContext::getPictogramElement)
        .map((PictogramElement pe) -> {
          CreateConnectionContext ccc = new CreateConnectionContext();
          ccc.setSourcePictogramElement(pe);
          Optional.of(pe).filter(Anchor.class::isInstance)
              .map(Anchor.class::cast)
              .or(() -> Optional.of(pe)
                  .filter(AnchorContainer.class::isInstance)
                  .map(AnchorContainer.class::cast)
                  .map(Graphiti.getPeService()::getChopboxAnchor))
              .ifPresent(ccc::setSourceAnchor);
          return ccc;
        }).map((CreateConnectionContext ccc) -> {
          ContextButtonEntry button = new ContextButtonEntry(null, context);
          button.setText("Create connection");
          button.setIconId(Images.Image.IMG_CONTEXT_SIMPLECONNECTION.name());
          Stream.of(fp.getCreateConnectionFeatures())
              .filter(f -> f.isAvailable(ccc))
              .filter(f -> f.canStartConnection(ccc)).findFirst()
              .ifPresent(button::addDragAndDropFeature);
          return button;
        }).stream().collect(Collectors.toList());
  }

}
