/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse;

import org.eclipse.osgi.util.NLS;
import org.ruminaq.util.PlatformUtil;

/**
 * Messages.
 *
 * @author Marek Jagielski
 */
public final class Messages extends NLS {

  public static String createProjectWizardWindowTitle;
  public static String createProjectWizardTitle;
  public static String createProjectWizardDescription;
  public static String createProjectWizardFailed;
  public static String createProjectWizardFailedConfigureBuilders;
  public static String createProjectWizardFailedPropertiesFile;
  public static String createProjectWizardFailedNature;
  public static String createProjectWizardFailedPom;
  public static String createProjectWizardFailedClasspathFile;
  public static String createProjectWizardFailedSourceFolders;
  public static String ruminaqFailed;
  public static String createDiagramWizardTitle;
  public static String createDiagramWizardDescription;
  public static String createTestDiagramWizardTitle;
  public static String createTestDiagramWizardDescription;
  public static String createDiagramWizardFailed;
  public static String openDiagramFailed;
  public static String createDiagramWizardProject;
  public static String createDiagramWizardProjectBrowse;
  public static String createDiagramWizardContainer;
  public static String createDiagramWizardContainerBrowse;
  public static String createDiagramWizardFilename;
  public static String createDiagramWizardProjectChoose;
  public static String createDiagramWizardContainerChoose;
  public static String createDiagramWizardStatusProjectNotSpecified;
  public static String createDiagramWizardStatusContainerNotSpecified;
  public static String createDiagramWizardStatusContainerNotExists;
  public static String createDiagramWizardStatusContainerNotWritale;
  public static String createDiagramWizardStatusFileNotSpecified;
  public static String createDiagramWizardStatusFileNotValid;
  public static String createDiagramWizardStatusFileExtensionNotValid;
  public static String createDiagramWizardStatusFileExists;
  public static String createUserDefinedTaskPageDescription;
  public static String workspacePrefsDescription;
  public static String workspacePrefsModelerLogLevel;
  public static String workspacePrefsRunnerLogLevel;
  public static String projectPropsGeneralGroup;
  public static String projectPropsVersionLabel;
  public static String projectPropsUpgradeButton;
  public static String projectPropsUpgradeButtonEnabled;
  public static String ruminaqEditorLoadMarkersFailed;
  public static String modelChangeInitialization;

  static {
    NLS.initializeMessages(
        PlatformUtil.getBundleSymbolicName(Messages.class) + ".messages",
        Messages.class);
  }

  private Messages() {
  }
}
