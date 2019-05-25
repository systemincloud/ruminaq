/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.ruminaq.eclipse;

import org.eclipse.osgi.util.NLS;

/**
 * Messages.
 *
 * @author Marek Jagielski
 */
public final class Messages extends NLS {

  private static final String BUNDLE_NAME = "org.ruminaq.eclipse.messages"; //$NON-NLS-1$

  public static String createProjectWizardTitle;
  public static String createProjectWizardDescription;
  public static String createProjectWizardWindowTitle;
  public static String createProjectWizardFailed;
  public static String createNatureFailed;
  public static String createPomFileFailed;
  public static String createClasspathFileFailed;
  public static String CreateDiagramWizard_PageTitle;
  public static String CreateTestDiagramWizard_WizardTitle;
  public static String ProjectValidator_ProjectNatureCheckTitle;
  public static String ProjectValidator_ProjectNatureCheckMessage;
  public static String ProjectValidator_ProjectNatureCheckToogle;
  public static String ProjectPreferences_ProjectPreferencesDescription;
  public static String ProjectPreferences_SystemCredentials;
  public static String ProjectPreferences_ShowSecredKey;
  public static String ProjectPreferences_SystemCredentials_AccountId;
  public static String ProjectPreferences_SystemCredentials_SystemId;
  public static String ProjectPreferences_SystemCredentials_SystemKey;
  public static String RuminaqProjectPreferencePage_grpRuminaq_text;
  public static String RuminaqProjectPreferencePage_lblNewLabel_text;
  public static String RuminaqProjectPreferencePage_lblSystemKey_text;
  public static String RuminaqProjectPreferencePage_lblSystemid_text;
  public static String RuminaqProjectPreferencePage_lblAccountId_text;
  public static String RuminaqProjectPreferencePage_lblSystemKey_text_1;
  public static String RuminaqProjectPreferencePage_hideSecretKeyCheckbox;
  public static String RuminaqProjectPreferencePage_btnTest_text;
  public static String RuminaqProjectPreferencePage_lblNewLabel_text_1;
  public static String RuminaqProjectPreferencePage_grpGeneral_text;
  public static String RuminaqProjectPreferencePage_lblVersion_text;
  public static String RuminaqProjectPreferencePage_btnUpdate_text;

  static {
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }

  private Messages() {
  }
}
