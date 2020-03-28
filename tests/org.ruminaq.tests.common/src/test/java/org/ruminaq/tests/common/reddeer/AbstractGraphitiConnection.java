package org.ruminaq.tests.common.reddeer;

import org.eclipse.graphiti.ui.internal.parts.ConnectionEditPart;
import org.eclipse.reddeer.gef.impl.connection.AbstractConnection;
import org.hamcrest.Matcher;

public class AbstractGraphitiConnection extends AbstractConnection {

  /**
   * Constructs graphiti edit part which fulfills a given matcher.
   * 
   * @param matcher matcher to match edit part
   * @param index index of edit part
   */
  public AbstractGraphitiConnection(Matcher<ConnectionEditPart> matcher, int index) {
    super(matcher, index);
  }

}
