/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.properties;

import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyInternalInputPortDebugSection extends GFPropertySection
    implements ITabbedPropertyConstants {

  private Composite root;
  private SashForm sshForm;

  private Composite cmpLeft;

  private Button btnBreakPoint;

  private Composite cmpRight;

  private TableViewer tblVwLogs;
  private Table tblLogs;
  private TableViewerColumn tblclVwLogsTimestamp;
  private TableViewerColumn tblclVwLogsMessage;

  /**
   * @wbp.parser.entryPoint
   */
  @Override
  public void createControls(Composite parent,
      TabbedPropertySheetPage tabbedPropertySheetPage) {
    super.createControls(parent, tabbedPropertySheetPage);

    initLayout(parent);
    initActions();
    initComponents();
    addStyles();
  }

  private void initLayout(Composite parent) {
    ((GridData) parent.getLayoutData()).verticalAlignment = SWT.FILL;
    ((GridData) parent.getLayoutData()).grabExcessVerticalSpace = true;
    ((GridData) parent.getLayoutData()).horizontalAlignment = SWT.FILL;
    ((GridData) parent.getLayoutData()).grabExcessHorizontalSpace = true;
    root = new Composite(parent, SWT.NULL);
    root.setLayout(new GridLayout(1, false));

    sshForm = new SashForm(root, SWT.NONE);
    sshForm.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, true, 1, 1));
    ((GridData) sshForm.getLayoutData()).verticalAlignment = SWT.FILL;
    ((GridData) sshForm.getLayoutData()).grabExcessVerticalSpace = true;
    ((GridData) sshForm.getLayoutData()).horizontalAlignment = SWT.FILL;
    ((GridData) sshForm.getLayoutData()).grabExcessHorizontalSpace = true;

    cmpLeft = new Composite(sshForm, SWT.NONE);
    cmpLeft.setLayout(new GridLayout(1, false));

    btnBreakPoint = new Button(cmpLeft, SWT.CHECK);

    cmpRight = new Composite(sshForm, SWT.NONE);

    tblLogs = new Table(cmpRight, SWT.BORDER | SWT.FULL_SELECTION);
    tblLogs.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
    tblVwLogs = new TableViewer(tblLogs);

    tblclVwLogsTimestamp = new TableViewerColumn(tblVwLogs, SWT.NONE);
    tblclVwLogsMessage = new TableViewerColumn(tblVwLogs, SWT.NONE);
  }

  private void initActions() {

  }

  private void initComponents() {
    btnBreakPoint.setText("breakpoint");

    tblLogs.setHeaderVisible(true);
    tblLogs.setLinesVisible(true);
    tblclVwLogsTimestamp.getColumn().setText("Timestamp");
    tblclVwLogsMessage.getColumn().setText("Message");

  }

  private void addStyles() {
    root.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    cmpLeft.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    btnBreakPoint
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    cmpRight
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
  }

  @Override
  public void refresh() {

  }

  @Override
  public void setInput(IWorkbenchPart part, ISelection selection) {
    super.setInput(part, selection);
  }
}
