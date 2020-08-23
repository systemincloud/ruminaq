/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.pythontask.ui.debug;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.ruminaq.debug.api.IEventProcessor;
import org.ruminaq.debug.model.MainState;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.runner.impl.debug.events.IDebugEvent;
import org.ruminaq.runner.impl.debug.events.debugger.NewTaskEvent;

import ch.qos.logback.classic.Logger;

public class PythonTask extends PythonTasksDebugElement
    implements IThread, IEventProcessor {

  private final Logger logger = ModelerLoggerFactory
      .getLogger(PythonTask.class);

  private String id;
  private String name;
  private String parentPath;
  private IFile file;

  public String getParentPath() {
    return parentPath;
  }

  public String getId() {
    return id;
  }

  protected PythonTask(IDebugTarget target, IProject project,
      NewTaskEvent event) {
    super(target);
    this.id = event.getId();
    this.name = event.getFullId();
    this.parentPath = event.getParentPath();
    for (IProject p : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
      String prefix = p.getLocation().removeLastSegments(1).toFile()
          .getAbsolutePath();
      if (parentPath.startsWith(prefix)) {
        logger.trace("prefix : ", prefix);
        logger.trace("parentPath : ", parentPath);
        this.file = ResourcesPlugin.getWorkspace().getRoot()
            .getFile(new Path(parentPath.replace(prefix, "")));
        break;
      }
    }

    setState(MainState.RUNNING);
  }

  @Override
  public String getName() throws DebugException {
    return name;
  }

  @Override
  public int getPriority() throws DebugException {
    return 0;
  }
//	@Override public IStackFrame[] getStackFrames()   throws DebugException { return ports.toArray(new IStackFrame[ports.size()]); }

//	@Override public IStackFrame   getTopStackFrame() throws DebugException { return !ports.isEmpty() ? ports.get(0) : null; }
//	@Override public boolean       hasStackFrames()   throws DebugException { return !ports.isEmpty(); }

  @Override
  public IBreakpoint[] getBreakpoints() {
    List<IBreakpoint> ret = new LinkedList<>();
//		for(Port p : ports)
//			ret.addAll(p.getBreakpoints());
    return ret.toArray(new IBreakpoint[ret.size()]);
  }

  public IFile getFile() {
    return file;
  }

  @Override
  public void handleEvent(IDebugEvent event) {
  }

  @Override
  public IStackFrame[] getStackFrames() throws DebugException {
    return null;
  }

  @Override
  public IStackFrame getTopStackFrame() throws DebugException {
    return null;
  }

  @Override
  public boolean hasStackFrames() throws DebugException {
    return false;
  }
}
