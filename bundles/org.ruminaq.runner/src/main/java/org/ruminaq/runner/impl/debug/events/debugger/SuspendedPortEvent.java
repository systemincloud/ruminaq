/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.runner.impl.debug.events.debugger;

import org.eclipse.debug.core.DebugEvent;
import org.ruminaq.runner.impl.debug.events.AbstractPortEvent;
import org.ruminaq.runner.impl.debug.events.AbstractPortEventListener;
import org.ruminaq.runner.impl.debug.events.IDebuggerEvent;

public class SuspendedPortEvent extends AbstractPortEvent
    implements IDebuggerEvent {

  private static final long serialVersionUID = 1L;

  public enum Type {
    CLIENT(DebugEvent.CLIENT_REQUEST), BREAKPOINT(DebugEvent.BREAKPOINT),
    STEP_OVER(DebugEvent.STEP_OVER), INIT(DebugEvent.UNSPECIFIED);

    private int debugType;

    public int getDebugType() {
      return debugType;
    }

    Type(int debugType) {
      this.debugType = debugType;
    }
  }

  private final Type type;

  public Type getType() {
    return type;
  }

  public SuspendedPortEvent(Type type, AbstractPortEventListener apel) {
    super(apel);
    this.type = type;
  }
}
