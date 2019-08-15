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

  public static String createProjectWizardTitle;
  public static String createProjectWizardDescription;
  public static String createProjectWizardWindowTitle;
  public static String createProjectWizardFailed;
  public static String createProjectWizardFailedPropertiesFile;
  public static String createNatureFailed;
  public static String createPomFileFailed;
  public static String ruminaqFailed;
  public static String createClasspathFileFailed;
  public static String createDiagramWizardTitle;
  public static String createDiagramWizardDescription;
  public static String createTestDiagramWizardTitle;
  public static String createDiagramWizardPageTitle;
  public static String projectValidatorProjectNatureCheckTitle;
  public static String projectValidatorProjectNatureCheckMessage;
  public static String projectValidatorProjectNatureCheckToogle;
  public static String projectPreferencesProjectPreferencesDescription;
  public static String projectPreferencesSystemCredentials;
  public static String projectPreferencesShowSecredKey;
  public static String projectPreferencesSystemCredentialsAccountId;
  public static String projectPreferencesSystemCredentialsSystemId;
  public static String projectPreferencesSystemCredentialsSystemKey;
  public static String ruminaqProjectPreferencePageGrpRuminaqText;
  public static String ruminaqProjectPreferencePageLblNewLabelText;
  public static String ruminaqProjectPreferencePageLblSystemKeyText;
  public static String ruminaqProjectPreferencePageLblSystemidText;
  public static String ruminaqProjectPreferencePageLblAccountIdText;
  public static String ruminaqProjectPreferencePageLblSystemKeyText1;
  public static String ruminaqProjectPreferencePageHideSecretKeyCheckbox;
  public static String ruminaqProjectPreferencePageBtnTestText;
  public static String ruminaqProjectPreferencePageLblNewLabelText1;
  public static String ruminaqProjectPreferencePageGrpGeneralText;
  public static String ruminaqProjectPreferencePageLblVersionText;
  public static String ruminaqProjectPreferencePageBtnUpdateText;

  static {
    NLS.initializeMessages(
        PlatformUtil.getBundleSymbolicName(Messages.class) + ".messages",
        Messages.class);
  }

  private Messages() {
  }
}
