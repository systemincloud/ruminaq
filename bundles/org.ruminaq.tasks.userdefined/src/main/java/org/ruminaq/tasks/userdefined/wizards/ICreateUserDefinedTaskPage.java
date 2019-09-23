package org.ruminaq.tasks.userdefined.wizards;

import org.eclipse.jface.wizard.IWizardPage;
import org.ruminaq.tasks.userdefined.model.userdefined.Module;

public interface ICreateUserDefinedTaskPage extends IWizardPage {
  Module getModel();
}
