/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.javatask.gui;

import java.util.Arrays;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.ruminaq.tasks.javatask.client.data.Bool;
import org.ruminaq.tasks.javatask.client.data.Complex32;
import org.ruminaq.tasks.javatask.client.data.Complex64;
import org.ruminaq.tasks.javatask.client.data.Control;
import org.ruminaq.tasks.javatask.client.data.Data;
import org.ruminaq.tasks.javatask.client.data.Decimal;
import org.ruminaq.tasks.javatask.client.data.Float32;
import org.ruminaq.tasks.javatask.client.data.Float64;
import org.ruminaq.tasks.javatask.client.data.Int32;
import org.ruminaq.tasks.javatask.client.data.Int64;
import org.ruminaq.tasks.javatask.client.data.Raw;
import org.ruminaq.tasks.javatask.client.data.Text;
import org.ruminaq.tasks.javatask.gui.api.JavaTaskExtension;

/**
 * Service JavaTaskExtension implementation.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=100" })
public class JavaTaskExtensionImpl implements JavaTaskExtension {

  @Override
  public List<Class<? extends Data>> getDataTypes() {
    return Arrays.asList(Bool.class, Complex32.class, Complex64.class,
        Control.class, Decimal.class, Int32.class, Int64.class, Float32.class,
        Float64.class, Raw.class, Text.class);
  }
}
