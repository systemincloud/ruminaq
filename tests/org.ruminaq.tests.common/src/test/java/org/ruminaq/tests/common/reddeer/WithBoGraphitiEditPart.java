package org.ruminaq.tests.common.reddeer;

public class WithBoGraphitiEditPart extends AbstractExtendedGraphitiEditPart {

  /**
   * Finds a graphiti edit part with bo of given class.
   *
   * @param clazz
   */
  public WithBoGraphitiEditPart(Class<?> clazz) {
    this(clazz, 0);
  }

  /**
   * Finds a graphiti edit part with bo of given class at the specified index.
   *
   * @param clazz
   * @param index
   */
  public WithBoGraphitiEditPart(Class<?> clazz, int index) {
    super(new IsEditPartWithBuisineseObjectOfType(clazz), index);
  }

}
