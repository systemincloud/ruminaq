/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui;

import java.util.Arrays;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.DataExtension;
import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.model.ruminaq.dt.Bool;
import org.ruminaq.model.ruminaq.dt.Complex32;
import org.ruminaq.model.ruminaq.dt.Complex64;
import org.ruminaq.model.ruminaq.dt.Control;
import org.ruminaq.model.ruminaq.dt.Decimal;
import org.ruminaq.model.ruminaq.dt.Float32;
import org.ruminaq.model.ruminaq.dt.Float64;
import org.ruminaq.model.ruminaq.dt.Int32;
import org.ruminaq.model.ruminaq.dt.Int64;
import org.ruminaq.model.ruminaq.dt.Raw;
import org.ruminaq.model.ruminaq.dt.Text;

/**
 * Service DataExtension implementation.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=5" })
public class DataExtensionImpl implements DataExtension {

  @Override
  public List<Class<? extends DataType>> getDataTypes() {
    return Arrays.asList(Bool.class, Complex32.class, Complex64.class,
        Control.class, Decimal.class, Int32.class, Int64.class, Float32.class,
        Float64.class, Raw.class, Text.class);
  }
}
