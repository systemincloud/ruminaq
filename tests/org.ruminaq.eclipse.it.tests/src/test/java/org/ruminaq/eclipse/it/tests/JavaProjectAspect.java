package org.ruminaq.eclipse.it.tests;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.ruminaq.util.PlatformUtil;

/**
 * Intercept IJavaProject.
 *
 * @author Marek Jagielski
 */
@Aspect
public class JavaProjectAspect {

  public static final String FAIL_CREATE_OUTPUT_LOCATION_PROJECT_NAME = "failCreateOutputLocation";

  /**
   * Pointcut on setOutputLocation.
   *
   */
  @Pointcut("call(* org.eclipse.jdt.core.IJavaProject.setOutputLocation(..)) "
      + "&& args(arg0, arg1)")
  public void setOutputLocation(IPath arg0, IProgressMonitor arg1) {
  }

  /**
   * Test failure of creating output location.
   *
   */
  @Around("setOutputLocation(arg0, arg1)")
  public Object around(ProceedingJoinPoint point, IPath arg0,
      IProgressMonitor arg1) throws Throwable {
    String failingProjectName = System
        .getProperty(FAIL_CREATE_OUTPUT_LOCATION_PROJECT_NAME);

    if (((IJavaProject) point.getTarget()).getProject().getName()
        .equals(failingProjectName)) {
      throw new JavaModelException(new Status(IStatus.ERROR,
          PlatformUtil.getBundleSymbolicName(getClass()), "Failed"));
    } else {
      return point.proceed(new Object[] { arg0, arg1 });
    }
  }
}
