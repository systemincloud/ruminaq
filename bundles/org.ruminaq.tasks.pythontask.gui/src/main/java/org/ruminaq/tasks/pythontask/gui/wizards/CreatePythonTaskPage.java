/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.pythontask.gui.wizards;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.ruminaq.eclipse.usertask.model.userdefined.Module;
import org.ruminaq.eclipse.wizards.task.AbstractCreateUserDefinedTaskPage;
import org.ruminaq.tasks.pythontask.gui.PythonData;
import org.ruminaq.tasks.pythontask.gui.api.PythonTaskExtension;
import org.ruminaq.util.ServiceUtil;

public class CreatePythonTaskPage extends AbstractCreateUserDefinedTaskPage {

  public CreatePythonTaskPage(String pageName) {
    super(pageName);
    setTitle("Ruminaq - Python Task");
    setDescription("Here you can describe your task features");
  }

  @Override
  protected List<String> getDataTypes() {
    return ServiceUtil
        .getServicesAtLatestVersion(CreatePythonTaskPage.class,
            PythonTaskExtension.class)
        .stream().map(PythonTaskExtension::getDataTypes).map(Map::keySet)
        .flatMap(Set::stream).collect(Collectors.toList());
  }

  @Override
  public Module getModel() {
    Module m = super.getModel();
    for (Pair<String, String> p : PythonData.INSTANCE.getPythonTaskDatas())
      m.getImportPrefix().put(p.getValue1(), p.getValue0());
    return m;
  }

  public String generate(Module module) {
    return CodeGenerator.generate(module);
  }
}
