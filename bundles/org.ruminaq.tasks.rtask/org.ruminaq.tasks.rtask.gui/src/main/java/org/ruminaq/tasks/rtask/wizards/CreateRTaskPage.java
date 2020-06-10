package org.ruminaq.tasks.rtask.wizards;

import org.eclipse.swt.widgets.Combo;
import org.javatuples.Pair;
import org.ruminaq.eclipse.usertask.model.userdefined.Module;
import org.ruminaq.eclipse.wizards.task.CreateUserDefinedTaskPage;
import org.ruminaq.tasks.rtask.RData;
import org.ruminaq.tasks.rtask.ui.wizards.ICreateRTaskPage;

public class CreateRTaskPage extends CreateUserDefinedTaskPage
    implements ICreateRTaskPage {

  public CreateRTaskPage(String pageName) {
    super(pageName);
    setTitle("System in Cloud - R Task");
    setDescription("Here you can describe your task features");
  }

  @Override
  protected void fillWithData(Combo cmb) {
    for (Pair<String, String> p : RData.INSTANCE.getRTaskDatas())
      cmb.add(p.getValue1());
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
