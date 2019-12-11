/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.add;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IPeCreateService;
import org.ruminaq.gui.model.diagram.PortShape;
import org.ruminaq.gui.model.diagram.RuminaqDiagram;
import org.ruminaq.model.ruminaq.Port;

public abstract class AbstractAddPortFeature extends AbstractAddElementFeature {

  public AbstractAddPortFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canAdd(IAddContext context) {
    return context.getTargetContainer() instanceof RuminaqDiagram;
  }
  
  public PictogramElement add(IAddContext context, PortShape portShape) {
    Port port = (Port) context.getNewObject();
    portShape.setContainer(context.getTargetContainer());
    portShape.setX(context.getX());
    portShape.setY(context.getY());
    IPeCreateService peCreateService = Graphiti.getPeCreateService();
    peCreateService.createChopboxAnchor(portShape);

    ContainerShape labelCS = addLabel(context.getTargetContainer(),
        port.getId(), portShape.getWidth(), portShape.getHeight(),
        context.getX(), context.getY());
    link(portShape, new Object[] { port, labelCS });
    link(labelCS, new Object[] { port, portShape });
    updatePictogramElement(labelCS);
    layoutPictogramElement(labelCS);
    return portShape;
  }
}
