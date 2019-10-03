/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.prefs;

import ch.qos.logback.classic.Level;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public enum WorkspacePrefs {
  INSTANCE;

  public static final String QUALIFIER = "org.ruminaq.prefs";

  public static final String MODELER_LOG_LEVEL = "modeler.log.level";
  public static final String RUNNER_LOG_LEVEL = "runner.log.level";

  private IPreferenceStore preferenceStore = new ScopedPreferenceStore(
      ConfigurationScope.INSTANCE, QUALIFIER);

  WorkspacePrefs() {
    preferenceStore.setDefault(MODELER_LOG_LEVEL, Level.ERROR.levelStr);
    preferenceStore.setDefault(RUNNER_LOG_LEVEL, Level.ERROR.levelStr);
  }

  public IPreferenceStore getPreferenceStore() {
    return preferenceStore;
  }

  public void set(String key, String value) {
    this.preferenceStore.setValue(key, value);
  }

  public String get(String key) {
    return preferenceStore.getString(key);
  }
}
