/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.runner.impl.data;

import java.util.LinkedList;

import org.ruminaq.runner.thrift.RemoteData;

public class ControlI extends DataI {

  private static final long serialVersionUID = 1L;

  public ControlI() {
    super(new LinkedList<Integer>() {
      private static final long serialVersionUID = 1L;
      {
        add(1);
      }
    });
  }

  public ControlI(RemoteData data) {
    super(data.getDims());
  }

  @Override
  public RemoteData getRemoteData() {
    RemoteData rd = new RemoteData();
    rd.dims = this.dims;
    rd.type = "Control";
    return rd;
  }

  @Override
  protected DataI to(Class<? extends DataI> clazz) {
    if (ControlI.class.isAssignableFrom(clazz))
      return this;
    else if (TextI.class.isAssignableFrom(clazz))
      return toTextI();
    return from(clazz);
  }

  //
  // Converters
  //

  public DataI toTextI() {
    return new TextI("*");
  }
}
