/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.prefs;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.ruminaq.util.Result;
import org.ruminaq.util.Try;

/**
 * 
 * @author Marek Jagielski
 */
public abstract class AbstractProps implements IPreferenceChangeListener {

  private static final String PROPS_DIR = ".settings";

  private interface Strategy {

    void put(String key, String value);

    String get(String key, String defaultValue);

    List<String> getStartingBy(String prefix);

    void remove(String key);

    void saveProps();

  }

  private class Secured implements Strategy {

    private ISecurePreferences node;

    protected Secured(IFile props) {
      node = Result.attempt(() -> SecurePreferencesFactory.open(
          new URL("file:///" + props.getRawLocation().toFile().getPath()),
          new HashMap<>())).orElse(null);
    }

    @Override
    public void put(String key, String value) {
      Try.check(() -> node.put(key, value, true));
    }

    @Override
    public String get(String key, String defaultValue) {
      return Result.attempt(() -> node.get(key, defaultValue)).orElse(null);
    }

    @Override
    public List<String> getStartingBy(String prefix) {
      return Stream.of(node.childrenNames()).filter(n -> n.startsWith(prefix))
          .collect(Collectors.toList());
    }

    @Override
    public void remove(String key) {
      node.remove(key);
    }

    @Override
    public void saveProps() {
      Try.check(() -> node.flush());
    }
  }

  private class Unsecured implements Strategy {

    private IEclipsePreferences node;

    protected Unsecured(ProjectScope ps, String name) {
      node = ps.getNode(name);
      node.addPreferenceChangeListener(AbstractProps.this);
      Try.check(() -> node.sync());
    }

    @Override
    public void put(String key, String value) {
      node.put(key, value);
    }

    @Override
    public String get(String key, String defaultValue) {
      return node.get(key, defaultValue);
    }

    @Override
    public List<String> getStartingBy(String prefix) {
      return Result.attempt(() -> Stream.of(node.childrenNames()))
          .orElseGet(Stream::empty).filter(n -> n.startsWith(prefix))
          .collect(Collectors.toList());
    }

    @Override
    public void remove(String key) {
      node.remove(key);
    }

    @Override
    public void saveProps() {
      Try.check(() -> node.flush());
    }
  }

  private IProject project;

  private Strategy strategy;

  protected AbstractProps(IProject project, String name, boolean secure) {
    this.project = project;
    IFolder setF = project.getFolder(PROPS_DIR);
    if (!setF.exists()) {
      Try.check(() -> setF.create(true, true, new NullProgressMonitor()));
    }

    IFile props = setF.getFile(name + ".prefs");
    if (!props.exists()) {
      Try.check(() -> props.create(
          new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)),
          IResource.FORCE, new NullProgressMonitor()));
    }

    if (secure) {
      this.strategy = new Secured(props);
    } else {
      this.strategy = new Unsecured(new ProjectScope(project), name);
    }
  }

  public void put(String key, String value) {
    strategy.put(key, value);
    saveProps();
  }

  public String get(String key) {
    return get(key, null);
  }

  public String get(String key, String defaultValue) {
    return strategy.get(key, defaultValue);
  }

  public List<String> getStartingBy(String prefix) {
    return strategy.getStartingBy(prefix);
  }

  public void remove(String key) {
    strategy.remove(key);
  }

  public synchronized void saveProps() {
    strategy.saveProps();
  }

  @Override
  public void preferenceChange(PreferenceChangeEvent event) {
  }

  public IProject getProject() {
    return project;
  }
}
