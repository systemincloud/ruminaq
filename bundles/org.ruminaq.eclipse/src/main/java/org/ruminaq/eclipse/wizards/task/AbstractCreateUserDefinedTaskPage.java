/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.task;

import java.util.Arrays;
import java.util.function.Predicate;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.ruminaq.eclipse.Messages;
import org.ruminaq.eclipse.usertask.model.userdefined.In;
import org.ruminaq.eclipse.usertask.model.userdefined.Module;
import org.ruminaq.eclipse.usertask.model.userdefined.UserdefinedFactory;

/**
 * Common wizard page for all custom tasks.
 *
 * @author Marek Jagielski
 */
public abstract class AbstractCreateUserDefinedTaskPage extends WizardPage
    implements CreateUserDefinedTaskPage {

  private static final int TWO_COLUMNS = 2;

  public static final String INF = "inf";

  private GeneralSection grpGeneral;
  private RunnerSection grpRunner;
  private Label lblInputPorts;
  private InputsSection grpInputs;
  private Label lblOutputPorts;
  private OutputsSection grpOutputs;
  private Label lblParameters;
  private ParametersSection grpParameters;

  protected AbstractCreateUserDefinedTaskPage(String pageName) {
    super(pageName);
  }

  @Override
  public void createControl(Composite parent) {
    setDescription(Messages.createUserDefinedTaskPageDescription);
    initLayout(parent);
    initComponents();
    Arrays.asList(grpGeneral, grpRunner, grpInputs, grpOutputs, grpParameters)
        .stream().forEach(s -> s.init());
  }

  private void initLayout(Composite parent) {
    Composite root = new Composite(parent, SWT.NULL);
    root.setLayout(new GridLayout(TWO_COLUMNS, false));

    grpGeneral = new GeneralSection(this, root, SWT.NONE);
    grpRunner = new RunnerSection(this, root, SWT.NONE);

    lblInputPorts = new Label(root, SWT.NONE);
    lblInputPorts.setLayoutData(
        new GridData(SWT.LEFT, SWT.CENTER, false, false, TWO_COLUMNS, 1));

    grpInputs = new InputsSection(this, root, SWT.NONE);

    lblOutputPorts = new Label(root, SWT.NONE);
    lblOutputPorts.setLayoutData(
        new GridData(SWT.LEFT, SWT.CENTER, false, false, TWO_COLUMNS, 1));

    grpOutputs = new OutputsSection(this, root, SWT.NONE);

    lblParameters = new Label(root, SWT.NONE);
    lblParameters.setLayoutData(
        new GridData(SWT.LEFT, SWT.CENTER, false, false, TWO_COLUMNS, 1));

    grpParameters = new ParametersSection(this, root, SWT.NONE);
    setControl(root);
  }

  private void initComponents() {
    lblInputPorts.setText("Input Ports:");
    lblOutputPorts.setText("Output Ports:");
    lblParameters.setText("Parameters:");
  }

  @Override
  public Module getModel() {
    Module module = UserdefinedFactory.eINSTANCE.createModule();
    Arrays.asList(grpGeneral, grpRunner, grpInputs, grpOutputs, grpParameters)
        .stream().forEach(s -> s.decorate(module));

    boolean hasAsync = module.getInputs().stream().anyMatch(In::isAsynchronous);
    boolean hasNonAsync = module.getInputs().stream()
        .anyMatch(Predicate.not(In::isAsynchronous));

    module.setExecuteAsync((!module.isAtomic() && hasNonAsync) || hasAsync);
    module.setExecuteExtSrc(module.isExternalSource());
    module.setGenerate(module.isGenerator());
    module
        .setExecute((module.isAtomic() && hasNonAsync) || module.isConstant());

    return module;
  }
}
