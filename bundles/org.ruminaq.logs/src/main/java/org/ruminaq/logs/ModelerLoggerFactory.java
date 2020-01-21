package org.ruminaq.logs;

import java.io.File;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.ruminaq.prefs.Prefs;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

public final class ModelerLoggerFactory {

  public static final String MODELER_LOG_LEVEL_PREF = "modeler.log.level";

  private static final String FILE_APPENDER_NAME = "FILE";

  private static FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();

  private ModelerLoggerFactory() {
  }

  static {
    File logFile = new File(
        ResourcesPlugin.getWorkspace().getRoot().getLocation().toString()
            + "/.metadata/modeler.log");
    if (logFile.exists()) {
      logFile.delete();
    }

    LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
    PatternLayoutEncoder ple = new PatternLayoutEncoder();

    ple.setPattern(
        "%date %-5level [%thread] %logger{10} [:%line] %replace(%msg){'[\\r\\n]', ''}%n");
    ple.setContext(lc);
    ple.start();

    fileAppender.setFile(
        ResourcesPlugin.getWorkspace().getRoot().getLocation().toString()
            + "/.metadata/modeler.log");
    fileAppender.setEncoder(ple);
    fileAppender.setContext(lc);
    fileAppender.setName(FILE_APPENDER_NAME);
    fileAppender.start();
  }

  interface GetLogger {
    public Logger operation();
  }

  public static Logger getLogger(Class<?> name) {
    return getLogger(() -> (Logger) LoggerFactory.getLogger(name));
  }

  public static Logger getLogger(String name) {
    return getLogger(() -> (Logger) LoggerFactory.getLogger(name));
  }

  public static Logger getLogger(GetLogger lambda) {
    Logger logger = lambda.operation();
    if (logger.getAppender(FILE_APPENDER_NAME) == null) {
      logger.addAppender(fileAppender);
      logger.setLevel(
          Level.toLevel(InstanceScope.INSTANCE.getNode(Prefs.QUALIFIER)
              .get(MODELER_LOG_LEVEL_PREF, Level.ERROR.levelStr)));
      logger.setAdditive(false);
    }
    return logger;
  }
}
