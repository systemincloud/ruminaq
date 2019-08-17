package org.ruminaq.eclipse.it.tests;

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
      + "&& args(page, input, activate)")
  public void openEditor(IWorkbenchPage page, IFile input, boolean activate) {
  }

  /**
   * Test failure of opening editor.
   *
   */
  @Around("openEditor(IPath, IProgressMonitor) && args(page, input, activate)")
  public Object around(ProceedingJoinPoint point, IWorkbenchPage page,
      IFile input, boolean activate) throws Throwable {
    String failingFileName = System.getProperty(FAIL_OPEN_EDITOR_FILE_NAME);

    if (input.getName().startsWith(failingFileName)) {
      throw new PartInitException(new Status(IStatus.ERROR,
          PlatformUtil.getBundleSymbolicName(getClass()), "Failed"));
    } else {
      return point.proceed(new Object[] { page, input, activate });
    }
  }
}
