package org.ruminaq.tests.common.reddeer;

import org.eclipse.reddeer.graphiti.impl.graphitieditpart.AbstractGraphitiEditPart;

public class WithLabelAssociated extends AbstractGraphitiEditPart {

  public WithLabelAssociated(String text) {
    this(text, 0);
  }

  public WithLabelAssociated(String text, int index) {
    super(new HasLabelWithText(text), index);
  }

}
