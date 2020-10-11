/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.model.ruminaq.dt;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.model.ruminaq.DataExtension;
import org.ruminaq.model.ruminaq.DataType;

/**
 * Service DataExtension implementation.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=5" })
public class BoolImpl implements DataExtension {

  @Override
  public List<Class<? extends DataType>> getDataTypes() {
    return Arrays.asList(Bool.class, Complex32.class, Complex64.class,
        Control.class, Decimal.class, Int32.class, Int64.class, Float32.class,
        Float64.class, Raw.class, Text.class);
  }

  @Override
  public Optional<DataType> getDataTypeFromName(String name) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean canCastFromTo(Class<? extends DataType> from,
      Class<? extends DataType> to) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isLossyCastFromTo(Class<? extends DataType> from,
      Class<? extends DataType> to) {
    // TODO Auto-generated method stub
    return false;
  }
}
