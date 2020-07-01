/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.task;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.ruminaq.eclipse.usertask.model.userdefined.Module;

/**
 * General configuration of task.
 *
 * @author Marek Jagielski
 */
class GeneralSection extends Group {

  private static final int NB_OF_COLUMNS = 5;

  private Button btnAtomic;
  private Button btnGenerator;
  private Button btnExternalSource;
  private Button btnConstant;

  public GeneralSection(Composite parent, int style) {
    super(parent, style);
    setLayout(new GridLayout(NB_OF_COLUMNS, false));
    setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
  }

  @Override
  protected void checkSubclass() {
    // allow subclass
  }

  void initLayout() {
    btnAtomic = new Button(this, SWT.CHECK);
    btnGenerator = new Button(this, SWT.CHECK);
    btnExternalSource = new Button(this, SWT.CHECK);
    btnConstant = new Button(this, SWT.CHECK);
  }

  void initComponents() {
    btnAtomic.setText("atomic");
    btnAtomic.setSelection(true);
    btnGenerator.setText("generator");
    btnExternalSource.setText("external source");
    btnConstant.setText("constant");
  }

  void initActions() {
    btnAtomic.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        if (!btnAtomic.getSelection()) {
          btnConstant.setSelection(false);
        }
      }
    });
    btnConstant.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        if (btnConstant.getSelection()) {
          btnAtomic.setSelection(true);
          btnGenerator.setSelection(false);
          btnExternalSource.setSelection(false);
        }
      }
    });
    btnGenerator.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        if (btnGenerator.getSelection()) {
          btnConstant.setSelection(false);
        }
      }
    });
    btnExternalSource.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        if (btnExternalSource.getSelection()) {
          btnConstant.setSelection(false);
        }
      }
    });
  }

  void decorate(Module module) {
    module.setAtomic(btnAtomic.getSelection());
    module.setConstant(btnConstant.getSelection());
    module.setExternalSource(btnExternalSource.getSelection());
    module.setGenerator(btnGenerator.getSelection());
  }

}