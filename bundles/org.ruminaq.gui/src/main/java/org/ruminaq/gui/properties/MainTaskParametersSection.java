/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.properties;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.model.ruminaq.MainTask;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.model.ruminaq.Parameter;
import org.ruminaq.model.ruminaq.RuminaqFactory;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.util.GlobalUtil;
import ch.qos.logback.classic.Logger;

/**
 * Retrieve parameters from whole diagram.
 *
 * @author Marek Jagielski
 */
public class MainTaskParametersSection extends AbstractParametersSection {

  private static final Logger LOGGER = ModelerLoggerFactory
      .getLogger(MainTaskParametersSection.class);

  private MainTask getMainTask() {
    return getRuminaqDiagram().getMainTask();
  }

  @Override
  protected Collection<Parameter> getParameters() {
    List<String> shouldBe = getMainTask().getTask().stream()
        .map(Task::getParameter).flatMap(Collection::stream)
        .map(Parameter::getCurrentValue)
        .map(GlobalUtil.PARAMETER_PATTERN::matcher).flatMap(Matcher::results)
        .map(MatchResult::group).distinct()
        .map(s -> s.substring(GlobalUtil.PARAMETER_PREFIX.length(),
            s.length() - GlobalUtil.PARAMETER_SUFFIX.length()))
        .collect(Collectors.toList());
    Set<String> is = getMainTask().getParameters().keySet();
    LOGGER.trace("should be {}", shouldBe.toArray());
    LOGGER.trace("is {}", is.toArray());
    List<String> toRemove = is.stream()
        .filter(Predicate.not(shouldBe::contains)).collect(Collectors.toList());
    List<String> toAdd = shouldBe.stream().filter(Predicate.not(is::contains))
        .collect(Collectors.toList());

    ModelUtil.runModelChange(() -> {
      toRemove.stream().forEach(getMainTask().getParameters()::remove);
      toAdd.stream().forEach(s -> getMainTask().getParameters().put(s, ""));
    }, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
        "Update parameters");

    return getMainTask().getParameters().entrySet().stream()
        .map((Entry<String, String> e) -> {
          Parameter p = RuminaqFactory.eINSTANCE.createParameter();
          p.setKey(e.getKey());
          p.setValue(e.getValue());
          p.setDefaultValue("");
          p.setDefault("".equals(e.getValue()));
          return p;
        }).collect(Collectors.toList());
  }

  @Override
  protected void saveParameter(final String key, final String value) {
    ModelUtil.runModelChange(
        () -> getMainTask().getParameters().put(key, value),
        getDiagramContainer().getDiagramBehavior().getEditingDomain(),
        "Change parameter");
  }
}
