package org.ruminaq.eclipse.it.tests;

import java.util.Optional;
import java.util.stream.Stream;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.navigator.ILinkHelper;
import org.ruminaq.eclipse.editor.EditorLinkHelper;

/**
 * Intercept LinkHelperService.
 *
 * @author Marek Jagielski
 */
@Aspect
public class LinkHelperServiceAspect {

  public static final String ONLY_RUMINAQ_HELPER_FILE_NAME = "onlyRuminaqHelperFileName";

  /**
   * Pointcut on getLinkHelpersFor.
   *
   */
  @Pointcut("call(* org.eclipse.ui.navigator.LinkHelperService.getLinkHelpersFor(..)) && args(arg0)")
  public void getLinkHelpersFor(Object arg0) {
  }

  /**
   * Leave only EditorLinkHelper.
   *
   */
  @Around("getLinkHelpersFor(arg0)")
  public ILinkHelper[] around(ProceedingJoinPoint point, Object arg0)
      throws Throwable {
    String onlyRuminaq = System.getProperty(ONLY_RUMINAQ_HELPER_FILE_NAME);

    ILinkHelper[] helpers = (ILinkHelper[]) point
        .proceed(new Object[] { arg0 });

    if (Optional.of(arg0).filter(IFile.class::isInstance)
        .map(o -> (IFile) o).map(IFile::getName).orElse("")
        .equals(onlyRuminaq)) {
      return Stream.of(helpers).filter(EditorLinkHelper.class::isInstance)
          .toArray(ILinkHelper[]::new);
    } else {
      return helpers;
    }
  }
}
