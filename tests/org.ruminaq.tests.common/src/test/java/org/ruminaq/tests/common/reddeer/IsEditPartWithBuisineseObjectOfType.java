package org.ruminaq.tests.common.reddeer;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class IsEditPartWithBuisineseObjectOfType extends BaseMatcher<EditPart> {

  private Class<?> clazz;

  /**
   * Constructs a matcher with a given class.
   *
   * @param label
   *            Label
   */
  public IsEditPartWithBuisineseObjectOfType(Class<?> clazz) {
    this.clazz = clazz;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.hamcrest.Matcher#matches(java.lang.Object)
   */
  @Override
  public boolean matches(Object obj) {
    if (obj instanceof GraphicalEditPart) {
      GraphicalEditPart gep = (GraphicalEditPart) obj;
      if (gep.isSelectable()) {
      }
    }
    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.hamcrest.SelfDescribing#describeTo(org.hamcrest.Description)
   */
  @Override
  public void describeTo(Description description) {
    description.appendText("is EditPart linked to '" + clazz.getCanonicalName() + "'");
  }

}
