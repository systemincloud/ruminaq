package org.ruminaq.tasks.debug.model;

import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.ruminaq.debug.model.vars.Data;
import org.ruminaq.debug.model.vars.KeyValueGroupVariable;
import org.ruminaq.debug.model.vars.SicVariable;
import org.ruminaq.tasks.debug.model.port.in.DataQueueVariable;
import org.ruminaq.tasks.debug.model.port.in.InputPort;
import org.ruminaq.tasks.debug.model.port.out.OutputPort;

public class TasksDebugModelPresentation implements IDebugModelPresentation {

  public static Image getImage(String file) {
    Bundle bundle = FrameworkUtil.getBundle(TasksDebugModelPresentation.class);
    URL url = FileLocator.find(bundle, new Path("icons/" + file), null);
    ImageDescriptor image = ImageDescriptor.createFromURL(url);
    return image.createImage();
  }

  public static final String ID = "org.ruminaq.tasks.debug.model.TasksDebugModelPresentation";

  private static final Image TARGET_TERMINATED = getImage(
      "target_terminated.png");
  private static final Image TARGET_SUSPENDED = getImage(
      "target_suspended.png");
  private static final Image TARGET_PARTLY_SUSPENDED = getImage(
      "target_partly_suspended.png");
  private static final Image TARGET_STEPPING = getImage("target_stepping.png");
  private static final Image TARGET_RUNNING = getImage("target_running.png");
  private static final Image TASK_TERMINATED = getImage("task_terminated.png");
  private static final Image TASK_SUSPENDED = getImage("task_suspended.png");
  private static final Image TASK_PARTLY_SUSPENDED = getImage(
      "task_partly_suspended.png");
  private static final Image TASK_STEPPING = getImage("task_stepping.png");
  private static final Image TASK_RUNNING = getImage("task_running.png");
  private static final Image OUT_TERMINATED = getImage("out_terminated.png");
  private static final Image OUT_SUSPENDED = getImage("out_suspended.png");
  private static final Image OUT_STEPPING = getImage("out_stepping.png");
  private static final Image OUT_RUNNING = getImage("out_running.png");
  private static final Image IN_TERMINATED = getImage("in_terminated.png");
  private static final Image IN_SUSPENDED = getImage("in_suspended.png");
  private static final Image IN_STEPPING = getImage("in_stepping.png");
  private static final Image IN_RUNNING = getImage("in_running.png");

  @Override
  public void addListener(ILabelProviderListener listener) {
  }

  @Override
  public void dispose() {
  }

  @Override
  public boolean isLabelProperty(Object element, String property) {
    return false;
  }

  @Override
  public void removeListener(ILabelProviderListener listener) {
  }

  @Override
  public IEditorInput getEditorInput(Object element) {
    if (element instanceof IFile)
      return new FileEditorInput((IFile) element);
    return null;
  }

  @Override
  public String getEditorId(IEditorInput input, Object element) {
    if (element instanceof IFile)
      return PlatformUI.getWorkbench().getEditorRegistry()
          .getDefaultEditor(((IFile) element).getName()).getId();
    return null;
  }

  @Override
  public void setAttribute(String attribute, Object value) {
  }

  @Override
  public Image getImage(Object element) {
    if (element instanceof TasksDebugTarget) {
      TasksDebugTarget target = (TasksDebugTarget) element;
      if (target.isTerminated())
        return TARGET_TERMINATED;
      if (target.isSuspended())
        return TARGET_SUSPENDED;
      if (target.hasSuspended())
        return TARGET_PARTLY_SUSPENDED;
      if (target.isStepping())
        return TARGET_STEPPING;
      return TARGET_RUNNING;
    } else if (element instanceof Task) {
      Task task = (Task) element;
      if (task.isTerminated())
        return TASK_TERMINATED;
      if (task.isSuspended())
        return TASK_SUSPENDED;
      if (task.hasSuspended())
        return TASK_PARTLY_SUSPENDED;
      if (task.isStepping())
        return TASK_STEPPING;
      return TASK_RUNNING;
    } else if (element instanceof OutputPort) {
      OutputPort port = (OutputPort) element;
      if (port.isTerminated())
        return OUT_TERMINATED;
      if (port.isSuspended())
        return OUT_SUSPENDED;
      if (port.isStepping())
        return OUT_STEPPING;
      return OUT_RUNNING;
    } else if (element instanceof InputPort) {
      InputPort port = (InputPort) element;
      if (port.isTerminated())
        return IN_TERMINATED;
      if (port.isSuspended())
        return IN_SUSPENDED;
      if (port.isStepping())
        return IN_STEPPING;
      return IN_RUNNING;
    } else if (element instanceof SicVariable) {
      SicVariable var = (SicVariable) element;
      if (var instanceof KeyValueGroupVariable) {

      }
      if (var instanceof Data) {

      }
      if (var instanceof DataQueueVariable) {

      }
    }
    return null;
  }

  @Override
  public String getText(Object element) {
    return null;
  }

  @Override
  public void computeDetail(IValue value, IValueDetailListener listener) {
    if (value instanceof SicVariable)
      listener.detailComputed(value, ((SicVariable) value).getDetailText());
  }
}