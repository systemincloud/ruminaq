package org.ruminaq.runner.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.ruminaq.model.model.ruminaq.InternalInputPort;
import org.ruminaq.model.model.ruminaq.InternalOutputPort;
import org.ruminaq.model.model.ruminaq.Task;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.debug.events.IDebugEvent;
import org.slf4j.Logger;

public abstract class TaskI implements Callable<ExecutionReport> {

	private final Logger logger = RunnerLoggerFactory.getLogger(TaskI.class);

	protected EmbeddedTaskI parent;
	public    EmbeddedTaskI getParent() { return parent; }
	private   Task model;

	public Task   getModel() { return model; }
	public String getId()    { return model != null ? model.getId() : ""; }

	private volatile boolean ready   = false;
	private volatile boolean running = false;
	private volatile int     priority = 0;

	protected boolean atomic         = true;
	protected boolean looped         = false;
	protected boolean generator      = false;
	protected boolean constant       = false;
	protected boolean externalSource = false;

	protected Map<String, InternalInputPortI>  internalInputPorts  = new HashMap<>();
	protected Map<String, InternalOutputPortI> internalOutputPorts = new HashMap<>();
	public InternalInputPortI  getInputPort (String id) { return internalInputPorts .get(id); }
	public InternalOutputPortI getOutputPort(String id) { return internalOutputPorts.get(id); }

	protected Lock      lockOutputs = new ReentrantLock();
	protected Lock      lockInputs  = new ReentrantLock();
	protected Condition outputsSync = lockOutputs.newCondition();
	public    Lock      getOutputsLock() { return lockOutputs; }
	public    Lock      getInputsLock()  { return lockInputs; }
	public    Condition getOutputsSync() { return outputsSync; }

	private Set<Integer> syncGrps;
	public void setSyncGroups(Set<Integer> syncGrps) { this.syncGrps = syncGrps; }

	private   Map<InternalOutputPortI, Boolean> toSend = Collections.synchronizedMap(new HashMap<InternalOutputPortI, Boolean>());
	protected void resetSync()                { toSend = Collections.synchronizedMap(new HashMap<InternalOutputPortI, Boolean>()); }

	public void putDataSync(InternalOutputPortI iop) {
		if(toSend.get(iop) == null) toSend.put(iop, false);
	}

	public void goSync(final InternalOutputPortI iop) {
		toSend.put(iop, true);
		send();
	}

	protected void send() {
		logger.trace("send");
		LinkedList<InternalOutputPortI> p0s = getCurrentSyncs(0);
		LinkedList<InternalOutputPortI> toRemove = new LinkedList<>();
		if(p0s != null) {
			logger.trace("{}:{}: check group 0", this.getClass().getSimpleName(), this.getId());
			for(InternalOutputPortI p0 : p0s) {
				Boolean b = toSend.get(p0);
				logger.trace("{} is {}", p0.getPortId(), b);
				if (b == null) continue;
				if (b == true && p0.getCurrentSync().getSyncPort() != null) {
					p0.putDataSync();
					toRemove.add(p0);
				}
			}
			toRemove.clear();
			outputsSync.signal();
		}

		for(Integer i : syncGrps) {
			logger.trace("{}:{}: check group {}", this.getClass().getSimpleName(), this.getId(), i.intValue());
			LinkedList<InternalOutputPortI> ps = getCurrentSyncs(i.intValue());
			if(ps == null) continue;
			for(InternalOutputPortI p : ps) {
				Boolean b = toSend.get(p);
				if(b == null) return;
				if(b == false && p.getCurrentSync().getSyncPort() != null) return;
			}
			// send
			logger.trace("{}:{}: send group {}", this.getClass().getSimpleName(), this.getId(), i.intValue());
			ps.forEach(InternalOutputPortI::putDataSync);
			toSend.clear();
			outputsSync.signal();
		}
	}

	private LinkedList<InternalOutputPortI> getCurrentSyncs(int i) {
		LinkedList<InternalOutputPortI> ret = new LinkedList<>();
		for (InternalOutputPortI iop : internalOutputPorts.values())
			if (iop.isSynchronized() && iop.getCurrentSync().getSyncGroup() == i) {
                ret.add(iop);
			}
		return ret;
	}

	protected boolean isWaitingForSync() {
		for (InternalOutputPortI p : internalOutputPorts.values())
			if (p.isWaiting()) {
				logger.trace("{}:{}:{} is waiting", this.getClass().getSimpleName(), this.getId(), p.getPortId());
				return true;
			}
		return false;
	}

	public TaskI(EmbeddedTaskI parent, Task task) {
		this.parent = parent;
		if(task == null) return;
		this.model  = task;
		this.atomic = task.isAtomic();
		this.looped = task.isLooped();

		logger.trace("{}:{}: atomic {}", this.getClass().getSimpleName(), this.getId(), this.atomic);
		logger.trace("{}:{}: looped {}", this.getClass().getSimpleName(), this.getId(), this.looped);

		for(InternalInputPort  ip : task.getInputPort())  internalInputPorts .put(ip.getId(), new InternalInputPortI(ip, this));
		for(InternalOutputPort op : task.getOutputPort()) internalOutputPorts.put(op.getId(), new InternalOutputPortI(op, this));
	}

	public boolean isReady()                            { return ready; }
	public void    setReady           (boolean ready)   { this.ready = ready; }
	public void    setReadyWithParents(boolean ready)   {
		parent.getReadyLock().lock();
		setReady(ready); parent.setReadyWithParents(ready);
		parent.getReadyLock().unlock();
	}
//	public void signalEngine() {
//		parent.getReadyLock().lock();
//		parent.signalEngine();
//		parent.getReadyLock().unlock();
//	}

	public void    breakRunner()                        { parent.breakRunner(); }
	public boolean isRunning()                          { return running; }
	public void    setRunning         (boolean running) { this.running = running; }
	public    int  getPriority()                        { return priority; }
	protected void setPriority        (int priority)    { this.priority = priority; }

	public boolean isAtomic()        { return atomic; }
	public boolean isLooped()        { return looped; }
	public boolean isGenerator()     { return generator; }
	public boolean isConstant()      { return constant; }
	public boolean isExternalSoure() { return externalSource; }

	/**
     * Implementation of task can mark in its constructor that is 'atomic'
     *
     * @param value
     */
    protected final void setAtomic(boolean value) {
        this.atomic = value;
    }

	/**
     * Implementation of task can mark in its constructor that is of type 'constant'
     *
     * @param value
     */
	protected final void setConstant(boolean value) {
	    this.constant = value;
	}

	/**
     * Implementation of task can mark in its constructor that is of type 'external source'
     *
     * @param value
     */
	protected final void setExternalSource(boolean value) {
	    this.externalSource = value;
	}

	/**
	 * Implementation of task can mark in its constructor that is of type 'generator'
	 *
	 * @param value
	 */
	protected final void setGenerator(boolean value) {
	    this.generator = value;
	}

	public Collection<InternalInputPortI>  getInternalInputPorts()  {
	    return internalInputPorts .values();
	}

	public Collection<InternalOutputPortI> getInternalOutputPorts() {
	    return internalOutputPorts.values();
	}

	public abstract List<? extends TaskI> getReadyTasks();

	public List<? extends GeneratorI> getAllGenerators() { return new ArrayList<>(); }

	public abstract void portManager(InternalInputPortI internalInputPortI);

	public void intervalShortenning(InternalInputPortI internalInputPortI) {
		// TODO: priority ??
	}

	public void dataOverwrittenOrLost(InternalInputPortI internalInputPortI) {
		// TODO: warning ??
	}

	public void runnerStart() { }
	public void runnerStop()  { }

	public void modelEvent(IDebugEvent event) {
		for(InternalInputPortI  ip : internalInputPorts .values()) ip.modelEvent(event);
		for(InternalOutputPortI ip : internalOutputPorts.values()) ip.modelEvent(event);
	}

	public void spreadDebugEvent(IDebugEvent event) { parent.spreadDebugEvent(event); }

	public void initDebugers() {
		for(InternalInputPortI  ip : internalInputPorts .values()) ip.initDebugers();
		for(InternalOutputPortI ip : internalOutputPorts.values()) ip.initDebugers();
	}
}
