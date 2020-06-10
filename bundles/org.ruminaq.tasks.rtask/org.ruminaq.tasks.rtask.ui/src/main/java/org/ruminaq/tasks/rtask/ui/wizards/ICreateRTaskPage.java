package org.ruminaq.tasks.rtask.ui.wizards;

import org.ruminaq.eclipse.usertask.model.userdefined.Module;
import org.ruminaq.eclipse.wizards.task.ICreateUserDefinedTaskPage;

public interface ICreateRTaskPage extends ICreateUserDefinedTaskPage {
  String generate(Module module);
}
