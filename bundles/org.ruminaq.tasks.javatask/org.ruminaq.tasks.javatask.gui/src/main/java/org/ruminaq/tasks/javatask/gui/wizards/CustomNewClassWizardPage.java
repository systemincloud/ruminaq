/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.javatask.gui.wizards;

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
    setTitle("Ruminaq- Java Task");
    setDescription("Here you define Java class");
  }

  @Override
  public void createControl(Composite parent) {
    initializeDialogUnits(parent);

    Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());

    int nColumns = 4;

    GridLayout layout = new GridLayout();
    layout.numColumns = nColumns;
    composite.setLayout(layout);

    createContainerControls(composite, nColumns);
    createPackageControls(composite, nColumns);
    createSeparator(composite, nColumns);
    createTypeNameControls(composite, nColumns);
    createSuperInterfacesControls(composite, nColumns);

    setControl(composite);
    Dialog.applyDialogFont(composite);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(composite,
        IJavaHelpContextIds.NEW_CLASS_WIZARD_PAGE);
  }
}
