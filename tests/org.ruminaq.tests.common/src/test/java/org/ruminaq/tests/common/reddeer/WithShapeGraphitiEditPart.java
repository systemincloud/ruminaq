package org.ruminaq.tests.common.reddeer;

import org.eclipse.reddeer.graphiti.impl.graphitieditpart.AbstractGraphitiEditPart;

public class WithShapeGraphitiEditPart extends AbstractGraphitiEditPart {

  /**
   * Finds a graphiti edit part with shape of given class.
   *
   * @param clazz
   */
  public WithShapeGraphitiEditPart(Class<?> clazz) {
    this(clazz, 0);
  }

  /**
   * Finds a graphiti edit part with shape of given class at the specified
   * index.
   *
   * @param clazz
   * @param index
   */
  public WithShapeGraphitiEditPart(Class<?> clazz, int index) {
    super(new IsEditPartWithShapeOfType(clazz), index);
  }

}
