/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.runner.impl.debug.events.model;

import org.ruminaq.runner.impl.debug.events.AbstractPortEvent;
import org.ruminaq.runner.impl.debug.events.AbstractPortEventListener;
import org.ruminaq.runner.impl.debug.events.IModelRequest;

public class ResumePortRequest extends AbstractPortEvent
    implements IModelRequest {

  private static final long serialVersionUID = 1L;

  public enum Type {
    NORMAL, STEP_OVER
  }

  private Type type;

  public Type getType() {
    return type;
  }

  public ResumePortRequest(AbstractPortEventListener apel) {
    this(Type.NORMAL, apel);
  }

  public ResumePortRequest(Type type, AbstractPortEventListener apel) {
    super(apel);
    this.type = type;
  }
}
