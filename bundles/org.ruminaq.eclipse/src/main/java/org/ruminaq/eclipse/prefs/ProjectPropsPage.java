/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;
import org.ruminaq.eclipse.Activator;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.prefs.ProjectProps;
import org.ruminaq.prefs.ProjectPropsSecure;
import org.ruminaq.prefs.Props;
import org.ruminaq.upgrade.Upgrade;
import org.slf4j.Logger;

/**
 *
 * @author Marek Jagielski
 */
public class ProjectPropsPage extends PropertyPage {

    private final Logger logger = ModelerLoggerFactory.getLogger(ProjectPropsPage.class);

    private Composite rootComposite;

    private Group     grpGeneral;
    private Label     lblVersionLabel;
    private Label     lblVersion;
    private Button    btnUpgrade;

    private Group     grpCredentials;
    private Label     lblAccountNumber;
    private Text      txtAccountNumber;
    private Label     lblSystemName;
    private Text      txtSystemName;
    private Label     lblSystemKey;
    private Text      txtSystemKey;
    private Button    btnShowSystemKey;
    private Button    btnTestConnection;
    private Label     lblStatus;

    private char secretEchoChar;

    private Props projectProps;
    private Props secureProps;

    public ProjectPropsPage() { }

    @Override
    protected Control createContents(Composite parent) {
        projectProps = ProjectProps.getInstance(getElement().getAdapter(IProject.class));
        secureProps  = ProjectPropsSecure.getInstance(getElement().getAdapter(IProject.class));
        rootComposite = new Composite(parent, SWT.NONE);
        initLayout(rootComposite);
        initComponenets();
        initActions();
        return rootComposite;
    }

    private void initLayout(Composite composite) {
        composite.setLayout(new GridLayout(1, false));

        grpGeneral = new Group(composite, SWT.NONE);
        grpGeneral.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        grpGeneral.setLayout(new GridLayout(4, false));

        lblVersionLabel = new Label(grpGeneral, SWT.NONE);
        lblVersion = new Label(grpGeneral, SWT.NONE);
        btnUpgrade = new Button(grpGeneral, SWT.NONE);

        grpCredentials = new Group(composite, SWT.NONE);
        grpCredentials.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        grpCredentials.setLayout(new GridLayout(2, false));
        new Label(grpGeneral, SWT.NONE);

        lblAccountNumber = new Label(grpCredentials, SWT.NONE);
        lblAccountNumber.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        txtAccountNumber = new Text(grpCredentials, SWT.BORDER);
        txtAccountNumber.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        lblSystemName = new Label(grpCredentials, SWT.NONE);
        lblSystemName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        txtSystemName = new Text(grpCredentials, SWT.BORDER);
        txtSystemName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        lblSystemKey = new Label(grpCredentials, SWT.NONE);
        lblSystemKey.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        txtSystemKey = new Text(grpCredentials, SWT.BORDER | SWT.PASSWORD);
        txtSystemKey.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        new Label(grpCredentials, SWT.NONE);

        btnShowSystemKey = new Button(grpCredentials, SWT.CHECK);

        btnTestConnection = new Button(grpCredentials, SWT.NONE);
        btnTestConnection.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

        lblStatus = new Label(grpCredentials, SWT.NONE);
    }

    private void initComponenets() {
        grpGeneral       .setText("General");
        lblVersionLabel  .setText("Modeler Version:");
        btnUpgrade       .setText("Upgrade");

        String version = projectProps.get(ProjectProps.MODELER_VERSION);
        if(version == null) {
            version = Activator.getVersionString();
            projectProps.put(ProjectProps.MODELER_VERSION, version);
        }
        lblVersion       .setText(version);

        if(version.equals(Activator.getBaseVersionString())) btnUpgrade.setEnabled(false);
        else btnUpgrade.setText("Upgrade to " + Activator.getBaseVersionString());

        grpCredentials   .setText("Credentials");
        lblAccountNumber .setText("Account Number:");
        lblSystemName    .setText("System Name:");
        lblSystemKey     .setText("Security Key:");

        performDefaults();

        btnShowSystemKey .setText("show Security Key");
        btnTestConnection.setText("Test");

        secretEchoChar = txtSystemKey.getEchoChar();
    }

    private void initActions() {
        btnUpgrade.addSelectionListener(new SelectionAdapter() {
            @Override public void widgetSelected(SelectionEvent e) {
                logger.trace("Upgrade button pushed");
                boolean status = new Upgrade(projectProps.get(ProjectProps.MODELER_VERSION), Activator.getBaseVersionString(), getElement().getAdapter(IProject.class)).execute();
                if(status) {
                    btnUpgrade.setText("Upgrade");
                    btnUpgrade.setEnabled(false);
                    lblVersion.setText(Activator.getBaseVersionString());
                }
            }
        });
        btnShowSystemKey.addSelectionListener(new SelectionAdapter() {
            @Override public void widgetSelected(SelectionEvent e) {
                if(btnShowSystemKey.getSelection()) txtSystemKey.setEchoChar('\0');
                else	                            txtSystemKey.setEchoChar(secretEchoChar);
            }
        });
        btnTestConnection.addSelectionListener(new SelectionAdapter() {
            @Override public void widgetSelected(SelectionEvent event) {
                grpCredentials.layout();
            }
        });
    }

    @Override
    public boolean performOk() {
        boolean saveSecure = false;

        String accountNumber    = secureProps.get(ProjectPropsSecure.ACCOUNT_NUMBER);
        String newAccountNumber = txtAccountNumber.getText().replace("\n", "");
        if((accountNumber == null && !newAccountNumber.equals("")) || (accountNumber != null && !newAccountNumber.equals(accountNumber))) {
            secureProps.put(ProjectPropsSecure.ACCOUNT_NUMBER, newAccountNumber);
            saveSecure = true;
        }

        String systemName    = secureProps.get(ProjectPropsSecure.SYSTEM_NAME);
        String newSystemName = txtSystemName.getText().replace("\n", "");
        if((systemName == null && !newSystemName.equals("")) || (accountNumber != null && !newSystemName.equals(systemName))) {
            secureProps.put(ProjectPropsSecure.SYSTEM_NAME, newSystemName);
            saveSecure = true;
        }

        String systemKey    = secureProps.get(ProjectPropsSecure.SYSTEM_KEY);
        String newSystemKey = txtSystemKey.getText().replace("\n", "");
        if((systemKey == null && !newSystemKey.equals("")) || (accountNumber != null && !newSystemKey.equals(systemKey))) {
            secureProps.put(ProjectPropsSecure.SYSTEM_KEY, newSystemKey);
            saveSecure = true;
        }

        projectProps.saveProps();

        if(saveSecure) secureProps.saveProps();

        return super.performOk();
    }

    @Override
    protected void performDefaults() {
        txtAccountNumber.setText(secureProps.get(ProjectPropsSecure.ACCOUNT_NUMBER, ""));
        txtSystemName   .setText(secureProps.get(ProjectPropsSecure.SYSTEM_NAME, ""));
        txtSystemKey    .setText(secureProps.get(ProjectPropsSecure.SYSTEM_KEY, ""));
        super.performDefaults();
    }
}
