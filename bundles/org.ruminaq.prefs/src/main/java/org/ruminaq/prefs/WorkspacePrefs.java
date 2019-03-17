package org.ruminaq.prefs;

import ch.qos.logback.classic.Level;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.ruminaq.consts.Constants;

public enum WorkspacePrefs {
	INSTANCE;

	public static final String MODELER_LOG_LEVEL = "modeler.log.level";
	public static final String RUNNER_LOG_LEVEL = "runner.log.level";

	private IPreferenceStore preferenceStore = new ScopedPreferenceStore(ConfigurationScope.INSTANCE,
			Constants.SicPlugin.PREFS_ID.s());

	{
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
