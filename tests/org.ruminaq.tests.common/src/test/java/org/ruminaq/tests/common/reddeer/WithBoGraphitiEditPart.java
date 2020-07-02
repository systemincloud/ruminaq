/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
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
