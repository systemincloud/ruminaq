package org.ruminaq.tasks.api;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public interface IView {
	void setFocus();
	void dispose();
	void createPartControl(Composite parent, Shell shell);
	void init(EObject bo, TransactionalEditingDomain ed);
}
