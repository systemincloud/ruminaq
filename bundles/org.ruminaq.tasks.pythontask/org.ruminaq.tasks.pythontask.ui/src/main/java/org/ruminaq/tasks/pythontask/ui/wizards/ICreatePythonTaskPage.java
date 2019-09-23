package org.ruminaq.tasks.pythontask.ui.wizards;

import org.ruminaq.tasks.userdefined.model.userdefined.Module;
import org.ruminaq.tasks.userdefined.wizards.ICreateUserDefinedTaskPage;

public interface ICreatePythonTaskPage extends ICreateUserDefinedTaskPage {
  String generate(Module module);
}
