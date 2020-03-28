package org.ruminaq.tests.common.reddeer;

public class WithShapeGraphitiConnection extends AbstractGraphitiConnection {

  /**
   * Finds a graphiti edit part with shape of given class.
   *
   * @param clazz
   */
  public WithShapeGraphitiConnection(Class<?> clazz) {
    this(clazz, 0);
  }

  /**
   * Finds a graphiti edit part with shape of given class at the specified
   * index.
   *
   * @param clazz
   * @param index
   */
  public WithShapeGraphitiConnection(Class<?> clazz, int index) {
    super(new IsConnectionWithShapeOfType(clazz), index);
  }

}
