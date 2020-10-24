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
import org.ruminaq.tasks.javatask.gui.Messages;

/**
 * Reuse jdt class wizard page..
 *
 * @author Marek Jagielski
 */
public class CustomNewClassWizardPage extends NewClassWizardPage {

  private static final int NB_OF_COLUMNS = 4;

  public CustomNewClassWizardPage() {
    setTitle(Messages.createJavaTaskWizardName);
    setDescription(Messages.customNewClassWizardPageDescription);
  }

  @Override
  public void createControl(Composite parent) {
    initializeDialogUnits(parent);

    Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());

    GridLayout layout = new GridLayout();
    layout.numColumns = NB_OF_COLUMNS;
    composite.setLayout(layout);

    createContainerControls(composite, NB_OF_COLUMNS);
    createPackageControls(composite, NB_OF_COLUMNS);
    createSeparator(composite, NB_OF_COLUMNS);
    createTypeNameControls(composite, NB_OF_COLUMNS);
    createSuperInterfacesControls(composite, NB_OF_COLUMNS);

    setControl(composite);
    Dialog.applyDialogFont(composite);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(composite,
        IJavaHelpContextIds.NEW_CLASS_WIZARD_PAGE);
  }
}
