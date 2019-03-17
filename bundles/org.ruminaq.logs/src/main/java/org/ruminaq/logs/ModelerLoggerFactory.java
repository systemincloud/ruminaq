package org.ruminaq.logs;

import java.io.File;

import org.eclipse.core.resources.ResourcesPlugin;
import org.ruminaq.prefs.WorkspacePrefs;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

public class ModelerLoggerFactory {

	private static final String FILE_APPENDER_NAME = "FILE";
	private static FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();

	static {
		File logFile = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + "/.metadata/modeler.log");
		if (logFile.exists()) {
		    logFile.delete();
		}

        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        PatternLayoutEncoder ple = new PatternLayoutEncoder();

        ple.setPattern("%date %-5level [%thread] %logger{10} [:%line] %msg%n");
        ple.setContext(lc);
        ple.start();

        fileAppender.setFile(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + "/.metadata/modeler.log");
        fileAppender.setEncoder(ple);
        fileAppender.setContext(lc);
        fileAppender.setName(FILE_APPENDER_NAME);
        fileAppender.start();
	}


	public static Logger getLogger(Class<?> name) {
		Logger logger = (Logger) LoggerFactory.getLogger(name);
		if(logger.getAppender(FILE_APPENDER_NAME) == null) {
	        logger.addAppender(fileAppender);
	        logger.setLevel(Level.toLevel(WorkspacePrefs.INSTANCE.get(WorkspacePrefs.MODELER_LOG_LEVEL)));
	        logger.setAdditive(false); /* set to true if root should log too */
		}
        return logger;
	}
}
