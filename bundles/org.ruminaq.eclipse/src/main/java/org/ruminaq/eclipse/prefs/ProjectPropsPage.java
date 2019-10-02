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
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.prefs.ProjectProps;
import org.ruminaq.prefs.Props;
import org.ruminaq.upgrade.Upgrade;
import org.ruminaq.util.PlatformUtil;
import org.slf4j.Logger;

/**
 *
 * @author Marek Jagielski
 */
public class ProjectPropsPage extends PropertyPage {

  private static final Logger LOGGER = ModelerLoggerFactory
      .getLogger(ProjectPropsPage.class);

  private Composite rootComposite;

  private Group grpGeneral;
  private Label lblVersionLabel;
  private Label lblVersion;
  private Button btnUpgrade;

  private Props projectProps;

  @Override
  protected Control createContents(Composite parent) {
    projectProps = ProjectProps
        .getInstance(getElement().getAdapter(IProject.class));
    rootComposite = new Composite(parent, SWT.NONE);
    initLayout(rootComposite);
    initComponenets();
    initActions();
    return rootComposite;
  }

  private void initLayout(Composite composite) {
    composite.setLayout(new GridLayout(1, false));

    grpGeneral = new Group(composite, SWT.NONE);
    grpGeneral
        .setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    grpGeneral.setLayout(new GridLayout(4, false));

    lblVersionLabel = new Label(grpGeneral, SWT.NONE);
    lblVersion = new Label(grpGeneral, SWT.NONE);
    btnUpgrade = new Button(grpGeneral, SWT.NONE);
  }

  private void initComponenets() {
    grpGeneral.setText("General");
    lblVersionLabel.setText("Modeler Version:");
    btnUpgrade.setText("Upgrade");

    String version = projectProps.get(ProjectProps.MODELER_VERSION);
    if (version == null) {
      version = PlatformUtil.getBundleVersion(this.getClass()).toString();
      projectProps.put(ProjectProps.MODELER_VERSION, version);
    }
    lblVersion.setText(version);

    if (version
        .equals(PlatformUtil.getBundleVersion(this.getClass()).toString())) {
      btnUpgrade.setEnabled(false);
    } else {
      btnUpgrade.setText("Upgrade to "
          + PlatformUtil.getBundleVersion(this.getClass()).toString());
    }

    performDefaults();
  }

  private void initActions() {
    btnUpgrade.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        LOGGER.trace("Upgrade button pushed");
        boolean status = new Upgrade(
            projectProps.get(ProjectProps.MODELER_VERSION),
            PlatformUtil.getBundleVersion(this.getClass()).toString(),
            getElement().getAdapter(IProject.class)).execute();
        if (status) {
          btnUpgrade.setText("Upgrade");
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
