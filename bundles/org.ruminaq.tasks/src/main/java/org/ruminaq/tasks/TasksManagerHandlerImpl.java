/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.ruminaq.debug.api.dispatcher.EventDispatchJob;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.tasks.api.ITaskApi;
import org.ruminaq.tasks.api.TaskManagerHandler;

import ch.qos.logback.classic.Logger;

public class TasksManagerHandlerImpl implements TaskManagerHandler {

  private final Logger logger = ModelerLoggerFactory
      .getLogger(TasksManagerHandlerImpl.class);

  private List<String> taskJarsPaths = new ArrayList<>();

  public List<String> getTaskJarsPaths() {
    return taskJarsPaths;
  }

  public List<String> getPaths(Collection<ITaskApi> tasks) {
    List<String> paths = new ArrayList<>();
    for (ITaskApi ta : tasks) {
      Bundle b = FrameworkUtil.getBundle(ta.getClass());
      URL url = b.getEntry("/");
      if (url == null)
        continue;
      try {
        String p = FileLocator.toFileURL(url).getFile();
        logger.trace("add task classpath: {}", p);
        paths.add(p);
        if (Platform.inDevelopmentMode())
          paths.add(p + "target/classes");
      } catch (IOException e) {
      }
    }
    return paths;
  }

  public List<IDebugTarget> getDebugTargets(ILaunch launch, IProject project,
      EventDispatchJob dispatcher) {
    List<IDebugTarget> dts = new LinkedList<>();
    return dts;
  }

  public Version getProjectVersionForTask(String bundlePrefix) {
//		for (ITaskApi ta : projectVersionTasks)
//			if (ta.getSymbolicName().equals(bundlePrefix))
//				return ta.getVersion();
    return null;
  }

  public void addToGeneralTab(Composite composite, final Task task,
      final TransactionalEditingDomain ed) {
    CLabel lblVersion;
    CLabel lblVersionValue;

    final Button btnOnlyLocalDefault;
    final Button btnOnlyLocal;

    // initLayout
    lblVersion = new CLabel(composite, SWT.NONE);
    lblVersionValue = new CLabel(composite, SWT.NONE);
    btnOnlyLocalDefault = new Button(composite, SWT.CHECK);
    btnOnlyLocal = new Button(composite, SWT.CHECK);

    // initComponents
    lblVersion.setText("Task version:");
//    lblVersionValue.setText(task.getVersion());
    btnOnlyLocalDefault.setText("Only local - default");
    btnOnlyLocal.setText("Only local");

    // initActions
    btnOnlyLocal.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent se) {
        ModelUtil.runModelChange(new Runnable() {
          @Override
          public void run() {
            task.setOnlyLocalUser(btnOnlyLocal.getSelection());
          }
        }, ed, "Model Update");
      }
    });

    // addStyles
    lblVersion
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    lblVersionValue
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
  }

  public LinkedHashSet<String> getProgramArguments(IProject p) {
    LinkedHashSet<String> ret = new LinkedHashSet<>();
    return ret;
  }
}
