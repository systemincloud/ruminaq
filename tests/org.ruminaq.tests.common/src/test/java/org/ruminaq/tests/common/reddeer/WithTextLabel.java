package org.ruminaq.tests.common.reddeer;

import org.eclipse.reddeer.graphiti.impl.graphitieditpart.AbstractGraphitiEditPart;

public class WithTextLabel extends AbstractGraphitiEditPart {

  public WithTextLabel(String text) {
    this(text, 0);
  }

  public WithTextLabel(String text, int index) {
    super(new IsLabelWithText(text), index);
  }

}
