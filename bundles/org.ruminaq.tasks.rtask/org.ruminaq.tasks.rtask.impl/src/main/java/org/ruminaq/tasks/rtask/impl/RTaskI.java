/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.rtask.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map.Entry;

import org.eclipse.core.runtime.CoreException;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.runner.impl.GeneratorI;
import org.ruminaq.runner.impl.PortMap;
import org.ruminaq.runner.impl.data.DataI;
import org.ruminaq.runner.impl.data.DataManager;
import org.ruminaq.util.GroovyExpressionUtil;
import org.ruminaq.runner.util.Util;
import org.ruminaq.tasks.rtask.model.rtask.RTask;
import ch.qos.logback.classic.Logger;

//import de.walware.rj.servi.RServi;
//import de.walware.rj.services.FunctionCall;

public class RTaskI extends GeneratorI implements RTaskListener {

  private final Logger logger = RunnerLoggerFactory.getLogger(RTaskI.class);

  public static final String ATTR_R_HOME = "r_home";
  public static final String ATTR_R_POLICY = "r_policy";
  public static final String ATTR_R_LIBS_USR = "r_libs_usr";
  public static final String ATTR_R_RJPATH = "r_rjpath";

  private String id;
//  private RServi fRservi;

  private Logger rLogger = null;

  private HashMap<String, String> parameters = new HashMap<>();

  private PortMap portIdData;

  public RTaskI(EmbeddedTaskI parent, Task task) {
    super(parent, task);

    String rHome = parent.getInputArgument(ATTR_R_HOME);
    String workdir = parent.getBasePath() + "/target";
    String rPolicy = parent.getInputArgument(ATTR_R_POLICY);
    String rLibsUsr = parent.getInputArgument(ATTR_R_LIBS_USR);
    String rjPath = parent.getInputArgument(ATTR_R_RJPATH);

    RServer.INSTANCE.init(rHome, workdir, rPolicy, rLibsUsr, rjPath);

    String impl = ((RTask) task).getImplementation();
    String fullPath = parent.getBasePath() + impl;
    String name = fullPath.substring(0, fullPath.lastIndexOf("."))
        .substring(fullPath.lastIndexOf("/") + 1);
    logger.trace("impl: {}", fullPath);
    logger.trace("name: {}", name);
    this.rLogger = RunnerLoggerFactory.getLogger(impl);

    this.id = Util.getUniqueId((RTask) task, parent.getBasePath());
//    this.fRservi = RServer.INSTANCE.getRServi(id);

//    if (fRservi == null) {
//      logger.error("error");
//      return;
//    }

    //
    // Parameters
    //
    for (Entry<String, String> p : ((org.ruminaq.tasks.rtask.model.rtask.RTask) task)
        .getParameters().entrySet())
      parameters.put(p.getKey(), parent.replaceVariables(p.getValue()));

//    try {
//      PrintOutServer.INSTACE.start(this);
//      CallbackServer.INSTANCE.start(this.id, this);
//      this.fRservi
//          .evalVoid("sink(socketConnection(host = \"localhost\", port = "
//              + PrintOutServer.INSTACE.getPort()
//              + ", blocking = TRUE, open = \"w\"))", null);

//      FunctionCall rtaskFile = this.fRservi.createFunctionCall("source");
//      rtaskFile.add("file", "file(\"" + fullPath + "\")");
//      rtaskFile.add("chdir", "TRUE");
//      rtaskFile.evalVoid(null);
//
//      this.fRservi.evalVoid("instance <- " + name + "$new()", null);
//
//      this.fRservi.evalVoid(
//          "instance$rtListener <- RTListener$new('" + this.id + "')", null);
//
//      this.fRservi.evalVoid(
//          "thriftc <- thriftr::load(t[0], module_name='runnersideserver_thrift', include_dirs=[t[1]])",
//          null);
//      this.fRservi.evalVoid(
//          "instance$rtListener$client <- thriftr::make_client(thriftc.RunnerSideServer, '127.0.0.1', "
//              + CallbackServer.INSTANCE.getPort() + ")",
//          null);
//      this.fRservi.evalVoid("instance$copyRtListener()", null);
//    } catch (CoreException e) {
//      e.printStackTrace();
//    }

    //
    // RTaskInfo
    //
//    try {
//      this.atomic = this.fRservi.evalData("instance$rtaskinfo$atomic", null)
//          .getData().getInt(0) == 1;
//      this.generator = this.fRservi
//          .evalData("instance$rtaskinfo$generator", null).getData()
//          .getInt(0) == 1;
//      this.externalSource = this.fRservi
//          .evalData("instance$rtaskinfo$external_source", null).getData()
//          .getInt(0) == 1;
//      this.constant = this.fRservi.evalData("instance$rtaskinfo$constant", null)
//          .getData().getInt(0) == 1;
//    } catch (CoreException e) {
//      e.printStackTrace();
//    }
  }

  @Override
  public void runnerStart() {
//    try {
//      this.fRservi.evalVoid("instance$runner_start()", null);
//    } catch (CoreException e) {
//      e.printStackTrace();
//    }
  }

  @Override
  public void runnerStop() {
//    try {
//      this.fRservi.evalVoid("instance$runner_stop()", null);
//      fRservi.close();
//      RServer.INSTANCE.stop(fRservi);
//      CallbackServer.INSTANCE.stop(this.id);
//      PrintOutServer.INSTACE.stop(this);
//    } catch (CoreException e) {
//      e.printStackTrace();
//    }
  }

  @Override
  protected void executeConstant() {
    logger.trace("executeConstant");
//    this.portIdData = null;
//    try {
//      this.fRservi.evalVoid("instance$execute(-1)", null);
//    } catch (CoreException e) {
//      e.printStackTrace();
//    }
  }

  @Override
  protected void execute(PortMap portIdData, int grp) {
    logger.trace("execute");
//    this.portIdData = portIdData;
//    try {
//      this.fRservi.evalVoid("instance$execute(" + grp + ")", null);
//    } catch (CoreException e) {
//      e.printStackTrace();
//    }
  }

  @Override
  protected void executeAsync(String portId, DataI data) {
    logger.trace("executeAsync: {}:{}", portId, data);
    PortMap portIdData = new PortMap();
    portIdData.put(portId, data);
    this.portIdData = portIdData;
//    try {
//      this.fRservi.evalVoid("instance$execute_async(" + portId + ")", null);
//    } catch (CoreException e) {
//      e.printStackTrace();
//    }
  }

  @Override
  protected void executeExternalSrc() {
    logger.trace("executeExternalSrc");
    this.portIdData = null;
//    try {
//      this.fRservi.evalVoid("instance$execute_ext_src()", null);
//    } catch (CoreException e) {
//      e.printStackTrace();
//    }
  }

  @Override
  protected void generate() {
    logger.trace("generate");
    this.portIdData = null;
//    try {
//      this.fRservi.evalVoid("instance$generate()", null);
//    } catch (CoreException e) {
//      e.printStackTrace();
//    }
  }

  @Override
  public void sleep(long l) {
    super.sleep(l);
  }

  @Override
  public void externalData(int i) {
    addExternalSrcExecNb(i);
    setReadyWithParents(true);
  }

  @Override
  public void generatorPause() {
    super.pause();
  }

  @Override
  public boolean generatorIsPaused() {
    return super.isPaused();
  }

  @Override
  public void generatorResume() {
    super.resume();
  }

  @Override
  public void generatorEnd() {
    super.end();
  }

  @Override
  public void exitRunner() {
    super.breakRunner();
  }

  @Override
  public String getParameter(String key) {
    return parameters.get(key);
  }

  @Override
  public String runExpression(String expression) {
    return GroovyExpressionUtil.evaluate(expression).toString();
  }

  @Override
  public void log(String level, String msg) {
    try {
      Method method = rLogger.getClass().getMethod(level, String.class);
      method.invoke(rLogger, msg);
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException
        | IllegalArgumentException | InvocationTargetException e) {
    }
  }

  @Override
  public DataI getData(String ipName, String datatype) {
    if (portIdData == null)
      return null;
    DataI dataI = portIdData.get(ipName);
    if (dataI != null)
      dataI = dataI.get(DataManager.INSTANCE.getDataFromName(datatype));
    return dataI;
  }

  @Override
  public void cleanQueue(String ipName) {
    internalInputPorts.get(ipName).removeAllData();
  }

  @Override
  public void putData(String opName, DataI dataI) {
    if (dataI == null)
      return;
    super.putData(opName, dataI);
  }
}
