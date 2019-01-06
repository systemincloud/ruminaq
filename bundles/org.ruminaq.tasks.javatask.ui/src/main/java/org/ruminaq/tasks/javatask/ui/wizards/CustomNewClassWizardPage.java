package org.ruminaq.tasks.javatask.ui.wizards;

import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

@SuppressWarnings("restriction")
public class CustomNewClassWizardPage extends NewClassWizardPage {

	public CustomNewClassWizardPage() {
		setTitle("System in Cloud - Java Task");
		setDescription("Here you define Java class");
	}
	
	@Override
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
			
		int nColumns= 4;
				
		GridLayout layout= new GridLayout();
		layout.numColumns= nColumns;		
		composite.setLayout(layout);
				
		createContainerControls(composite, nColumns);	
		createPackageControls(composite, nColumns);	
		createSeparator(composite, nColumns);
		createTypeNameControls(composite, nColumns);
		createSuperInterfacesControls(composite, nColumns);

		setControl(composite);
		Dialog.applyDialogFont(composite);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IJavaHelpContextIds.NEW_CLASS_WIZARD_PAGE);	
	}
}
