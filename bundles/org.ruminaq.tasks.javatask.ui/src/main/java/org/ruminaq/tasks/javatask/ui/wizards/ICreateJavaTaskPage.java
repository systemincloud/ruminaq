package org.ruminaq.tasks.javatask.ui.wizards;

import org.eclipse.jdt.core.IType;
import org.ruminaq.tasks.userdefined.model.userdefined.Module;
import org.ruminaq.tasks.userdefined.wizards.ICreateUserDefinedTaskPage;

public interface ICreateJavaTaskPage extends ICreateUserDefinedTaskPage {
    void decorateType(IType type, Module module);
}
