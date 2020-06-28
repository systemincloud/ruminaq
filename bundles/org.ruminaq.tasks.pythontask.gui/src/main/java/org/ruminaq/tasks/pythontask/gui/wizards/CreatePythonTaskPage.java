package org.ruminaq.tasks.pythontask.gui.wizards;

import org.javatuples.Pair;
import org.ruminaq.eclipse.usertask.model.userdefined.Module;
import org.ruminaq.eclipse.wizards.task.CreateUserDefinedTaskPage;
import org.ruminaq.tasks.pythontask.gui.PythonData;
import org.ruminaq.tasks.pythontask.ui.wizards.ICreatePythonTaskPage;

public class CreatePythonTaskPage extends CreateUserDefinedTaskPage
    implements ICreatePythonTaskPage {

  public CreatePythonTaskPage(String pageName) {
    super(pageName);
    setTitle("System in Cloud - Python Task");
    setDescription("Here you can describe your task features");
  }

  @Override
  protected List<String> getDataTypes() {
    for (Pair<String, String> p : PythonData.INSTANCE.getPythonTaskDatas())
      cmb.add(p.getValue1());
  }

  @Override
  public Module getModel() {
    Module m = super.getModel();
    for (Pair<String, String> p : PythonData.INSTANCE.getPythonTaskDatas())
      m.getImportPrefix().put(p.getValue1(), p.getValue0());
    return m;
  }

  @Override
  public String generate(Module module) {
    return CodeGenerator.generate(module).toString();
  }
}
