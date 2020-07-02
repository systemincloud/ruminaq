/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.runner.impl.debug.events;

public abstract class AbstractPortEvent extends AbstractEvent
    implements IPortEvent {

  private static final long serialVersionUID = 1L;

  private String diagramPath;
  private String taskId;
  private String portId;

  public String getDiagramPath() {
    return diagramPath;
  }

  public String getTaskId() {
    return taskId;
  }

  public String getPortId() {
    return portId;
  }

  public AbstractPortEvent(AbstractPortEventListener apel) {
    this.diagramPath = apel.getDiagramPath();
    this.taskId = apel.getTaskId();
    this.portId = apel.getPortId();
  }

  public boolean compare(AbstractPortEventListener apel) {
    return this.diagramPath.equals(apel.getDiagramPath())
        && this.taskId.equals(apel.getTaskId())
        && this.portId.equals(apel.getPortId());
  }
}
