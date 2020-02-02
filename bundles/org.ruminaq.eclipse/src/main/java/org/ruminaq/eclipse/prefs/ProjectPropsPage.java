/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.prefs;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;
import org.osgi.framework.Version;
import org.ruminaq.eclipse.Messages;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.prefs.ProjectProps;
import org.ruminaq.prefs.Props;
import org.ruminaq.upgrade.Upgrade;
import org.ruminaq.util.PlatformUtil;
import org.slf4j.Logger;

/**
 * Ruminaq project properties page.
 *
 * @author Marek Jagielski
 */
public class ProjectPropsPage extends PropertyPage {

  private static final Logger LOGGER = ModelerLoggerFactory
      .getLogger(ProjectPropsPage.class);

  private static final int COLUMNS_IN_VIEW = 4;

  private Group grpGeneral;
  private Label lblVersionLabel;
  private Label lblVersion;
  private Button btnUpgrade;

  private Props projectProps;

  public ProjectPropsPage() {
    // default constructor
  }

  @Override
  protected Control createContents(Composite parent) {
    projectProps = ProjectProps
        .getInstance(getElement().getAdapter(IProject.class));
    Composite rootComposite = new Composite(parent, SWT.NONE);
    initLayout(rootComposite);
    initComponenets();
    initActions();
    return rootComposite;
  }

  /**
   * Layout of page.
   *
   * @param parent outer composite
   */
  private void initLayout(Composite parent) {
    parent.setLayout(new GridLayout(1, false));

    grpGeneral = new Group(parent, SWT.NONE);
    grpGeneral
        .setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    grpGeneral.setLayout(new GridLayout(COLUMNS_IN_VIEW, false));

    lblVersionLabel = new Label(grpGeneral, SWT.NONE);
    lblVersion = new Label(grpGeneral, SWT.NONE);
    btnUpgrade = new Button(grpGeneral, SWT.NONE);
  }

  /**
   * Set default values.
   */
  private void initComponenets() {
    grpGeneral.setText(Messages.projectPropsGeneralGroup);
    lblVersionLabel.setText(Messages.projectPropsVersionLabel);
    btnUpgrade.setText(Messages.projectPropsUpgradeButton);

    String version = projectProps.get(ProjectProps.RUMINAQ_VERSION);
    Version bundleVersion = PlatformUtil.getBundleVersion(this.getClass());
    Version bundleVersionWithoutQualifier = new Version(bundleVersion.getMajor(),
        bundleVersion.getMinor(), bundleVersion.getMicro());

    if (version == null) {
      projectProps.put(ProjectProps.RUMINAQ_VERSION,
          bundleVersionWithoutQualifier.toString());
      version = bundleVersionWithoutQualifier.toString();
    }
    lblVersion.setText(version);

    if (version.equals(bundleVersionWithoutQualifier.toString())) {
      btnUpgrade.setEnabled(false);
    } else {
      btnUpgrade.setText(Messages.projectPropsUpgradeButtonEnabled + " "
          + PlatformUtil.getBundleVersion(this.getClass()));
    }

    performDefaults();
  }

  /**
   * Set listeners.
   */
  private void initActions() {
    btnUpgrade.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        LOGGER.trace("Upgrade button pushed");
        boolean status = new Upgrade(
            projectProps.get(ProjectProps.RUMINAQ_VERSION),
            PlatformUtil.getBundleVersion(this.getClass()).toString(),
            getElement().getAdapter(IProject.class)).execute();
        if (status) {
          btnUpgrade.setText(Messages.projectPropsUpgradeButton);
          btnUpgrade.setEnabled(false);
          lblVersion.setText(
              PlatformUtil.getBundleVersion(this.getClass()).toString());
        }
      }
    });
  }

  @Override
  public boolean performOk() {
    projectProps.saveProps();
    return super.performOk();
  }
}
