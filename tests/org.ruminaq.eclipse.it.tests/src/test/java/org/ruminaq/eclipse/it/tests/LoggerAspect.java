package org.ruminaq.eclipse.it.tests;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.codehaus.plexus.util.ExceptionUtils;

/**
 * Intercept IJavaProject.
 *
 * @author Marek Jagielski
 */
@Aspect
public class LoggerAspect {

  public static final String FILE_PATH = "loggerFilePath";

  /**
   * Pointcut on error.
   *
   */
  @Pointcut("call(* org.slf4j.Logger.error(..)) " + "&& args(arg0)")
  public void error(String arg0) {
  }

  /**
   * Pointcut on error with Throwable.
   *
   */
  @Pointcut("call(* org.slf4j.Logger.error(..)) " + "&& args(arg0, arg1)")
  public void errorWithThrowable(String arg0, Throwable arg1) {
  }

  @Around("error(arg0)")
  public void around(ProceedingJoinPoint point, String arg0) throws Throwable {
    point.proceed(new Object[] { arg0 });
  }

  @Around("errorWithThrowable(arg0, arg1)")
  public void around(ProceedingJoinPoint point, String arg0, Throwable arg1)
      throws Throwable {
    String loggerFilePath = System.getProperty(FILE_PATH);
    if ("".equals(loggerFilePath)) {
      point.proceed(new Object[] { arg0, arg1 });
    } else {
      Files.writeString(Paths.get(loggerFilePath), "[ERROR] " + arg0 + "\n",
          StandardCharsets.UTF_8, StandardOpenOption.APPEND);
      Files.writeString(Paths.get(loggerFilePath),
          "[ERROR] " + ExceptionUtils.getStackTrace(arg1) + "\n",
          StandardCharsets.UTF_8, StandardOpenOption.APPEND);
    }
  }
}
