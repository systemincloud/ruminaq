package org.ruminaq.runner;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.ruminaq.eclipse.wizards.project.SourceFolders;
import org.ruminaq.model.dt.DatatypePackage;
import org.ruminaq.runner.dirmi.DirmiClient;
import org.ruminaq.runner.impl.debug.DebugI;
import org.ruminaq.runner.impl.debug.events.debugger.StartedEvent;
import org.ruminaq.runner.impl.debug.events.debugger.TerminatedEvent;
import org.ruminaq.runner.service.RunnerServiceManager;
import ch.qos.logback.classic.Logger;

import com.google.common.base.Joiner;

public class Runner {

  private final Logger logger = RunnerLoggerFactory.getLogger(Runner.class);

  public static final String ATTR_ACCOUNT = "a";
  public static final String ATTR_SYSTEM = "s";
  public static final String ATTR_KEY = "k";
  public static final String ATTR_MACHINE = "m";
  public static final String ATTR_ROOT = "r";
  public static final String ATTR_DIAGRAM = "f";
  public static final String ATTR_DNS = "n";
  public static final String ATTR_LOG_LEVEL = "l";
  public static final String ATTR_PORT = "p";
  public static final String ATTR_ONLY_LOCAL = "o";
  public static final String ATTR_DEBUG = "d";
  public static final String ATTR_MVN_REPO = "mvn_repo";

  public static final String ATTR_THRIFT_CLIENT = "thrift_client";
  public static final String ATTR_THRIFT_SERVER = "thrift_server";
  public static final String ATTR_THRIFT_DATA = "thrift_data";

  private Engine engine;

  public Engine getEngine() {
    return engine;
  }

  private String basePath;

  protected Runner(String testFile, boolean onlyLocal, CommandLine line) {
    init(testFile, onlyLocal, line);
  }

  private void init(String testFile, boolean onlyLocal, CommandLine line) {
    System.out.println("Running: "
        + testFile.replace("/", File.separator).replace("\\", File.separator));

    logger.trace("Test        : {}", testFile);
    logger.trace("Only local  : {}", onlyLocal);

    DatatypePackage.eINSTANCE.getClass();
    RunnerServiceManager.INSTANCE.initModelPackages();

    this.basePath = testFile.substring(0,
        testFile.indexOf(SourceFolders.TEST_RESOURCES));
    try {
      engine = new Engine(new AdapterTask(basePath, testFile, onlyLocal, line));
    } catch (Exception e) {
      e.printStackTrace();
      logger.error("\n{}\n{}", e.getMessage(),
          Joiner.on("\n").join(e.getStackTrace()));
    }
    DebugI.INSTANCE.setEngine(engine);
    DebugI.INSTANCE.initDebugers();
  }

  protected void run() {
    logger.debug("START");
    System.out.println("START");
    DebugI.INSTANCE.debug(new StartedEvent());
    engine.run();
    DebugI.INSTANCE.debug(new TerminatedEvent());
    System.out.println("STOP");
    logger.debug("STOP");
  }

  public static void main(String[] args) {
    Options options = new Options();
    options.addOption(ATTR_ACCOUNT, true, "");
    options.addOption(ATTR_SYSTEM, true, "");
    options.addOption(ATTR_KEY, true, "");
    options.addOption(ATTR_MACHINE, true, "");
    options.addOption(ATTR_ROOT, true, "");
    options.addOption(ATTR_DIAGRAM, true, "");
    options.addOption(ATTR_DNS, true, "");
    options.addOption(ATTR_LOG_LEVEL, true, "");
    options.addOption(ATTR_PORT, true, "");
    options.addOption(ATTR_ONLY_LOCAL, false, "");
    options.addOption(ATTR_DEBUG, false, "");
    options.addOption(ATTR_MVN_REPO, true, "");

    options.addOption(ATTR_THRIFT_CLIENT, true, "");
    options.addOption(ATTR_THRIFT_SERVER, true, "");
    options.addOption(ATTR_THRIFT_DATA, true, "");

    RunnerServiceManager.INSTANCE.addOptions(options);

    CommandLineParser parser = new DefaultParser();
    CommandLine line = null;
    try {
      line = parser.parse(options, args);
    } catch (ParseException e) {
      e.printStackTrace();
      return;
    }

    String projectRoot = "";
    if (line.hasOption(ATTR_ROOT))
      projectRoot = line.getOptionValue(ATTR_ROOT).replace("+", " ");
    else {
      System.err.println("Internal error : project root not set");
      return;
    }

    if (line.hasOption(ATTR_LOG_LEVEL)) {
      RunnerLoggerFactory.setLogLevel(line.getOptionValue(ATTR_LOG_LEVEL));
      RunnerLoggerFactory.initFileAppender(projectRoot + "/target/");
    }

    DirmiClient.INSTANCE.init(Integer.parseInt(line.getOptionValue(ATTR_PORT)));

    String testFile;
    boolean onlyLocal;
    if (line.hasOption(ATTR_DIAGRAM))
      testFile = line.getOptionValue(ATTR_DIAGRAM).replace("+", " ");
    else
      return;
    onlyLocal = line.hasOption(ATTR_ONLY_LOCAL);
    DebugI.INSTANCE.init(line.hasOption(ATTR_DEBUG));
    DirmiClient.INSTANCE.debugInitialized();

    Runner runner = new Runner(testFile, onlyLocal, line);
    DirmiClient.INSTANCE.registrationDone();
    runner.run();
    System.exit(0);
  }
}
