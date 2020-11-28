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
import org.ruminaq.eclipse.prefs.ProjectProps;
import org.ruminaq.eclipse.prefs.AbstractProps;

/**
 * Intercept Props.
 *
 * @author Marek Jagielski
 */
@Aspect
public class PropsAspect {

  public static final String VERSION_PROJECT_NAME = "propsVersionProjectName";
  public static final String VERSION_PROJECT = "propsVersionProject";

  /**
   * Pointcut on error.
   *
   */
  @Pointcut("call(* org.ruminaq.prefs.Props.get(..)) " + "&& args(arg0)")
  public void getProperty(String arg0) {
  }

  @Around("getProperty(arg0)")
  public Object around(ProceedingJoinPoint point, String arg0)
      throws Throwable {
    String versionProjectName = System
        .getProperty(VERSION_PROJECT_NAME);
    if (ProjectProps.RUMINAQ_VERSION.equals(arg0) && ((AbstractProps) point.getTarget())
        .getProject().getName().equals(versionProjectName)) {
      String versionProject = System
          .getProperty(VERSION_PROJECT);
      return versionProject;
    } else {
      return point.proceed(new Object[] { arg0 });
    }
  }
}
