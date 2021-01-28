/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.launch.ui;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.ruminaq.eclipse.RuminaqDiagramUtil;

public class InTestDirectoryPropertyTester extends PropertyTester {

  private static final String PROPERTY_NAME = "inTestDir";

  @Override
  public boolean test(Object receiver, String property, Object[] arg2,
      Object expectedValue) {
    if (property.equals(PROPERTY_NAME) && receiver instanceof IFile)
      return RuminaqDiagramUtil.isTest(((IFile) receiver));
    return false;
  }
}
