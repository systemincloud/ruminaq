/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.randomgenerator.gui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.PropertyDescriptionExtension;
import org.ruminaq.tasks.randomgenerator.gui.Description.Filter;
import org.ruminaq.tasks.randomgenerator.model.randomgenerator.RandomGenerator;
import org.ruminaq.util.Result;
import org.ruminaq.util.ServiceFilter;
import org.ruminaq.util.ServiceFilterArgs;

@Component(property = { "service.ranking:Integer=5" })
@ServiceFilter(Filter.class)
public class Description implements PropertyDescriptionExtension {

  /**
   * Only on Constant.
   */
  public static class Filter implements Predicate<ServiceFilterArgs> {

    @Override
    public boolean test(ServiceFilterArgs args) {
      return Optional.ofNullable(args).map(ServiceFilterArgs::getArgs)
          .map(l -> l.get(0)).filter(RandomGenerator.class::isInstance).isPresent();
    }
  }

  @Override
  public String getDescription() {
    return Optional.of(FrameworkUtil.getBundle(Description.class))
        .map(b -> b.getEntry("docs/RandomGenerator.md"))
        .map(url -> Result.attempt(() -> url.openConnection()))
        .map(r -> r.orElse(null)).filter(Objects::nonNull)
        .map(urlConn -> Result.attempt(() -> urlConn.getInputStream()))
        .map(r -> r.orElse(null)).filter(Objects::nonNull)
        .map(is -> new BufferedReader(
            new InputStreamReader(is, StandardCharsets.UTF_8)))
        .map(BufferedReader::lines).orElseGet(Stream::empty)
        .collect(Collectors.joining("\n"));
  }

}
