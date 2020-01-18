/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.add;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IPeCreateService;
import org.ruminaq.gui.model.diagram.PortShape;
import org.ruminaq.gui.model.diagram.RuminaqDiagram;
import org.ruminaq.model.ruminaq.BaseElement;

/**
 * AddPort common class.
 *
 * @author Marek Jagielski
 */
public abstract class AbstractAddPortFeature extends AbstractAddElementFeature {

  public AbstractAddPortFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canAdd(IAddContext context) {
    return context.getTargetContainer() instanceof RuminaqDiagram;
  }
  
  public PictogramElement add(IAddContext context, PortShape portShape) {
    portShape.setContainer(context.getTargetContainer());
    portShape.setX(context.getX());
    portShape.setY(context.getY());
    IPeCreateService peCreateService = Graphiti.getPeCreateService();
    peCreateService.createChopboxAnchor(portShape);
    
    BaseElement port = (BaseElement) context.getNewObject();
    portShape.setModelObject(port);
    addLabel(portShape);
    return portShape;
  }
}
