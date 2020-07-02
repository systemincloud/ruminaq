/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.javatask.gui.debug;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.ruminaq.debug.model.vars.SicVariable;

public class JavaTasksDebugModelPresentation
    implements IDebugModelPresentation {

  public static final String ID = "org.ruminaq.tasks.javatask.ui.debug.JavaTasksDebugModelPresentation";

  private static final Image TARGET_TERMINATED = getImage(
      "target_terminated.png");
  private static final Image TARGET_PARTLY_SUSPENDED = getImage(
      "target_partly_suspended.png");
  private static final Image TARGET_RUNNING = getImage("target_running.png");
  private static final Image JAVATASK_SUSPENDED = getImage(
      "task_suspended.png");
  private static final Image JAVATASK_STEPPING = getImage("task_stepping.png");
  private static final Image JAVATASK_RUNNING = getImage("task_running.png");

  public static Image getImage(String file) {
    Bundle bundle = FrameworkUtil
        .getBundle(JavaTasksDebugModelPresentation.class);
    URL url = FileLocator.find(bundle, new Path("icons/" + file), null);
    ImageDescriptor image = ImageDescriptor.createFromURL(url);
    return image.createImage();
  }

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
    return null;
  }

  @Override
  public String getEditorId(IEditorInput input, Object element) {
    return null;
  }

  @Override
  public void setAttribute(String attribute, Object value) {
  }

  @Override
  public Image getImage(Object element) {
    if (element instanceof JavaTasksDebugTarget) {
      JavaTasksDebugTarget target = (JavaTasksDebugTarget) element;
      if (target.isTerminated())
        return TARGET_TERMINATED;
      if (target.hasSuspended())
        return TARGET_PARTLY_SUSPENDED;
      return TARGET_RUNNING;
    } else if (element instanceof JavaTask) {
      JavaTask task = (JavaTask) element;
      if (task.isSuspended())
        return JAVATASK_SUSPENDED;
      if (task.isStepping())
        return JAVATASK_STEPPING;
      return JAVATASK_RUNNING;
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
