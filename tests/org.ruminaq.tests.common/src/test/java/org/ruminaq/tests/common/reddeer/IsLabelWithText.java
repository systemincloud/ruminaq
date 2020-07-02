/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tests.common.reddeer;

import java.util.Optional;

import org.eclipse.gef.EditPart;
import org.eclipse.graphiti.ui.internal.parts.ContainerShapeEditPart;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.ruminaq.gui.model.diagram.LabelShape;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.model.ruminaq.BaseElement;

public class IsLabelWithText extends BaseMatcher<EditPart> {

  private String text;

  public IsLabelWithText(String text) {
    this.text = text;
  }

  @Override
  public boolean matches(Object obj) {
    return Optional.of(obj).filter(ContainerShapeEditPart.class::isInstance)
        .map(ContainerShapeEditPart.class::cast)
        .map(ContainerShapeEditPart::getPictogramElement)
        .filter(LabelShape.class::isInstance).map(LabelShape.class::cast)
        .map(LabelShape::getLabeledShape).map(RuminaqShape::getModelObject)
        .map(BaseElement::getId).filter(text::equals).isPresent();
  }

  @Override
  public void describeTo(Description arg0) {
    // TODO Auto-generated method stub

  }

}
