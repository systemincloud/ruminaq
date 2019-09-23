package org.ruminaq.tasks.pythontask.wizards;

import org.eclipse.swt.widgets.Combo;
import org.javatuples.Pair;
import org.ruminaq.tasks.pythontask.PythonData;
import org.ruminaq.tasks.pythontask.ui.wizards.ICreatePythonTaskPage;
import org.ruminaq.tasks.userdefined.model.userdefined.Module;
import org.ruminaq.tasks.userdefined.wizards.CreateUserDefinedTaskPage;

public class CreatePythonTaskPage extends CreateUserDefinedTaskPage
    implements ICreatePythonTaskPage {

  public CreatePythonTaskPage(String pageName) {
    super(pageName);
    setTitle("System in Cloud - Python Task");
    setDescription("Here you can describe your task features");
  }

  @Override
  protected void fillWithData(Combo cmb) {
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
