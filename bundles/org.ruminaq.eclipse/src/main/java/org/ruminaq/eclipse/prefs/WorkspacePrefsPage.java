/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.prefs;

import java.util.stream.Stream;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.ruminaq.eclipse.Messages;
import org.ruminaq.launch.api.LaunchExtension;
import org.ruminaq.logs.ModelerLoggerFactory;

import ch.qos.logback.classic.Level;

/**
 * Ruminaq prefereneces page.
 *
 * @author Marek Jagielski
 */
public class WorkspacePrefsPage extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage {

  public static final String QUALIFIER = "org.ruminaq.prefs";

  private static final String[][] LOG_LEVELS = Stream
      .of(Level.ERROR, Level.WARN, Level.INFO, Level.DEBUG, Level.TRACE)
      .map(level -> new String[] { level.levelStr, level.levelStr })
      .toArray(String[][]::new);

  public WorkspacePrefsPage() {
    super(GRID);
  }

  @Override
  public void init(IWorkbench workbench) {
    setPreferenceStore(
        new ScopedPreferenceStore(InstanceScope.INSTANCE, QUALIFIER));
    setDescription(Messages.workspacePrefsDescription);
  }

  @Override
  protected void createFieldEditors() {
    addField(new ComboFieldEditor(ModelerLoggerFactory.MODELER_LOG_LEVEL_PREF,
        Messages.workspacePrefsModelerLogLevel, LOG_LEVELS,
        getFieldEditorParent()));
    addField(new ComboFieldEditor(LaunchExtension.RUNNER_LOG_LEVEL_PREF,
        Messages.workspacePrefsRunnerLogLevel, LOG_LEVELS,
        getFieldEditorParent()));
  }
}
