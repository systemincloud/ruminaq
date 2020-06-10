package org.ruminaq.tasks.pythontask.ui.wizards;

import org.ruminaq.eclipse.wizards.task.ICreateUserDefinedTaskPage;
import org.ruminaq.eclipse.usertask.model.userdefined.Module;

public interface ICreatePythonTaskPage extends ICreateUserDefinedTaskPage {
  String generate(Module module);
}
