/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.add;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeService;
import org.ruminaq.gui.GuiUtil;
import org.ruminaq.gui.LabelUtil;

/**
 * Abstract class for all Ruminaque elements.
 * Elements can have labels assiociated.
 *
 * @author Marek Jagielski
 */
public abstract class AbstractAddElementFeature
    extends AbstractAddShapeFeature {

  public AbstractAddElementFeature(IFeatureProvider fp) {
    super(fp);
  }

  protected ContainerShape addLabel(ContainerShape targetContainer,
      String label, int width, int height, int shapeX, int shapeY) {
    IPeService peService = Graphiti.getPeService();
    IGaService gaService = Graphiti.getGaService();

    ContainerShape textContainerShape = peService
        .createContainerShape(targetContainer, true);

    Rectangle r = gaService.createInvisibleRectangle(textContainerShape);
    MultiText text = gaService.createDefaultMultiText(getDiagram(), r, label);
    text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
    text.setVerticalAlignment(Orientation.ALIGNMENT_TOP);

    GuiUtil.alignWithShape(text, textContainerShape, width, height, shapeX,
        shapeY, 0, 0);

    LabelUtil.setLabel(textContainerShape);

    return textContainerShape;
  }
}
