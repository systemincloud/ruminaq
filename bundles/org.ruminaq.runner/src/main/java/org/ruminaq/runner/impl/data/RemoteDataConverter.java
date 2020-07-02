/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.runner.impl.data;

import java.util.Optional;

import org.ruminaq.runner.service.RunnerServiceManager;
import org.ruminaq.runner.thrift.RemoteData;

public enum RemoteDataConverter {
  INSTANCE;

  public RemoteData toRemoteData(DataI dataI) {
    if (dataI == null) {
      RemoteData empty = new RemoteData();
      empty.type = "";
      return empty;
    }
    Optional<RemoteData> data = RunnerServiceManager.INSTANCE
        .toRemoteData(dataI);
    if (data.isPresent())
      return data.get();

    return dataI.getRemoteData();
  }

  public DataI fromRemoteData(RemoteData data) {

    Optional<DataI> dataI = RunnerServiceManager.INSTANCE.fromRemoteData(data);
    if (dataI.isPresent())
      return dataI.get();

    else if ("Bool".equals(data.getType()))
      return new BoolI(data);
    else if ("Complex32".equals(data.getType()))
      return new Complex32I(data);
    else if ("Complex64".equals(data.getType()))
      return new Complex64I(data);
    else if ("Control".equals(data.getType()))
      return new ControlI();
    else if ("Decimal".equals(data.getType()))
      return new DecimalI(data);
    else if ("Float32".equals(data.getType()))
      return new Float32I(data);
    else if ("Float64".equals(data.getType()))
      return new Float64I(data);
    else if ("Int32".equals(data.getType()))
      return new Int32I(data);
    else if ("Int64".equals(data.getType()))
      return new Int64I(data);
    else if ("Raw".equals(data.getType()))
      return new RawI(data);
    else if ("Text".equals(data.getType()))
      return new TextI(data);

    return null;
  }
}
