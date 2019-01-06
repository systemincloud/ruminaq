package org.ruminaq.runner;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

public class RunnerLoggerFactory {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    private static String fileName = formatter.format(new Date()) + ".log";

    public static String ERROR_MSG = "Internal error. Please send log file /target/logs/" + fileName + " to marek.jagielski@gmail.com to help improve Ruminaq";

    private static final String FILE_APPENDER_NAME = "FILE";
    private static FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
    private static boolean init = false;

    private static String logLevel = Level.WARN.levelStr;

    public static void setLogLevel(String logLevel) { RunnerLoggerFactory.logLevel = logLevel; }

    public static void initFileAppender(String directory) {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        PatternLayoutEncoder ple = new PatternLayoutEncoder();

        ple.setPattern("%date %-5level [%thread] %logger{10} [:%line] %msg%n");
        ple.setContext(lc);
        ple.start();

        fileAppender.setFile(directory + "logs/" + RunnerLoggerFactory.fileName);
        fileAppender.setEncoder(ple);
        fileAppender.setContext(lc);
        fileAppender.setName(FILE_APPENDER_NAME);
        fileAppender.start();
        init = true;
    }

    public static Logger getLogger(Class<?> name) {
        Logger logger = (Logger) LoggerFactory.getLogger(name);
        return initLogger(logger);
    }

    public static Logger getLogger(String name) {
        Logger logger = (Logger) LoggerFactory.getLogger(name);
        return initLogger(logger);
    }

    private static Logger initLogger(Logger logger) {
        if(logger.getAppender(FILE_APPENDER_NAME) == null && init) {
            logger.addAppender(fileAppender);
            logger.setLevel(Level.toLevel(logLevel));
            logger.setAdditive(false);
        }
        return logger;
    }
}
