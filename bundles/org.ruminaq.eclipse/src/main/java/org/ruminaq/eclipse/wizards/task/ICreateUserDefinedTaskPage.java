package org.ruminaq.eclipse.wizards.task;

import org.eclipse.jface.wizard.IWizardPage;
import org.ruminaq.eclipse.usertask.model.userdefined.Module;

public interface ICreateUserDefinedTaskPage extends IWizardPage {
  Module getModel();
}
