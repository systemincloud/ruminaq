/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.javatask.gui;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.UserDefinedTaskExtension;
import org.ruminaq.tasks.javatask.client.JavaTask;
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
import org.ruminaq.util.ServiceFilter;
import org.ruminaq.util.ServiceFilterArgs;

/**
 * Service UserDefinedTaskExtension implementation
 * for JavaTask.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=5" })
@ServiceFilter(UserDefinedTaskExtensionImpl.Filter.class)
public class UserDefinedTaskExtensionImpl implements UserDefinedTaskExtension {

  private static final List<Class<? extends Data>> SUPPORTED_DATA = Arrays
      .asList(Bool.class, Complex32.class, Complex64.class, Control.class,
          Decimal.class, Int32.class, Int64.class, Float32.class, Float64.class,
          Raw.class, Text.class);

  private static class Filter implements Predicate<ServiceFilterArgs> {

    @Override
    public boolean test(ServiceFilterArgs args) {
      return Optional.ofNullable(args).map(ServiceFilterArgs::getArgs)
          .map(List::stream).orElseGet(Stream::empty)
          .anyMatch(JavaTask.class::equals);
    }
  }

  @Override
  public List<String> getSupportedData() {
    return SUPPORTED_DATA.stream().map(Class::getSimpleName)
        .collect(Collectors.toList());
  }

  @Override
  public String getCannonicalDataName(String dataName) {
    return SUPPORTED_DATA.stream()
        .filter(dt -> dt.getSimpleName().equals(dataName)).findFirst()
        .map(Class::getCanonicalName).orElse(null);
  }
}
