/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

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
import org.ruminaq.util.Try;

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
  @Pointcut("call(* org.ruminaq.eclipse.EclipseUtil.createFolderWithParents(..)) && args(project, path)")
  public void createFolderWithParents(IProject project, String path) {
  }

  /**
   * Test failure of creating output location.
   *
   */
  @Around("createFolderWithParents(project, path)")
  public Try<CoreException> around(ProceedingJoinPoint point, IProject project,
      String path) throws Throwable {
    String failingProjectName = System
        .getProperty(FAIL_CREATE_SOURCE_FOLDERS_PROJECT_NAME);

    if (project.getName().equals(failingProjectName)) {
      return Try.crash(new CoreException(new Status(IStatus.ERROR,
          PlatformUtil.getBundleSymbolicName(getClass()), "Failed")));
    } else {
      return (Try<CoreException>) point.proceed(new Object[] { project, path });
    }
  }
}
