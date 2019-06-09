/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse;

public class RuminaqException extends Exception {

  public RuminaqException(String msg) {
    super(msg);
  }

  public RuminaqException(String msg, Throwable e) {
    super(msg, e);
  }
}
