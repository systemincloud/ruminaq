/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.launch.ui.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchGroup;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.ruminaq.launch.RuminaqLaunchConfigurationConstants;
import org.ruminaq.launch.ui.Activator;

public class ExecuteTestAction
    implements ILaunchShortcut, IExecutableExtension {

  private static final String CONF_TYPE = "org.ruminaq.ruminaqLaunchConfigurationType"; //$NON-NLS-1$
  private static final String WITH_DIALOG = "WITH_DIALOG"; //$NON-NLS-1$
  private boolean showDialog = false;

  @Override
  public void setInitializationData(IConfigurationElement config,
      String propertyName, Object data) throws CoreException {
    this.showDialog = WITH_DIALOG.equals(data);
  }

  @Override
  public void launch(ISelection selection, String mode) {
    if (selection instanceof IStructuredSelection) {
      IStructuredSelection structuredSelection = (IStructuredSelection) selection;
      Object object = structuredSelection.getFirstElement();
      if (object instanceof IFile)
        launch(((IFile) object), mode);
    }
  }

  @Override
  public void launch(IEditorPart editor, String mode) {
    IEditorInput editorInput = editor.getEditorInput();
    if (editorInput instanceof IFileEditorInput)
      launch(((IFileEditorInput) editorInput).getFile(), mode);
  }

  private void launch(IFile file, String mode) {
    ILaunchConfiguration launchConfiguration = createLaunchConfiguration(file);
    ILaunchGroup group = DebugUITools.getLaunchGroup(launchConfiguration, mode);
    if (showDialog)
      DebugUITools.openLaunchConfigurationDialog(Activator.getShell(),
          launchConfiguration, group.getIdentifier(), null);
    else
      DebugUITools.launch(launchConfiguration, mode);
  }

  private ILaunchConfiguration createLaunchConfiguration(IFile file) {
    try {
      ILaunchManager launchManager = DebugPlugin.getDefault()
          .getLaunchManager();
      ILaunchConfigurationType launchConfigurationType = launchManager
          .getLaunchConfigurationType(CONF_TYPE);
      String diagram = file.getProjectRelativePath().removeFirstSegments(3)
          .toString();
      ILaunchConfigurationWorkingCopy workingCopy = launchConfigurationType
          .newInstance(null, diagram.replace("/", ".") + " local");
      workingCopy.setAttribute(
          RuminaqLaunchConfigurationConstants.ATTR_PROJECT_NAME,
          file.getProject().getName());
      workingCopy.setAttribute(
          RuminaqLaunchConfigurationConstants.ATTR_TEST_TASK, diagram);
      workingCopy.setAttribute(
          RuminaqLaunchConfigurationConstants.ATTR_MACHINE_ID, "");
      workingCopy.setAttribute(
          RuminaqLaunchConfigurationConstants.ATTR_ONLY_LOCAL_TASKS, true);
      return workingCopy.doSave();
    } catch (CoreException ex) {
    }
    return null;
  }
}
