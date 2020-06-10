package org.ruminaq.tasks.rtask.ui.wizards;

import org.ruminaq.eclipse.wizards.task.ICreateUserDefinedTaskPage;
import org.ruminaq.tasks.userdefined.model.userdefined.Module;

public interface ICreateRTaskPage extends ICreateUserDefinedTaskPage {
  String generate(Module module);
}
