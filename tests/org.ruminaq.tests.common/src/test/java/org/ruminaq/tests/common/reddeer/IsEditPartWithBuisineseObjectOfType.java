package org.ruminaq.tests.common.reddeer;

import java.util.Optional;

import org.eclipse.gef.EditPart;
import org.eclipse.graphiti.ui.internal.parts.ContainerShapeEditPart;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.ruminaq.gui.model.diagram.RuminaqShape;

public class IsEditPartWithBuisineseObjectOfType extends BaseMatcher<EditPart> {

  private Class<?> clazz;

  /**
   * Constructs a matcher with a given class.
   *
   * @param label Label
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
    return Optional.of(obj).filter(ContainerShapeEditPart.class::isInstance)
        .map(ContainerShapeEditPart.class::cast)
        .map(ContainerShapeEditPart::getPictogramElement)
        .filter(RuminaqShape.class::isInstance).map(RuminaqShape.class::cast)
        .map(RuminaqShape::getModelObject)
        .filter(o -> clazz.isInstance(o)).isPresent();
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
