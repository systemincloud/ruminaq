package org.ruminaq.eclipse.it.tests;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.ruminaq.util.PlatformUtil;

/**
 * Intercept EclipseUtil.
 *
 * @author Marek Jagielski
 */
@Aspect
public class EclipseUtilAspect {

  public static final String FAIL_CREATE_SOURCE_FOLDERS_PROJECT_NAME = "failCreateSourceFolders";

  /**
   * Pointcut on createFolderWithParents.
   *
   */
  @Pointcut("call(* org.ruminaq.util.EclipseUtil.createFolderWithParents(..)) && args(project, path)")
  public void createFolderWithParents(IProject project, String path) {
  }

  /**
   * Test failure of creating output location.
   *
   */
  @Around("createFolderWithParents(project, path)")
  public void around(ProceedingJoinPoint point, IProject project, String path)
      throws Throwable {
    String failingProjectName = System
        .getProperty(FAIL_CREATE_SOURCE_FOLDERS_PROJECT_NAME);

    if (project.getName().equals(failingProjectName)) {
      throw new CoreException(new Status(IStatus.ERROR,
          PlatformUtil.getBundleSymbolicName(getClass()), "Failed"));
    } else {
      point.proceed(new Object[] { project, path });
    }
  }
}
