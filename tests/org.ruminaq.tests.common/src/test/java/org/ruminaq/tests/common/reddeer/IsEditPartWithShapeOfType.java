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

public class IsEditPartWithShapeOfType extends BaseMatcher<EditPart> {

  private Class<?> clazz;

  /**
   * Constructs a matcher with a given class.
   *
   * @param label Label
   */
  public IsEditPartWithShapeOfType(Class<?> clazz) {
    this.clazz = clazz;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.hamcrest.Matcher#matches(java.lang.Object)
   */
  @Override
  public boolean matches(Object obj) {
    return Optional.of(obj).filter(ContainerShapeEditPart.class::isInstance)
        .map(ContainerShapeEditPart.class::cast)
        .map(ContainerShapeEditPart::getPictogramElement)
        .filter(clazz::isInstance).map(clazz::cast).isPresent();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.hamcrest.SelfDescribing#describeTo(org.hamcrest.Description)
   */
  @Override
  public void describeTo(Description description) {
    description
        .appendText("is EditPart linked to '" + clazz.getCanonicalName() + "'");
  }

}
