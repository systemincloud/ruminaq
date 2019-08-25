/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.runner.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.graphiti.mm.MmPackage;
import org.ruminaq.eclipse.ConstantsUtil;
import org.ruminaq.model.ruminaq.Connection;
import org.ruminaq.model.ruminaq.EmbeddedTask;
import org.ruminaq.model.ruminaq.FlowSource;
import org.ruminaq.model.ruminaq.FlowTarget;
import org.ruminaq.model.ruminaq.InputPort;
import org.ruminaq.model.ruminaq.InternalInputPort;
import org.ruminaq.model.ruminaq.InternalOutputPort;
import org.ruminaq.model.ruminaq.InternalPort;
import org.ruminaq.model.ruminaq.MainTask;
import org.ruminaq.model.ruminaq.OutputPort;
import org.ruminaq.model.ruminaq.RuminaqPackage;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.runner.RunnerException;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.debug.DebugI;
import org.ruminaq.runner.impl.debug.events.IDebugEvent;
import org.ruminaq.runner.impl.debug.events.debugger.NewTaskEvent;
import org.ruminaq.runner.util.Observable;
import org.ruminaq.runner.util.Observer;
import org.ruminaq.util.GlobalUtil;
import org.slf4j.Logger;

public class EmbeddedTaskI extends TaskI {

  private final Logger logger = RunnerLoggerFactory
      .getLogger(EmbeddedTaskI.class);

  private List<TaskI> tasks = new LinkedList<TaskI>(); // Collections.synchronizedList(new
                                                       // LinkedList<TaskI>());
  private List<BasicTaskI> constants = new LinkedList<BasicTaskI>(); // Collections.synchronizedList(new
                                                                     // LinkedList<BasicTaskI>());
  private List<GeneratorI> generators = new LinkedList<GeneratorI>(); // Collections.synchronizedList(new
                                                                      // LinkedList<GeneratorI>());

  private String diagramPath;

  public String getDiagramPath() {
    return diagramPath;
  }

  private HashMap<String, String> parameters = new HashMap<>();

  private boolean inCloud = false;
  private boolean runOnlyLocal = false;

  private boolean hasExternalSource = false;
  private boolean syncConns = true;

  public boolean hasExternalSource() {
    return hasExternalSource;
  }

  private void setHasExternalSourceParents(boolean b) {
    this.hasExternalSource = b;
    if (parent != null)
      parent.setHasExternalSourceParents(b);
  }

  public boolean getSyncConns() {
    return syncConns;
  }

  @Override
  public boolean isRunning() {
    return false;
  }

  public void executeConstants() {
    logger.trace("executeConstants");
    for (BasicTaskI c : constants)
      c.executeConstant();
    for (TaskI t : tasks)
      if (t instanceof EmbeddedTaskI)
        ((EmbeddedTaskI) t).executeConstants();
  }

  public Lock getReadyLock() {
    return parent.getReadyLock();
  }
//    public void hangEngine()   { parent.hangEngine(); }

  public EmbeddedTaskI(EmbeddedTaskI parent, Task task, String path,
      Map<String, String> params, boolean inCloud, boolean runOnlyLocal) {
    super(parent, task);
    this.diagramPath = path;
    this.inCloud = inCloud;
    this.runOnlyLocal = runOnlyLocal;
    MainTask mainTask = loadTask(path);
    if (mainTask == null) {
      throw new ImplementationException(
          "Can't load " + ((EmbeddedTask) task).getImplementationTask());
    }
    if (params != null) {
      parameters.putAll(params);
    } else {
      parameters.putAll(mainTask.getParameters());
    }
    this.syncConns = mainTask.isPreventLosts();

    createImplementation(mainTask);
  }

  public EmbeddedTaskI() {
    super(null, null);
  } // for engine adapter only

  @Override
  public LinkedList<TaskI> getReadyTasks() {
    logger.trace("{}: getReadyTasks", this.getId());
    LinkedList<TaskI> readyTasks = new LinkedList<>();
    for (TaskI task : tasks)
      if (task.isReady()) {
        logger.trace("{}:{}: is ready", task.getClass().getSimpleName(),
            task.getId());
        readyTasks.addAll(task.getReadyTasks());
      }

    if (readyTasks.size() == 0) {
      logger.trace("{}: No ready or running tasks inside", this.getId());
      setReady(false);
      if (isAtomic()) {
        // Hang if there are hanging constants due to synchronization
//                hangOnHangingConsts();
        // If block is atomic and there left some atomic blocks with some inputs
        // changed set them unchanged
        checkHangingInputs();
        // If block is atomic and there are some loops inside
        checkLoops();
      }
    }
    return readyTasks;
  }

  private void addGenerator(GeneratorI impl) {
    generators.add(impl);
  }

  private void addConstant(BasicTaskI impl) {
    constants.add(impl);
  }

  private void addTask(TaskI impl) {
    tasks.add(impl);
    if (impl.isExternalSoure())
      setHasExternalSourceParents(true);
  }

  public TaskI getTask(String id) {
    for (TaskI t : tasks)
      if (t.getId().equals(id))
        return t;
    for (TaskI t : constants)
      if (t.getId().equals(id))
        return t;
    for (TaskI t : generators)
      if (t.getId().equals(id))
        return t;
    return null;
  }

  @Override
  public List<? extends GeneratorI> getAllGenerators() {
    List<GeneratorI> allGenerators = new ArrayList<>();
    for (TaskI t : tasks)
      allGenerators.addAll(t.getAllGenerators());
    allGenerators.addAll(generators);
    return allGenerators;
  }

//    private void hangOnHangingConsts() {
//        logger.trace("hangOnHangingConsts");
//        for(TaskI t : tasks) {
//            if(t instanceof EmbeddedTaskI && !((EmbeddedTaskI) t).isAtomic())
//                ((EmbeddedTaskI) t).hangOnHangingConsts();
//        }
//        for(TaskI t : constants) {
//            for(LinkedList<InternalOutputPortI> c : t.getSyncOuts().values())
//                for(InternalOutputPortI iop : c)
//                    if(iop.isWaiting()) parent.hangEngine();
//        }
//    }

  public void setInputsUnchanged() {
    // TODO Auto-generated method stub
  }

  public void checkHangingInputs() {
    // TODO Auto-generated method stub
  }

  public void checkLoops() {
    // TODO Auto-generated method stub
  }

  private void createImplementation(MainTask mainTask) {
    Map<InternalInputPort, InternalInputPortI> inputsTmp = new HashMap<>();
    Map<InternalOutputPort, InternalOutputPortI> outputsTmp = new HashMap<>();

    for (Task task : mainTask.getTask()) {
      if (isOnlyLocal(task) && inCloud)
        continue;
      if (isOnlyLocal(task) && !runOnlyLocal
          && !ConstantsUtil.isTest(task.eResource().getURI(), getBasePath()))
        continue;

      TaskI taskI = TaskImplementationFactory.INSTANCE.getImplementation(this,
          task, getBasePath(), inCloud, runOnlyLocal);
      if (taskI == null)
        throw new RunnerException(
            "No implementation found for task: " + task.getId());

      if (taskI.isConstant()) {
        logger.trace("Add constant {}", taskI);
        addConstant((BasicTaskI) taskI);
      } else if (taskI.isGenerator()) {
        logger.trace("Add generator {}", taskI);
        addGenerator((GeneratorI) taskI);
        if (taskI.getInternalInputPorts().size() > 0) {
          logger.trace("Add task {}", taskI);
          addTask(taskI);
        }
      } else {
        logger.trace("Add task {}", taskI);
        addTask(taskI);
      }
      DebugI.INSTANCE.debug(new NewTaskEvent(taskI, diagramPath));

      for (InternalInputPortI iip : taskI.getInternalInputPorts()) {
        inputsTmp.put(iip.getModel(), iip);
      }
      for (InternalOutputPortI iop : taskI.getInternalOutputPorts()) {
        outputsTmp.put(iop.getModel(), iop);
      }
    }
    logger.trace(
        "EmbeddedTask has {} task(s), {} generator(s), "
            + "{} input(s), {} output(s)",
        tasks.size(), generators.size(), internalInputPorts.size(),
        internalOutputPorts.size());
    // connections
    for (Connection c : mainTask.getConnection()) {
      logger.trace("Add connection ...");
      FlowTarget ft = c.getTargetRef();
      Observer target = inputsTmp.get(ft);
      if (target == null && ft instanceof OutputPort) {
        for (InternalOutputPortI iop : internalOutputPorts.values())
          if (iop.getPortId().equals(((OutputPort) ft).getId())) {
            target = iop;
            logger.trace("... to {}:{}",
                ((InternalOutputPortI) target).getTaskId(),
                ((InternalOutputPortI) target).getPortId());
            break;
          }
      } else if (target != null)
        logger.trace("... to {}:{}", ((InternalInputPortI) target).getTaskId(),
            ((InternalInputPortI) target).getPortId());
      if (target == null)
        continue;

      FlowSource st = c.getSourceRef();
      Observable source = outputsTmp.get(st);
      if (source != null) {
        logger.trace("... from {}:{}",
            ((InternalOutputPortI) source).getTaskId(),
            ((InternalOutputPortI) source).getPortId());
        source.addObserver(target);
      } else if (st instanceof InputPort) {
        for (InternalInputPortI iip : internalInputPorts.values()) {
          if (iip.getPortId().equals(((InputPort) c.getSourceRef()).getId())) {
            source = iip;
            break;
          }
        }
        if (source != null) {
          logger.trace("... from {}:{}",
              ((InternalInputPortI) source).getTaskId(),
              ((InternalInputPortI) source).getPortId());
          source.addObserver(target);
        }
      }
    }

    // synchronization
    for (TaskI t : tasks)
      synchronizationInit(t);
    for (TaskI t : constants)
      synchronizationInit(t);
    for (TaskI t : generators)
      synchronizationInit(t);
  }

  private boolean isOnlyLocal(Task task) {
    return task.isOnlyLocalDefault() ? task.isOnlyLocal()
        : task.isOnlyLocalUser();
  }

  private void synchronizationInit(TaskI t) {
    Set<Integer> grps = new HashSet<Integer>();

    for (InternalOutputPortI o : t.getInternalOutputPorts())
      if (o.isSynchronized()) {
        logger.trace("{}:{}: is synchronized", o.getTaskId(), o.getPortId());
        for (SynchronizationI s : o.getSyncs()) {
          int group = s.getSyncGroup();
          grps.add(Integer.valueOf(group));
          logger.trace("... group is {}", group);
          InternalPort p1 = s.getSyncPort();
          logger.trace("... port is {}", p1 == null ? "NONE" : p1.getId());
          if (p1 != null) {
            String taskId = p1.getParent().getId();
            logger.trace("... its Task is {}", taskId);
            if (p1 instanceof InternalInputPort) {
              InternalInputPortI ip = getTask(taskId).getInputPort(p1.getId());
              logger.trace("Found sync port impl: {}", ip.getPortId());
              ip.addSyncOut(s);
            } else if (p1 instanceof InternalOutputPort) {
              InternalOutputPortI op = getTask(taskId)
                  .getOutputPort(p1.getId());
              logger.trace("Found sync port impl: {}", op.getPortId());
              op.addSyncOut(s);
            }
          }
        }

        InternalPort p2 = o.getResetSyncPort();
        logger.trace("... rst port is {}", p2 == null ? "NONE" : p2.getId());
        if (p2 != null) {
          String taskId = p2.getParent().getId();
          logger.trace("... its Task is {}", taskId);
          if (p2 instanceof InternalInputPort) {
            InternalInputPortI ip = getTask(taskId).getInputPort(p2.getId());
            logger.trace("Found rst skipped port impl: {}", ip.getPortId());
            ip.addResetSync(o);
          } else if (p2 instanceof InternalOutputPort) {
            InternalOutputPortI op = getTask(taskId).getOutputPort(p2.getId());
            logger.trace("Found rst skipped port impl: {}", op.getPortId());
            op.addResetSync(o);
          }
        }
      }
    t.setSyncGroups(grps);
  }

  private MainTask loadTask(String path) {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
        Resource.Factory.Registry.DEFAULT_EXTENSION,
        new XMIResourceFactoryImpl());
    RuminaqPackage.eINSTANCE.eClass();
    MmPackage.eINSTANCE.eClass();
    URI fileURI = URI.createFileURI(new File(path).getAbsolutePath());
    Resource resource = resourceSet.getResource(fileURI, true);
    MainTask mt = null;
    if (resource.getContents().size() > 0) {
      mt = (MainTask) resource.getContents().get(1);
      logger.trace("Task loaded: {}", mt);
    }
    return mt;
  }

  @Override
  public void portManager(InternalInputPortI input) {
    if (!isAtomic() || input.isAsynchronous()) {
      input.reserveData();
      input.putData(input.getData());
    } else {
      logger.trace("{}:{}: synchronous port", this.getId(), input.getPortId());
      Map<Integer, Boolean> grps = new HashMap<>();
      for (InternalInputPortI iip : internalInputPorts.values()) {
        if (!iip.isAsynchronous()) {
          if (!grps.containsKey(iip.getGroup()))
            grps.put(iip.getGroup(), true);
          if (iip.hasData())
            grps.put(iip.getGroup(), true && grps.get(iip.getGroup()));
          else
            grps.put(iip.getGroup(), false);
        }
      }
      for (Entry<Integer, Boolean> grp : grps.entrySet())
        if (grp.getValue()) {
          logger.trace("{}: ready", this.getId());
          for (InternalInputPortI iip : internalInputPorts.values()) {
            if (iip.isAsynchronous())
              continue;
            if (iip.getGroup() != grp.getKey().intValue())
              continue;
            logger.trace("{}:{}: forward port", this.getId(), iip.getPortId());
            iip.reserveData();
            iip.putData(iip.getData());
          }
        }
    }
  }

  public String replaceVariables(String string) {
    logger.trace("replaceVariables {}", string);
    String ret = string;
    String tmp;
    StringBuffer sb = new StringBuffer();
    Matcher m = Pattern.compile(GlobalUtil.GV).matcher(string);
    int lastIdx = 0;
    while (m.find()) {
      int start = m.start();
      if (start != lastIdx)
        sb.append(ret.substring(lastIdx, start));
      lastIdx = m.end();
      String var = m.group();
      String param = getParameter(var.substring(2, var.length() - 1));
      if (param != null)
        sb.append(param);
    }
    sb.append(ret.substring(lastIdx));
    tmp = sb.toString();
    if (!"".equals(tmp))
      ret = tmp;
    return ret;
  }

  public String getInputArgument(String name) {
    return parent.getInputArgument(name);
  }

  public String getParameter(String key) {
    if (!parameters.containsKey(key)) {
      System.err
          .println("No parameter " + key + " defined in task " + this.getId());
      logger.error("{} : no parameters {}", this.getId(), key);
      System.exit(1);
    }
    return parent.replaceVariables(parameters.get(key));
  }

  @Override
  public void runnerStart() {
    logger.trace("{} : runnerStart", this.getId());
    tasks.forEach(TaskI::runnerStart);
    constants.forEach(TaskI::runnerStart);
    generators.stream().filter(t -> !tasks.contains(t))
        .forEach(TaskI::runnerStart);
  }

  @Override
  public void runnerStop() {
    logger.trace("{} : runnerStop", this.getId());
    tasks.forEach(TaskI::runnerStop);
    constants.forEach(TaskI::runnerStop);
    generators.stream().filter(t -> !tasks.contains(t))
        .forEach(TaskI::runnerStop);
  }

  public String getBasePath() {
    return parent.getBasePath();
  }

  @Override
  public void modelEvent(IDebugEvent event) {
    super.modelEvent(event);
    tasks.forEach(t -> t.modelEvent(event));
    constants.forEach(t -> t.modelEvent(event));
    generators.forEach(t -> t.modelEvent(event));
  }

  @Override
  public void initDebugers() {
    super.initDebugers();
    tasks.forEach(TaskI::initDebugers);
    constants.forEach(TaskI::initDebugers);
    generators.forEach(TaskI::initDebugers);
  }

  @Override
  public ExecutionReport call() throws Exception {
    throw new RuntimeException("EmbeddedTask in thread !!!");
  } // never called
}
