package org.ruminaq.tasks.rtask.ui.wizards;

import org.ruminaq.tasks.userdefined.model.userdefined.Module;
import org.ruminaq.tasks.userdefined.wizards.ICreateUserDefinedTaskPage;

public interface ICreateRTaskPage extends ICreateUserDefinedTaskPage {
  String generate(Module module);
}
