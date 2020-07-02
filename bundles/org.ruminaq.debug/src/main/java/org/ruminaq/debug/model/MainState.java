/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.debug.model;

public enum MainState implements IState {
  NOT_STARTED(""), RUNNING("running"), SUSPENDED("suspended"),
  STEPPING("stepping"), TERMINATED("terminated");

  private final String name;

  MainState(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
