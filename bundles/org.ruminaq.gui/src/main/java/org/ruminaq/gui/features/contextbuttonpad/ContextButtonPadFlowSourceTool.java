/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.contextbuttonpad;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.eclipse.graphiti.features.ICreateConnectionFeature;
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
    List<IContextButtonEntry> buttons = new ArrayList<>();

    PictogramElement pe = context.getPictogramElement();

    CreateConnectionContext ccc = new CreateConnectionContext();
    ccc.setSourcePictogramElement(pe);
    Anchor anchor = null;
    if (pe instanceof Anchor)
      anchor = (Anchor) pe;
    else if (pe instanceof AnchorContainer)
      anchor = Graphiti.getPeService().getChopboxAnchor((AnchorContainer) pe);
    ccc.setSourceAnchor(anchor);

    ICreateConnectionFeature[] features = fp.getCreateConnectionFeatures();
    ContextButtonEntry button = new ContextButtonEntry(null, context);
    button.setText("Create connection");
    ArrayList<String> names = new ArrayList<>();
    button.setIconId(Images.Image.IMG_CONTEXT_SIMPLECONNECTION.name());
    for (ICreateConnectionFeature feature : features) {
      if (feature.isAvailable(ccc) && feature.canStartConnection(ccc)) {
        button.addDragAndDropFeature(feature);
        names.add(feature.getCreateName());
      }
    }

    buttons.add(button);

    return buttons;
  }

}
