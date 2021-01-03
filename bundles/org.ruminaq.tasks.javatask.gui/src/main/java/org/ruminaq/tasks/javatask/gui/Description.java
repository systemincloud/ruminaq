/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.javatask.gui;

import java.util.Optional;
import java.util.function.Predicate;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.PropertyDescriptionExtension;
import org.ruminaq.gui.properties.MarkdownDescription;
import org.ruminaq.tasks.javatask.model.javatask.JavaTask;
import org.ruminaq.util.ServiceFilter;
import org.ruminaq.util.ServiceFilterArgs;

/**
 * Service PropertyDescriptionExtension implementation.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=5" })
@ServiceFilter(Description.Filter.class)
public class Description extends MarkdownDescription
    implements PropertyDescriptionExtension {

  private static class Filter implements Predicate<ServiceFilterArgs> {

    @Override
    public boolean test(ServiceFilterArgs args) {
      return Optional.ofNullable(args).map(ServiceFilterArgs::getArgs)
          .map(l -> l.get(0)).filter(JavaTask.class::isInstance).isPresent();
    }
  }

  @Override
  public String getDescription() {
    return getEntry(Description.class, "docs/JavaTask.md");
  }

}
