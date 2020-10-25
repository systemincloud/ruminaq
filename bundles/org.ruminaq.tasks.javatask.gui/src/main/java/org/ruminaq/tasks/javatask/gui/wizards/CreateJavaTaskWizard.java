/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.javatask.gui.wizards;

import java.lang.reflect.Field;
import java.util.Optional;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.wizards.NewClassCreationWizard;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.ruminaq.eclipse.wizards.task.CreateUserDefinedTaskListener;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.tasks.javatask.gui.Messages;
import org.slf4j.Logger;

/**
 * Wizard for creating a Java Class that implements custom task.
 *
 * @author Marek Jagielski
 */
public class CreateJavaTaskWizard extends NewClassCreationWizard {

  private static final Logger LOGGER = ModelerLoggerFactory
      .getLogger(CreateJavaTaskWizard.class);

  public static final String ID = "org.ruminaq.tasks.javatask.gui.wizards.CreateJavaTaskWizard";

  private CreateJavaTaskPage cjtp = new CreateJavaTaskPage(
      Messages.createJavaTaskWizardName);

  private IStructuredSelection selection;
  private CreateUserDefinedTaskListener listener;

  public void setProject(IStructuredSelection selection) {
    this.selection = selection;
  }

  public void setListener(CreateUserDefinedTaskListener listener) {
    this.listener = listener;
  }

  private Optional<IType> createdType() {
    return Optional.ofNullable(getCreatedElement())
        .filter(IType.class::isInstance).map(IType.class::cast);
  }

  /**
   * Field fPage of NewClassCreationWizard is private.
   *
   * @param fPage new value for field
   */
  private void replacefPage(NewClassWizardPage fPage) {
    try {
      Field fPageF = NewClassCreationWizard.class.getDeclaredField("fPage");
      fPageF.setAccessible(true);
      fPageF.set(this, fPage);
    } catch (IllegalArgumentException | IllegalAccessException
        | NoSuchFieldException | SecurityException e) {
      LOGGER.error("Error creating wizzard", e);
    }
  }

  @Override
  public void addPages() {
    NewClassWizardPage fPage = new CustomNewClassWizardPage();
    fPage.setWizard(this);
    fPage.init(Optional.ofNullable(selection).orElseGet(this::getSelection));
    replacefPage(fPage);
    addPage(fPage);
    addPage(cjtp);
  }

  @Override
  protected void finishPage(IProgressMonitor monitor)
      throws InterruptedException, CoreException {
    super.finishPage(monitor);
    createdType().ifPresent(t -> Display.getDefault()
        .syncExec(() -> cjtp.decorateType(t, cjtp.getModel())));
  }

  @Override
  public boolean performFinish() {
    boolean ret = super.performFinish();
    Optional.ofNullable(listener).ifPresent(l -> createdType()
        .ifPresent(t -> l.created(t.getFullyQualifiedName())));
    return ret;
  }
}
