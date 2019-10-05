package org.ruminaq.eclipse.it.tests;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.ruminaq.prefs.ProjectProps;
import org.ruminaq.prefs.Props;

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
    if (ProjectProps.MODELER_VERSION.equals(arg0) && ((Props) point.getTarget())
        .getProject().getName().equals(versionProjectName)) {
      String versionProject = System
          .getProperty(VERSION_PROJECT);
      return versionProject;
    } else {
      return point.proceed(new Object[] { arg0 });
    }
  }
}
