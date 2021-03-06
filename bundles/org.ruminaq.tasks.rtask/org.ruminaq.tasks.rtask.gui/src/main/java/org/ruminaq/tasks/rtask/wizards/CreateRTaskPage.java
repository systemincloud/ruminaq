/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.rtask.wizards;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.ruminaq.eclipse.usertask.model.userdefined.Module;
import org.ruminaq.eclipse.wizards.task.AbstractCreateUserDefinedTaskPage;
import org.ruminaq.tasks.rtask.RData;
import org.ruminaq.tasks.rtask.api.RTaskExtension;
import org.ruminaq.tasks.rtask.ui.wizards.ICreateRTaskPage;
import org.ruminaq.util.ServiceUtil;

public class CreateRTaskPage extends AbstractCreateUserDefinedTaskPage
    implements ICreateRTaskPage {

  public CreateRTaskPage(String pageName) {
    super(pageName);
    setTitle("System in Cloud - R Task");
    setDescription("Here you can describe your task features");
  }

  @Override
  public List<String> getDataTypes() {
    return ServiceUtil
        .getServicesAtLatestVersion(CreateRTaskPage.class,
            RTaskExtension.class)
        .stream().map(RTaskExtension::getDataTypes).map(Map::keySet)
        .flatMap(Set::stream).collect(Collectors.toList());
  }

  @Override
  public Module getModel() {
    Module m = super.getModel();
    for (Pair<String, String> p : RData.INSTANCE.getRTaskDatas())
      m.getImportPrefix().put(p.getValue1(), p.getValue0());
    return m;
  }

  @Override
  public String generate(Module module) {
    return CodeGenerator.generate(module).toString();
  }
}
