/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.runner.impl.debug.events;

public abstract class AbstractEvent implements IDebugEvent {

  private static final long serialVersionUID = 1L;

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

  @Override
  public void preevaluate() {
  };
}
