/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.task;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.ruminaq.eclipse.usertask.model.userdefined.Module;

/**
 * Runner intercept methods.
 *
 * @author Marek Jagielski
 */
class RunnerSection extends Group {

  private Button btnRunnerStart;
  private Button btnRunnerStop;

  public RunnerSection(Composite parent, int style) {
    super(parent, style);
    initLayout();
    initComponents();
  }

  @Override
  protected void checkSubclass() {
    // allow subclass
  }

  private final void initLayout() {
    setLayout(
        new GridLayout(AbstractCreateUserDefinedTaskPage.TWO_COLUMNS, false));
    setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false,
        AbstractCreateUserDefinedTaskPage.TWO_COLUMNS, 1));

    btnRunnerStart = new Button(this, SWT.CHECK);
    btnRunnerStop = new Button(this, SWT.CHECK);
  }

  private final void initComponents() {
    btnRunnerStart.setText("runnerStart");
    btnRunnerStop.setText("runnerStop");
  }

  protected void decorate(Module module) {
    module.setRunnerStart(btnRunnerStart.getSelection());
    module.setRunnerStop(btnRunnerStop.getSelection());
  }
}
