/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.debug.model;

import org.ruminaq.debug.api.dispatcher.IEventProcessor;

public interface ISicTarget extends IEventProcessor {
  void setState(IState state);

  void fireTerminateEvent();
}
