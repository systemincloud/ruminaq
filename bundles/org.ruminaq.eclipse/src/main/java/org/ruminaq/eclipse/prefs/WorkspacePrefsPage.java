/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.prefs;

import ch.qos.logback.classic.Level;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.ruminaq.eclipse.Messages;
import org.ruminaq.prefs.WorkspacePrefs;

/**
 * Ruminaw prefereneces page
 *
 * @author Marek Jagielski
 */
public class WorkspacePrefsPage extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage {

  private static final String[][] LOG_LEVELS = new String[][] {
      { Level.ERROR.levelStr, Level.ERROR.levelStr },
      { Level.WARN.levelStr, Level.WARN.levelStr },
      { Level.INFO.levelStr, Level.INFO.levelStr },
      { Level.DEBUG.levelStr, Level.DEBUG.levelStr },
      { Level.TRACE.levelStr, Level.TRACE.levelStr } };

  public WorkspacePrefsPage() {
    super(GRID);
  }

  @Override
  public void init(IWorkbench workbench) {
    setPreferenceStore(WorkspacePrefs.INSTANCE.getPreferenceStore());
    setDescription(Messages.workspacePrefsDescription);
  }

  @Override
  protected void createFieldEditors() {
    addField(new ComboFieldEditor(WorkspacePrefs.MODELER_LOG_LEVEL,
        Messages.workspacePrefsModelerLogLevel, LOG_LEVELS,
        getFieldEditorParent()));
    addField(new ComboFieldEditor(WorkspacePrefs.RUNNER_LOG_LEVEL,
        Messages.workspacePrefsRunnerLogLevel, LOG_LEVELS,
        getFieldEditorParent()));
  }
}
