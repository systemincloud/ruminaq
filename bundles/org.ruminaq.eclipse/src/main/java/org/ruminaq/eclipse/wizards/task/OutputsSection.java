/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.task;

import java.util.Arrays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.ruminaq.eclipse.usertask.model.userdefined.Module;
import org.ruminaq.util.EclipseUtil;
import org.ruminaq.util.WidgetSelectedSelectionListener;

/**
 * Output Ports.
 *
 * @author Marek Jagielski
 */
class OutputsSection extends AbstractSection
    implements DeleteTableItemListener {

  private OutputsTableSection tableSection;
  private Label lblOutputsAddName;
  private Text txtOutputsAddName;
  private Combo cmbOutputsAddData;
  private Button btnOutputsAdd;

  private Button btnOutputsRemove;

  public OutputsSection(CreateUserDefinedTaskPage page, Composite parent,
      int style) {
    super(page, parent, style);
    tableSection = new OutputsTableSection(this);
  }

  @Override
  protected void initLayout() {
    setLayout(new GridLayout(TWO_COLUMNS, false));
    setLayoutData(
        new GridData(SWT.LEFT, SWT.CENTER, false, false, TWO_COLUMNS, 1));

    tableSection.initLayout(this);

    Group grpOutputsAdd = new Group(this, SWT.NONE);
    grpOutputsAdd.setLayout(new GridLayout(FIVE_COLUMNS, false));

    btnOutputsRemove = new Button(this, SWT.PUSH);

    lblOutputsAddName = new Label(grpOutputsAdd, SWT.NONE);
    lblOutputsAddName
        .setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    txtOutputsAddName = new Text(grpOutputsAdd, SWT.BORDER);
    cmbOutputsAddData = new Combo(grpOutputsAdd, SWT.NONE | SWT.READ_ONLY);
    btnOutputsAdd = new Button(grpOutputsAdd, SWT.PUSH);
  }

  @Override
  protected void initComponents() {
    tableSection.initComponents();

    lblOutputsAddName.setText("Name:");
    this.userDefinedTaskPage.getDataTypes().stream()
        .forEach(cmbOutputsAddData::add);
    cmbOutputsAddData.select(0);
    btnOutputsAdd.setText("Add");
    btnOutputsAdd.setEnabled(false);

    btnOutputsRemove.setText("Remove");
    btnOutputsRemove.setEnabled(false);
  }

  @Override
  protected void initActions() {
    tableSection.initActions();
    txtOutputsAddName.addModifyListener((ModifyEvent event) -> btnOutputsAdd
        .setEnabled(EclipseUtil.hasNonEmptyValueInTable(tableSection.getTable(),
            0, txtOutputsAddName.getText())));
    btnOutputsAdd.addSelectionListener(
        (WidgetSelectedSelectionListener) (SelectionEvent event) -> {
          tableSection.createItem(Arrays.asList(txtOutputsAddName.getText(),
              cmbOutputsAddData.getText()));
          txtOutputsAddName.setText("");
        });
    btnOutputsRemove.addSelectionListener(
        (WidgetSelectedSelectionListener) (SelectionEvent event) -> {
          tableSection.removeSelectedItems();
          btnOutputsRemove.setEnabled(false);
        });
  }

  public void canDelete() {
    btnOutputsRemove.setEnabled(true);
  }

  @Override
  public void decorate(Module module) {
    tableSection.decorate(module);
  }
}
