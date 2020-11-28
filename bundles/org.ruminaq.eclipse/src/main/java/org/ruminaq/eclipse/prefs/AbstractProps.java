/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.prefs;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.osgi.service.prefs.BackingStoreException;

/**
 * 
 * @author Marek Jagielski
 */
public abstract class AbstractProps implements IPreferenceChangeListener {

  private static final String PROPS_DIR = ".settings";

  private IProject project;
  private ISecurePreferences secureNode;
  private IEclipsePreferences propsNode;

  private boolean secure;

  protected AbstractProps(IProject project, String name, boolean secure) {
    this.project = project;
    ProjectScope ps = new ProjectScope(project);
    this.secure = secure;

    IFolder setF = project.getFolder(PROPS_DIR);
    if (!setF.exists())
      try {
        setF.create(true, true, new NullProgressMonitor());
      } catch (CoreException e2) {
      }

    IFile props = setF.getFile(name + ".prefs");
    if (!props.exists())
      try {
        props.create(IOUtils.toInputStream(""), IResource.FORCE,
            new NullProgressMonitor());
      } catch (CoreException e1) {
      }

    if (secure) {
      try {
        secureNode = SecurePreferencesFactory.open(
            new URL("file:///" + props.getRawLocation().toFile().getPath()),
            new HashMap<>());
      } catch (IOException e1) {
      }
    } else {
      propsNode = ps.getNode(name);
      propsNode.addPreferenceChangeListener(this);
      try {
        propsNode.sync();
      } catch (Exception e) {
      }
    }
  }

  public void put(String key, String value) {
    if (secure) {
      try {
        secureNode.put(key, value, true);
      } catch (StorageException e) {
      }
    } else
      propsNode.put(key, value);
    saveProps();
  }

  public String get(String key) {
    return get(key, null);
  }

  public String get(String key, String defaultValue) {
    if (secure) {
      try {
        return secureNode.get(key, defaultValue);
      } catch (StorageException e) {
        e.printStackTrace();
      }
    } else {
      return propsNode.get(key, defaultValue);
    }
    return null;
  }

  public List<String> getStartingBy(String prefix) {
    List<String> ret = new LinkedList<>();
    if (secure) {
      for (String s : secureNode.childrenNames()) {
        if (s.startsWith(prefix)) {
          ret.add(s);
        }
      }
    } else {
      try {
        for (String s : propsNode.childrenNames()) {
          if (s.startsWith(prefix)) {
            ret.add(s);
          }
        }
      } catch (BackingStoreException e) {
      }
    }

    return ret;
  }

  public void remove(String key) {
    if (secure) {
      secureNode.remove(key);
    } else {
      propsNode.remove(key);
    }
  }

  public synchronized void saveProps() {
    if (secure)
      try {
        secureNode.flush();
      } catch (IOException e) {
      }
    else
      try {
        propsNode.flush();
      } catch (BackingStoreException e) {
      }
  }

  @Override
  public void preferenceChange(PreferenceChangeEvent event) {
  }

  public IProject getProject() {
    return project;
  }
}
