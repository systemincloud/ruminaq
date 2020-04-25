/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.ruminaq.consts.Constants;
import org.ruminaq.debug.api.dispatcher.EventDispatchJob;
import org.ruminaq.eclipse.RuminaqDiagramUtil;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.tasks.api.ITaskApi;
import org.ruminaq.tasks.api.TaskManagerHandler;
import org.ruminaq.tasks.api.TasksExtensionHandler;
import ch.qos.logback.classic.Logger;

@SuppressWarnings("unchecked")
public class TasksManagerHandlerImpl implements TaskManagerHandler {

  private final Logger logger = ModelerLoggerFactory
      .getLogger(TasksManagerHandlerImpl.class);

  private TasksExtensionHandler extensions;

  private Collection<ITaskApi> tasks;
  private Collection<ITaskApi> projectVersionTasks;

  @Reference(cardinality = ReferenceCardinality.MULTIPLE,
      policy = ReferencePolicy.DYNAMIC)
  protected void bind(ITaskApi extension) {
    if (tasks == null) {
      tasks = new ArrayList<>();
    }
    if (projectVersionTasks == null) {
      projectVersionTasks = new ArrayList<>();
    }
    tasks.add(extension);

//		String version = extension.getVersion().getMajor() + "."
//		    + extension.getVersion().getMinor() + "."
//		    + extension.getVersion().getMicro();
//		if (version.equals(nameVersion.get(extension.getSymbolicName()))) {
//			projectVersionTasks.add(extension);
//		}

    taskJarsPaths.addAll(getPaths(tasks));
  }

  protected void unbind(ITaskApi extension) {
    tasks.remove(extension);
    projectVersionTasks.remove(extension);
  }

  private JSONParser parser = new JSONParser();
  private Map<String, String> nameVersion = new HashMap<>();

  private List<String> taskJarsPaths = new ArrayList<>();

  public Collection<ITaskApi> getTasks() {
    return tasks;
  }

  public Collection<ITaskApi> getProjectVersionTasks() {
    return projectVersionTasks;
  }

  public List<String> getTaskJarsPaths() {
    return taskJarsPaths;
  }

  public void init(BundleContext ctx) {
    JSONObject listJson = null;

    try {
      listJson = (JSONObject) parser.parse(new InputStreamReader(
          TasksManagerHandlerImpl.class.getResourceAsStream("/list.json")));
    } catch (IOException | ParseException e) {
      e.printStackTrace();
    }

    JSONArray categories = (JSONArray) listJson.get("categories");
    Iterator<JSONObject> categoryIt = categories.iterator();
    while (categoryIt.hasNext()) {
      JSONObject category = categoryIt.next();
      JSONArray tasks = (JSONArray) category.get("tasks");
      if (tasks != null) {
        Iterator<JSONObject> taskIt = tasks.iterator();
        while (taskIt.hasNext()) {
          JSONObject task = taskIt.next();
          String m2 = (String) task.get("m2");
          String[] tmp = m2.split(":");
          nameVersion.put(tmp[1], tmp[2].replace(Constants.SNAPSHOT, ""));
        }
      }
    }

    extensions.init(ctx);
    for (String s : extensions.getListJson()) {
      if (s == null)
        continue;
      listJson = null;
      try {
        listJson = (JSONObject) parser.parse(s);
      } catch (ParseException e) {
        continue;
      }
      JSONArray tasks = (JSONArray) listJson.get("tasks");
      if (tasks != null) {
        Iterator<JSONObject> taskIt = tasks.iterator();
        while (taskIt.hasNext()) {
          JSONObject task = taskIt.next();
          String m2 = (String) task.get("m2");
          String[] tmp = m2.split(":");
          nameVersion.put(tmp[1], tmp[2].replace(Constants.SNAPSHOT, ""));
        }
      }
    }
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
    projectVersionTasks.stream().forEach(
        ta -> dts.addAll(ta.getDebugTargets(launch, project, dispatcher)));
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
    btnOnlyLocalDefault.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent se) {
        ModelUtil.runModelChange(new Runnable() {
          @Override
          public void run() {
            task.setOnlyLocalDefault(btnOnlyLocalDefault.getSelection());
            btnOnlyLocal.setEnabled(!btnOnlyLocalDefault.getSelection());
            if (btnOnlyLocalDefault.getSelection()) {
              task.setOnlyLocalUser(task.isOnlyLocal());
              btnOnlyLocal.setSelection(task.isOnlyLocal());
            }
          }
        }, ed, "Model Update");
      }
    });
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

    // refresh
    if (RuminaqDiagramUtil.isTest(task.eResource().getURI())) {
      btnOnlyLocalDefault.setVisible(false);
      btnOnlyLocal.setVisible(false);
    } else {
      if (task.isOnlyLocal()) {
        btnOnlyLocalDefault.setSelection(true);
        btnOnlyLocalDefault.setEnabled(false);
        btnOnlyLocal.setEnabled(false);
      } else {
        btnOnlyLocalDefault.setSelection(task.isOnlyLocalDefault());
        btnOnlyLocal.setSelection(task.isOnlyLocalUser());
        btnOnlyLocal.setEnabled(!task.isOnlyLocalDefault());
      }
    }
  }

  public LinkedHashSet<String> getProgramArguments(IProject p) {
    LinkedHashSet<String> ret = new LinkedHashSet<>();
    for (ITaskApi ta : projectVersionTasks) {
      LinkedHashSet<String> s = ta.getProgramArguments(p);
      if (s != null)
        ret.addAll(s);
    }
    return ret;
  }
}
