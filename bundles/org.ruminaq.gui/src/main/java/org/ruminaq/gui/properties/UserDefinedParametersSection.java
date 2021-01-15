/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.properties;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.model.ruminaq.Parameter;
import org.ruminaq.model.ruminaq.UserDefinedTask;

public class UserDefinedParametersSection extends AbstractParametersSection {

  private UserDefinedTask bo;

  private TransactionalEditingDomain ed;

  @Override
  protected boolean isDefault() {
    return true;
  }

  @Override
  public void refresh() {
    this.bo = Optional.ofNullable(getSelectedPictogramElement())
        .filter(RuminaqShape.class::isInstance).map(RuminaqShape.class::cast)
        .map(RuminaqShape::getModelObject)
        .filter(UserDefinedTask.class::isInstance)
        .map(UserDefinedTask.class::cast).get();
    super.refresh();
  }

  @Override
  protected Map<String, String> getActualParams() {
    return bo.getParameter().stream()
        .collect(Collectors.toMap(Parameter::getKey, Parameter::getValue));
  }

  @Override
  protected Map<String, String> getDefaultParams() {
    return bo.getParameter().stream().collect(
        Collectors.toMap(Parameter::getKey, Parameter::getDefaultValue));
  }

  @Override
  protected void saveParameter(final String key, final String value) {
    ModelUtil.runModelChange(new Runnable() {
      public void run() {
        if (bo == null)
          return;
        bo.getParameter().stream().filter(p -> p.getKey().equals(key))
            .findFirst().ifPresent(p -> p.setValue(value));
      }
    }, ed, "Change parameter");
  }
}
