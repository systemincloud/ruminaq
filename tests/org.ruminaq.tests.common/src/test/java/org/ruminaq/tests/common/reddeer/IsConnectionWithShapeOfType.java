package org.ruminaq.tests.common.reddeer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import org.eclipse.graphiti.ui.internal.figures.GFAbstractShape;
import org.eclipse.graphiti.ui.internal.figures.GFPolylineConnection;
import org.eclipse.graphiti.ui.internal.parts.ConnectionEditPart;
import org.eclipse.graphiti.ui.internal.parts.IPictogramElementDelegate;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class IsConnectionWithShapeOfType
    extends BaseMatcher<ConnectionEditPart> {

  private Class<?> clazz;

  /**
   * Constructs a matcher with a given class.
   *
   * @param label Label
   */
  public IsConnectionWithShapeOfType(Class<?> clazz) {
    this.clazz = clazz;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.hamcrest.Matcher#matches(java.lang.Object)
   */
  @Override
  public boolean matches(Object obj) {
    return Optional.of(obj).filter(GFPolylineConnection.class::isInstance)
        .map(GFPolylineConnection.class::cast)
        .map((GFPolylineConnection gc) -> {
          try {
            Method m = GFAbstractShape.class
                .getDeclaredMethod("getPictogramElementDelegate");
            m.setAccessible(true);
            return m.invoke(gc);
          } catch (NoSuchMethodException | SecurityException
              | IllegalAccessException | IllegalArgumentException
              | InvocationTargetException e) {
            return null;
          }
        }).filter(IPictogramElementDelegate.class::isInstance)
        .map(IPictogramElementDelegate.class::cast)
        .map(IPictogramElementDelegate::getPictogramElement)
        .filter(clazz::isInstance).isPresent();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.hamcrest.SelfDescribing#describeTo(org.hamcrest.Description)
   */
  @Override
  public void describeTo(Description description) {
    description.appendText(
        "is ConnectionEditPart linked to '" + clazz.getCanonicalName() + "'");
  }

}
