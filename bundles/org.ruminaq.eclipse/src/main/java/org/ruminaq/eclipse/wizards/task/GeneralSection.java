/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.task;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.ruminaq.eclipse.usertask.model.userdefined.Module;
import org.ruminaq.util.WidgetSelectedSelectionListener;

/**
 * General configuration of task.
 *
 * @author Marek Jagielski
 */
class GeneralSection extends AbstractSection {

  private Button btnAtomic;
  private Button btnGenerator;
  private Button btnExternalSource;
  private Button btnConstant;

  public GeneralSection(CreateUserDefinedTaskPage page, Composite parent, int style) {
    super(page, parent, style);
  }

  @Override
  protected void initLayout() {
    setLayout(new GridLayout(FIVE_COLUMNS, false));
    setLayoutData(
        new GridData(SWT.LEFT, SWT.CENTER, false, false, TWO_COLUMNS, 1));
    btnAtomic = new Button(this, SWT.CHECK);
    btnGenerator = new Button(this, SWT.CHECK);
    btnExternalSource = new Button(this, SWT.CHECK);
    btnConstant = new Button(this, SWT.CHECK);
  }

  @Override
  protected void initComponents() {
    btnAtomic.setText("atomic");
    btnAtomic.setSelection(true);
    btnGenerator.setText("generator");
    btnExternalSource.setText("external source");
    btnConstant.setText("constant");
  }

  @Override
  protected void initActions() {
    btnAtomic.addSelectionListener(
        (WidgetSelectedSelectionListener) (SelectionEvent event) -> {
          if (!btnAtomic.getSelection()) {
            btnConstant.setSelection(false);
          }
        });
    btnConstant.addSelectionListener(
        (WidgetSelectedSelectionListener) (SelectionEvent event) -> {
          if (btnConstant.getSelection()) {
            btnAtomic.setSelection(true);
            btnGenerator.setSelection(false);
            btnExternalSource.setSelection(false);
          }
        });
    btnGenerator.addSelectionListener(
        (WidgetSelectedSelectionListener) (SelectionEvent event) -> {
          if (btnGenerator.getSelection()) {
            btnConstant.setSelection(false);
          }
        });
    btnExternalSource.addSelectionListener(
        (WidgetSelectedSelectionListener) (SelectionEvent event) -> {
          if (btnExternalSource.getSelection()) {
            btnConstant.setSelection(false);
          }
        });
  }

  /**
   * Contribute to custom task definition.
   *
   * @param module custom task definition.
   */
  void decorate(Module module) {
    module.setAtomic(btnAtomic.getSelection());
    module.setConstant(btnConstant.getSelection());
    module.setExternalSource(btnExternalSource.getSelection());
    module.setGenerator(btnGenerator.getSelection());
  }
}
