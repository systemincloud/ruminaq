package org.ruminaq.eclipse.it.tests;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.eclipse.jdt.core.IJavaProject;
import org.ruminaq.eclipse.Messages;
import org.ruminaq.eclipse.RuminaqException;

@Aspect
public class CreateProjectWizardAspect {

  public static final String FAIL_CREATE_OUTPUT_LOCATION_PROJECY_NAME = "failCreateOutputLocation";

  /**
   * Test failure of creating output location.
   *
   */
  @Around("call(* org.ruminaq.eclipse.wizards.project."
      + "CreateProjectWizard.createOutputLocation(..)) "
      + "&& args(javaProject)")
  public Object around(ProceedingJoinPoint point, IJavaProject javaProject)
      throws Throwable {
    String failingProjectName = System
        .getProperty(FAIL_CREATE_OUTPUT_LOCATION_PROJECY_NAME);
    if (javaProject.getProject().getName().equals(failingProjectName)) {
      throw new RuminaqException(Messages.createProjectWizardFailed);
    } else {
      return point.proceed(new Object[] { javaProject });
    }
  }
}
