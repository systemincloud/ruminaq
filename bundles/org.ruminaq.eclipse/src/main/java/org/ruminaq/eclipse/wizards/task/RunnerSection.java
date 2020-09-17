/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.task;

/**
 * Runner intercept methods.
 *
 * @author Marek Jagielski
 */
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.ruminaq.eclipse.usertask.model.userdefined.Module;

class RunnerSection extends Group {

  private Button btnRunnerStart;
  private Button btnRunnerStop;

  public RunnerSection(Composite parent, int style) {
    super(parent, style);
  }

  @Override
  protected void checkSubclass() {
    // allow subclass
  }

  protected void initLayout() {
    setLayout(new GridLayout(AbstractCreateCustomTaskPage.TWO_COLUMNS, false));
    setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));

    btnRunnerStart = new Button(this, SWT.CHECK);
    btnRunnerStop = new Button(this, SWT.CHECK);
  }

  protected void initComponents() {
    btnRunnerStart.setText("runnerStart");
    btnRunnerStop.setText("runnerStop");
  }

  protected void decorate(Module module) {
    module.setRunnerStart(btnRunnerStart.getSelection());
    module.setRunnerStop(btnRunnerStop.getSelection());
  }

}