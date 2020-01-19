/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.add;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.ruminaq.gui.model.diagram.DiagramFactory;
import org.ruminaq.gui.model.diagram.LabelShape;
import org.ruminaq.gui.model.diagram.LabeledRuminaqShape;
import org.ruminaq.gui.model.diagram.impl.label.LabelUtil;

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

  protected LabelShape addLabel(LabeledRuminaqShape labeledShape) {
    LabelShape labelShape = DiagramFactory.eINSTANCE.createLabelShape(); 
    labeledShape.setLabel(labelShape);
    labelShape.setContainer(labeledShape.getContainer());
    LabelUtil.placeInDefaultPosition(labelShape);
    return labelShape;
  }
}
