/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.runner.impl.cmd;

import org.ruminaq.runner.impl.InternalInputPortI;

public interface Command {
  void execute(InternalInputPortI internalInputPortI);
}
