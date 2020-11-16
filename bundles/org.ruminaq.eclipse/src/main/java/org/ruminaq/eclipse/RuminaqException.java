/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse;

/**
 * Main bundle exception.
 *
 * @author Marek Jagielski
 */
public class RuminaqException extends RuntimeException {

  public RuminaqException(String msg, Throwable e) {
    super(msg, e);
  }

  public RuminaqException(String msg) {
    super(msg);
  }
}
