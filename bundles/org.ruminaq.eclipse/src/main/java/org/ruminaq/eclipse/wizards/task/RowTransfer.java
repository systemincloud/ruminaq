/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.task;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * Helper class for drag and drop.
 *
 * @author Marek Jagielski
 */
class RowTransfer extends ByteArrayTransfer {

  private static final String ROWTYPENAME = "RowType";
  private static final int ROWTYPEID = registerType(ROWTYPENAME);
  private static final RowTransfer instance = new RowTransfer();

  public static RowTransfer getInstance() {
    return instance;
  }

  @Override
  protected String[] getTypeNames() {
    return new String[] { ROWTYPENAME };
  }

  @Override
  protected int[] getTypeIds() {
    return new int[] { ROWTYPEID };
  }

  @Override
  public void javaToNative(Object object, TransferData transferData) {
  }

  @Override
  public Object nativeToJava(TransferData transferData) {
    return null;
  }
}
