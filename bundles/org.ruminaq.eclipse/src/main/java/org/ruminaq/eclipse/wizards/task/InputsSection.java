/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.task;

import java.util.stream.Stream;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.ruminaq.eclipse.usertask.model.userdefined.Module;
import org.ruminaq.util.EclipseUtil;
import org.ruminaq.util.WidgetSelectedSelectionListener;

/**
 * Input Ports.
 *
 * @author Marek Jagielski
 */
class InputsSection extends AbstractSection {

  private InputsTableSection tableSection;
  private Label lblInputsAddName;
  private Text txtInputsAddName;
  private Combo cmbInputsAddData;
  private Button btnInputsAddAsync;
  private Button btnInputsAddHold;
  private Label lblInputsAddGroup;
  private Spinner spnInputsAddGroup;
  private Label lblInputsAddQueue;
  private Button btnInputsAddQueueInf;
  private Spinner spnInputsAddQueue;
  private Button btnInputsAdd;

  private Button btnInputsRemove;

  public InputsSection(CreateUserDefinedTaskPage page, Composite parent,
      int style) {
    super(page, parent, style);
    tableSection = new InputsTableSection(this, parent, style);
  }

  @Override
  protected void initLayout() {
    setLayout(new GridLayout(TWO_COLUMNS, false));
    setLayoutData(
        new GridData(SWT.LEFT, SWT.CENTER, false, false, TWO_COLUMNS, 1));

    tableSection.initLayout();

    Group grpInputsAdd = new Group(this, SWT.NONE);
    grpInputsAdd.setLayout(new GridLayout(5, false));

    lblInputsAddName = new Label(grpInputsAdd, SWT.NONE);
    lblInputsAddName
        .setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    txtInputsAddName = new Text(grpInputsAdd, SWT.BORDER);
    cmbInputsAddData = new Combo(grpInputsAdd, SWT.NONE | SWT.READ_ONLY);
    Composite cmpInputsAddOptions = new Composite(grpInputsAdd, SWT.NULL);
    cmpInputsAddOptions.setLayout(new GridLayout(1, false));
    btnInputsAddAsync = new Button(cmpInputsAddOptions, SWT.CHECK);
    btnInputsAddHold = new Button(cmpInputsAddOptions, SWT.CHECK);
    Composite cmpInputsAddGroup = new Composite(cmpInputsAddOptions, SWT.NULL);
    cmpInputsAddGroup.setLayout(new GridLayout(2, false));
    lblInputsAddGroup = new Label(cmpInputsAddGroup, SWT.NONE);
    spnInputsAddGroup = new Spinner(cmpInputsAddGroup, SWT.BORDER);
    Composite cmpInputsAddQueue = new Composite(cmpInputsAddOptions, SWT.NULL);
    cmpInputsAddQueue.setLayout(new GridLayout(3, false));
    lblInputsAddQueue = new Label(cmpInputsAddQueue, SWT.NONE);
    btnInputsAddQueueInf = new Button(cmpInputsAddQueue, SWT.CHECK);
    spnInputsAddQueue = new Spinner(cmpInputsAddQueue, SWT.BORDER);
    btnInputsAdd = new Button(grpInputsAdd, SWT.PUSH);

    btnInputsRemove = new Button(this, SWT.PUSH);
  }

  @Override
  protected void initComponents() {
    tableSection.initComponents();

    lblInputsAddName.setText("Name:");
    this.userDefinedTaskPage.getDataTypes().stream()
        .forEach(cmbInputsAddData::add);
    cmbInputsAddData.select(0);
    btnInputsAddAsync.setText("asynchronous");
    btnInputsAddHold.setText("hold last data");
    lblInputsAddGroup.setText("group");
    spnInputsAddGroup.setMinimum(-1);
    spnInputsAddGroup.setMaximum(Integer.MAX_VALUE);
    spnInputsAddGroup.setSelection(-1);
    btnInputsAddQueueInf.setText("inf");
    lblInputsAddQueue.setText("queue size:");
    spnInputsAddQueue.setMinimum(1);
    spnInputsAddQueue.setMaximum(Integer.MAX_VALUE);
    spnInputsAddQueue.setSelection(1);
    btnInputsAdd.setText("Add");
    btnInputsAdd.setEnabled(false);

    btnInputsRemove.setText("Remove");
    btnInputsRemove.setEnabled(false);
  }

  @Override
  protected void initActions() {
    tableSection.initActions();
    txtInputsAddName.addModifyListener((ModifyEvent event) -> btnInputsAdd
        .setEnabled(EclipseUtil.hasNonEmptyValueInTable(tableSection.getTable(),
            0, txtInputsAddName.getText())));
    btnInputsAddAsync.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        btnInputsAddHold.setEnabled(!btnInputsAddAsync.getSelection());
        spnInputsAddGroup.setEnabled(!btnInputsAddAsync.getSelection());
      }
    });
    btnInputsAddQueueInf.addSelectionListener(
        (WidgetSelectedSelectionListener) event -> spnInputsAddQueue
            .setEnabled(!btnInputsAddQueueInf.getSelection()));
    spnInputsAddQueue.addSelectionListener(
        (WidgetSelectedSelectionListener) event -> getParent().layout());
    btnInputsAdd.addSelectionListener(
        (WidgetSelectedSelectionListener) (SelectionEvent event) -> {
          TableItem item = new TableItem(tableSection.getTable(), SWT.NONE);
          boolean async = btnInputsAddAsync.getSelection();
          String grp = Integer.toString(spnInputsAddGroup.getSelection());
          boolean inf = btnInputsAddQueueInf.getSelection();
          item.setText(new String[] { txtInputsAddName.getText(),
              cmbInputsAddData.getText(), Boolean.toString(async),
              async ? "-1" : grp,
              async ? Boolean.toString(false)
                  : Boolean.toString(btnInputsAddHold.getSelection()),
              inf ? AbstractCreateUserDefinedTaskPage.INF
                  : Integer.toString(spnInputsAddQueue.getSelection()) });
          Stream.of(tableSection.getTable().getColumns())
              .forEach(TableColumn::pack);
          tableSection.getTable().layout();
          txtInputsAddName.setText("");
        });
    btnInputsRemove.addSelectionListener(
        (WidgetSelectedSelectionListener) (SelectionEvent event) -> {
          EclipseUtil.removeSelectedRows(tableSection.getTable());
          tableSection.getTable().deselectAll();
          btnInputsRemove.setEnabled(false);
        });
  }

  public void canDelete() {
    btnInputsRemove.setEnabled(true);
  }

  @Override
  public void decorate(Module module) {
    tableSection.decorate(module);
  }
}
