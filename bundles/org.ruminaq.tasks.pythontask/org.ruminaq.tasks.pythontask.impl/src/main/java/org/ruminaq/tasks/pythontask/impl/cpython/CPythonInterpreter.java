package org.ruminaq.tasks.pythontask.impl.cpython;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.ruminaq.runner.Runner;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.runner.impl.data.DataI;
import org.ruminaq.runner.impl.data.DataManager;
import org.ruminaq.runner.impl.data.RemoteDataConverter;
import org.ruminaq.runner.thrift.ProcessSideServer;
import org.ruminaq.runner.thrift.RemoteData;
import org.ruminaq.runner.thrift.RunnerSideServer;
import org.ruminaq.tasks.pythontask.impl.PyInterpreter;
import org.ruminaq.tasks.pythontask.impl.PythonTaskI;
import org.ruminaq.tasks.pythontask.impl.rmi.Server;
import org.ruminaq.util.Util;
import ch.qos.logback.classic.Logger;

public class CPythonInterpreter extends PyInterpreter
    implements RunnerSideServer.Iface {

  private final Logger logger = RunnerLoggerFactory
      .getLogger(CPythonInterpreter.class);

  private CPythonInterpreter() {
  }

  private static class LazyHolder {
    private static final CPythonInterpreter INSTANCE = new CPythonInterpreter();
  }

  public static CPythonInterpreter getInstance() {
    return LazyHolder.INSTANCE;
  }

  private Process process;
  private Server server = new Server();
  private TTransport transport;
  private ProcessSideServer.Client mainClient;
  private int portRev = 0;

  private Map<String, ProcessSideServer.Client> idThriftClientkMap = new HashMap<>();
  private Map<String, Lock> locks = new HashMap<>();

  private Lock lock = new ReentrantLock();
  private Condition cond = lock.newCondition();

  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected void init(EmbeddedTaskI parent, String processJar) {
    String bin = parent.getInputArgument(PythonTaskI.ATTR_PY_BIN);
    String env = parent.getInputArgument(PythonTaskI.ATTR_PY_ENV);
    String path = parent.getInputArgument(PythonTaskI.ATTR_PY_PATH);
    String extl = parent.getInputArgument(PythonTaskI.ATTR_PY_EXT_LIBS);
    String thriftc = parent.getInputArgument(Runner.ATTR_THRIFT_CLIENT);
    String thrifts = parent.getInputArgument(Runner.ATTR_THRIFT_SERVER);
    String thriftd = parent.getInputArgument(Runner.ATTR_THRIFT_DATA);
    String dir = parent.getBasePath() + "target";

    logger.trace("bin: {}", bin);
    logger.trace("env: {}", env);
    logger.trace("path: {}", path);
    logger.trace("processJar: {}", processJar);
    logger.trace("dir: {}", dir);
    logger.trace("thrift client: {}", thriftc);
    logger.trace("thrift server: {}", thrifts);
    logger.trace("thrift data: {}", thriftd);

    RunnerSideServer.Processor processor = new RunnerSideServer.Processor(this);
    int port = server.init(processor);
    this.portRev = Util.findFreeLocalPort(port + 1);
    logger.trace("port: {}", port);
    logger.trace("port: {}", portRev);
    ProcessBuilder pb = new ProcessBuilder(bin, processJar,
        Integer.toString(port), Integer.toString(portRev), path, extl, thriftc,
        thrifts, thriftd);
    pb.inheritIO();
    pb.redirectErrorStream(true);
    pb.directory(new File(dir));
    try {
      logger.trace("Start process");
      lock.lock();
      process = pb.start();
      cond.await();
      lock.unlock();
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void runnerIsRunning() throws TException {
    try {
      lock.lock();
      this.transport = new TSocket("localhost", this.portRev);
      this.transport.open();
      TProtocol protocol = new TBinaryProtocol(transport);
      this.mainClient = new ProcessSideServer.Client(protocol);
      cond.signal();
      lock.unlock();
    } catch (TException x) {
      x.printStackTrace();
    }
  }

  @Override
  protected void createTask(String id, String implFile) {
    try {
      this.mainClient.createTask(id, implFile);
      TSocket transport = new TSocket("localhost", this.portRev);
      transport.open();
      TProtocol protocol = new TBinaryProtocol(transport);
      ProcessSideServer.Client taskClient = new ProcessSideServer.Client(
          protocol);
      idThriftClientkMap.put(id, taskClient);
      locks.put(id, new ReentrantLock());
    } catch (TException e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean atomic(String id) {
    try {
      return this.idThriftClientkMap.get(id).atomic(id);
    } catch (TException e) {
      return false;
    }
  }

  @Override
  public boolean generator(String id) {
    try {
      return this.idThriftClientkMap.get(id).generator(id);
    } catch (TException e) {
      return false;
    }
  }

  @Override
  public boolean externalSource(String id) {
    try {
      return this.idThriftClientkMap.get(id).externalSource(id);
    } catch (TException e) {
      return false;
    }
  }

  @Override
  public boolean constant(String id) {
    try {
      return this.idThriftClientkMap.get(id).constant(id);
    } catch (TException e) {
      return false;
    }
  }

  private volatile int stopped = 0;

  @Override
  public void runnerStart(String id) {
    try {
      this.idThriftClientkMap.get(id).runnerStart(id);
    } catch (TException e) {
    }
  }

  @Override
  public void runnerStop(String id) {
    try {
      this.mainClient.runnerStop(id);
      stopped++;
      if (stopped == this.idThriftClientkMap.size()) {
        transport.close();
        if (process != null)
          process.destroy();
      }
    } catch (TException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void execute(String id, int grp) {
    try {
      this.idThriftClientkMap.get(id).execute(id, grp);
    } catch (TException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void executeAsync(String id, String portId) {
    try {
      this.idThriftClientkMap.get(id).executeAsync(id, portId);
    } catch (TException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void executeExternalSrc(String id) {
    try {
      this.idThriftClientkMap.get(id).executeExternalSrc(id);
    } catch (TException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void generate(String id) {
    try {
      this.idThriftClientkMap.get(id).generate(id);
    } catch (TException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void externalData(String taskid, int i) throws TException {
    idToPythonTaskMap.get(taskid).externalData(i);
  }

  @Override
  public void sleep(String taskid, long l) throws TException {
    idToPythonTaskMap.get(taskid).sleep(l);
  }

  @Override
  public void generatorPause(String taskid) throws TException {
    idToPythonTaskMap.get(taskid).generatorPause();
  }

  @Override
  public boolean generatorIsPaused(String taskid) throws TException {
    return idToPythonTaskMap.get(taskid).generatorIsPaused();
  }

  @Override
  public void generatorResume(String taskid) throws TException {
    idToPythonTaskMap.get(taskid).generatorResume();
  }

  @Override
  public void generatorEnd(String taskid) throws TException {
    idToPythonTaskMap.get(taskid).generatorEnd();
  }

  @Override
  public void exitRunner(String taskid) throws TException {
    idToPythonTaskMap.get(taskid).exitRunner();
  }

  @Override
  public String getParameter(String taskid, String key) throws TException {
    return idToPythonTaskMap.get(taskid).getParameter(key);
  }

  @Override
  public String runExpression(String taskid, String expression)
      throws TException {
    return idToPythonTaskMap.get(taskid).runExpression(expression).toString();
  }

  @Override
  public void log(String taskid, String level, String message)
      throws TException {
    idToPythonTaskMap.get(taskid).log(level, message);
  }

  @Override
  public RemoteData getData(String taskid, String portid, String datatype)
      throws TException {
    DataI d = idToPythonTaskMap.get(taskid).getData(portid);
    if (d != null)
      d = d.get(DataManager.INSTANCE.getDataFromName(datatype));
    return RemoteDataConverter.INSTANCE.toRemoteData(d);
  }

  @Override
  public void cleanQueue(String taskid, String portid) throws TException {
    idToPythonTaskMap.get(taskid).getInputPort(portid).removeAllData();
  }

  @Override
  public void putData(String taskid, String portid, RemoteData data)
      throws TException {
    DataI dataI = RemoteDataConverter.INSTANCE.fromRemoteData(data);
    PythonTaskI t = idToPythonTaskMap.get(taskid);
    if (t != null && dataI != null)
      t.putData(portid, dataI);
  }
}
