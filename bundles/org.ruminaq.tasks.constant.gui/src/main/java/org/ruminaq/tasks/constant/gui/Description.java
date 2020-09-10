/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.constant.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.PropertyDescriptionExtension;
import org.ruminaq.tasks.constant.gui.Description.Filter;
import org.ruminaq.tasks.constant.model.constant.Constant;
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
          .map(l -> l.get(0)).filter(Constant.class::isInstance).isPresent();
    }
  }

  @Override
  public String getDescription() {
    Bundle bundle = FrameworkUtil.getBundle(Description.class);
    URL url = bundle.getEntry("docs/Constant.md");
    try {
      InputStream inputStream = url.openConnection().getInputStream();
      String text = new BufferedReader(
          new InputStreamReader(inputStream, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));
      return text;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return "";
  }

}
