/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.properties;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.model.ruminaq.Parameter;
import org.ruminaq.model.ruminaq.UserDefinedTask;

/**
 * 
 * @author Marek Jagielski
 */
public class UserDefinedParametersSection extends AbstractParametersSection {

  private Optional<UserDefinedTask> model() {
    return Optional.ofNullable(getSelectedPictogramElement())
        .filter(RuminaqShape.class::isInstance).map(RuminaqShape.class::cast)
        .map(RuminaqShape::getModelObject)
        .filter(UserDefinedTask.class::isInstance)
        .map(UserDefinedTask.class::cast);
  }

  private Stream<Parameter> parameters() {
    return model().map(UserDefinedTask::getParameter).map(List::stream)
        .orElseGet(Stream::empty);
  }

  @Override
  protected boolean isDefault() {
    return true;
  }

  @Override
  protected Map<String, String> getActualParams() {
    return parameters()
        .collect(Collectors.toMap(Parameter::getKey, Parameter::getValue));
  }

  @Override
  protected Map<String, String> getDefaultParams() {
    return parameters().collect(
        Collectors.toMap(Parameter::getKey, Parameter::getDefaultValue));
  }

  @Override
  protected void saveParameter(final String key, final String value) {
    ModelUtil.runModelChange(
        () -> model().get().getParameter().stream()
            .filter(p -> p.getKey().equals(key)).findFirst()
            .ifPresent(p -> p.setValue(value)),
        getDiagramContainer().getDiagramBehavior().getEditingDomain(),
        "Change parameter");
  }
}
