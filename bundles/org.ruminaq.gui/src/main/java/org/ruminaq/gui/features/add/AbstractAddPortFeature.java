/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.add;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.ruminaq.gui.features.styles.PortStyle;
import org.ruminaq.model.ruminaq.Port;

public abstract class AbstractAddPortFeature extends AbstractAddElementFeature {

  public AbstractAddPortFeature(IFeatureProvider fp) {
    super(fp);
  }

  protected abstract int getWidth();

  protected abstract LineStyle getLineStyle(Port port);

  @Override
  public PictogramElement add(IAddContext context) {
    final Port port = (Port) context.getNewObject();
    ContainerShape targetContainer = context.getTargetContainer();

    final IPeCreateService peCreateService = Graphiti.getPeCreateService();

    final ContainerShape containerShape = peCreateService
        .createContainerShape(context.getTargetContainer(), true);
    final IGaService gaService = Graphiti.getGaService();

    int width = 30;
    int height = 15;

    final Rectangle invisibleRectangle = gaService
        .createInvisibleRectangle(containerShape);
    gaService.setLocationAndSize(invisibleRectangle, context.getX(),
        context.getY(), width, height);

    // create and set visible rectangle inside invisible rectangle
    RoundedRectangle roundedRectangle = gaService
        .createRoundedRectangle(invisibleRectangle, 20, 20);
    roundedRectangle.setParentGraphicsAlgorithm(invisibleRectangle);
    roundedRectangle.setStyle(PortStyle.getStyle(getDiagram()));
    roundedRectangle.setLineStyle(getLineStyle(port));
    roundedRectangle.setLineWidth(getWidth());
    gaService.setLocationAndSize(roundedRectangle, 0, 0, width, height);

    int x = context.getX();
    int y = context.getY();

    ContainerShape labelCS = addLabel(targetContainer, port.getId(), width,
        height, x, y);
    link(containerShape, new Object[] { port, labelCS });
    link(labelCS, new Object[] { port, containerShape });

    peCreateService.createChopboxAnchor(containerShape);

    updatePictogramElement(labelCS);
    layoutPictogramElement(labelCS);

    return containerShape;
  }
}
