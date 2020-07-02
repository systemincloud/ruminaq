/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.it.tests;

import java.util.Optional;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.ruminaq.util.PlatformUtil;

/**
 * Intercept IDE.
 *
 * @author Marek Jagielski
 */
@Aspect
public class CreateDiagramWizardAspect {

  public static final String FAIL_OPEN_EDITOR_FILE_NAME = "failOpenEditor";

  /**
   * Pointcut on openEditor.
   *
   */
  @Pointcut("call(* org.ruminaq.eclipse.wizards.diagram.CreateDiagramWizard.openEditor(..)) "
      + "&& args(arg0, arg1)")
  public void openEditor(IWorkbenchPage arg0, IFile arg1) {
  }

  /**
   * Test failure of opening editor.
   *
   */
  @Around("openEditor(arg0, arg1)")
  public Object around(ProceedingJoinPoint point, IWorkbenchPage arg0,
      IFile arg1) throws Throwable {
    Optional<String> failingFileName = Optional
        .ofNullable(System.getProperty(FAIL_OPEN_EDITOR_FILE_NAME));

    if (failingFileName.isPresent()
        && (arg1.getName().startsWith(failingFileName.get()))) {
      throw new PartInitException(new Status(IStatus.ERROR,
          PlatformUtil.getBundleSymbolicName(getClass()), "Failed"));
    } else {
      return point.proceed(new Object[] { arg0, arg1 });
    }
  }
}
