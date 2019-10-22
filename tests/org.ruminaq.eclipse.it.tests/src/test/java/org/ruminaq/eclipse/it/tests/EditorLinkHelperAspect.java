package org.ruminaq.eclipse.it.tests;

import java.util.Optional;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.ruminaq.eclipse.editor.EditorLinkHelper;

/**
 * Intercept LinkHelperService.
 *
 * @author Marek Jagielski
 */
@Aspect
public class EditorLinkHelperAspect {

  public static final String ONLY_RUMINAQ_HELPER_FILE_NAME = "onlyRuminaqHelperFileName";

  /**
   * Pointcut on getLinkHelpersFor.
   *
   */
  @Pointcut("call(* org.eclipse.graphiti.examples.common.navigator.EditorLinkHelper.activateEditor(..)) && args(arg0, arg1)")
  public void activateEditor(IWorkbenchPage arg0,
      IStructuredSelection arg1) {
  }

  /**
   * Leave only EditorLinkHelper.
   *
   */
  @Around("activateEditor(arg0, arg1)")
  public void around(ProceedingJoinPoint point, IWorkbenchPage arg0,
      IStructuredSelection arg1) throws Throwable {
    String onlyRuminaq = System.getProperty(ONLY_RUMINAQ_HELPER_FILE_NAME);

    if (Optional.of(arg1).map(IStructuredSelection::getFirstElement)
        .filter(IFile.class::isInstance).map(o -> (IFile) o).map(IFile::getName)
        .orElse("").equals(onlyRuminaq)) {
      new EditorLinkHelper().activateEditor(arg0, arg1);
    } else {
      point.proceed(new Object[] { arg0, arg1 });
    }
  }
}
