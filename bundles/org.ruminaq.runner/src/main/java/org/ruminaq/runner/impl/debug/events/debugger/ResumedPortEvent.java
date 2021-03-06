/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.runner.impl.debug.events.debugger;

import org.ruminaq.runner.impl.debug.events.AbstractPortEvent;
import org.ruminaq.runner.impl.debug.events.AbstractPortEventListener;
import org.ruminaq.runner.impl.debug.events.IDebuggerEvent;
import org.ruminaq.runner.impl.debug.events.model.ResumePortRequest;

public class ResumedPortEvent extends AbstractPortEvent
    implements IDebuggerEvent {

  private static final long serialVersionUID = 1L;

  public enum Type {
    NORMAL, STEP_OVER;

    public static Type get(ResumePortRequest.Type type) {
      switch (type) {
        case NORMAL:
          return NORMAL;
        case STEP_OVER:
          return STEP_OVER;
        default:
          return NORMAL;
      }
    }
  };

  private final Type type;

  public Type getType() {
    return type;
  }

  public ResumedPortEvent(Type type, AbstractPortEventListener apel) {
    super(apel);
    this.type = type;
  }
}
