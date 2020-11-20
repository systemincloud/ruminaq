/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.task;

import java.util.Collections;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.ruminaq.eclipse.usertask.model.userdefined.CustomParameter;
import org.ruminaq.eclipse.usertask.model.userdefined.Module;
import org.ruminaq.eclipse.usertask.model.userdefined.UserdefinedFactory;
import org.ruminaq.util.WidgetSelectedSelectionListener;

/**
 * Parameters of task.
 *
 * @author Marek Jagielski
 */
class ParametersSection extends Group {

  private static final int TWO_COLUMNS = 2;
  private static final int FIVE_COLUMNS = 5;

  private Table tblParameters;
  private TableColumn tblclParametersName;
  private TableColumn tblclParametersValue;

  private Label lblParametersAddName;
  private Text txtParametersAddName;
  private Label lblParametersAddValue;
  private Text txtParametersAddValue;
  private Button btnParametersAdd;

  private Button btnParametersRemove;

  public ParametersSection(Composite parent, int style) {
    super(parent, style);
    initLayout();
    initComponents();
    initActions();
  }

  @Override
  protected void checkSubclass() {
    // allow subclass
  }

  private void initLayout() {
    setLayout(new GridLayout(TWO_COLUMNS, false));
    setLayoutData(
        new GridData(SWT.LEFT, SWT.CENTER, false, false, TWO_COLUMNS, 1));

    tblParameters = new Table(this,
        SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
    tblParameters.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true, 1, TWO_COLUMNS));

    tblclParametersName = new TableColumn(tblParameters, SWT.NONE);
    tblclParametersValue = new TableColumn(tblParameters, SWT.NONE);

    Group grpParametersAdd = new Group(this, SWT.NONE);
    grpParametersAdd.setLayout(new GridLayout(FIVE_COLUMNS, false));

    btnParametersRemove = new Button(this, SWT.PUSH);

    lblParametersAddName = new Label(grpParametersAdd, SWT.NONE);
    lblParametersAddName
        .setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    txtParametersAddName = new Text(grpParametersAdd, SWT.BORDER);
    lblParametersAddValue = new Label(grpParametersAdd, SWT.NONE);
    lblParametersAddValue
        .setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    txtParametersAddValue = new Text(grpParametersAdd, SWT.BORDER);
    btnParametersAdd = new Button(grpParametersAdd, SWT.PUSH);
  }

  private void initComponents() {
    tblParameters.setHeaderVisible(true);
    tblParameters.setLinesVisible(true);

    tblclParametersName.setText("Name");
    tblclParametersValue.setText("DefaultValue");
    Stream.of(tblParameters.getColumns()).forEach(TableColumn::pack);

    lblParametersAddName.setText("Name:");
    lblParametersAddValue.setText("Default value:");
    btnParametersAdd.setText("Add");
    btnParametersAdd.setEnabled(false);

    btnParametersRemove.setText("Remove");
    btnParametersRemove.setEnabled(false);
  }

  private void initActions() {
    tblParameters.addSelectionListener(
        (WidgetSelectedSelectionListener) event -> btnParametersRemove
            .setEnabled(true));
    txtParametersAddName
        .addModifyListener((ModifyEvent event) -> btnParametersAdd
            .setEnabled(!"".equals(txtParametersAddName.getText())
                && Stream.of(tblParameters.getItems()).map(i -> i.getText(0))
                    .noneMatch(txtParametersAddName.getText()::equals)));
    btnParametersAdd.addSelectionListener(
        (WidgetSelectedSelectionListener) (SelectionEvent event) -> {
          TableItem item = new TableItem(tblParameters, SWT.NONE);
          item.setText(new String[] { txtParametersAddName.getText(),
              txtParametersAddValue.getText() });
          Stream.of(tblParameters.getColumns()).forEach(TableColumn::pack);
          tblParameters.layout();
          txtParametersAddName.setText("");
          txtParametersAddValue.setText("");
        });
    btnParametersRemove.addSelectionListener(
        (WidgetSelectedSelectionListener) (SelectionEvent event) -> {
          IntStream.of(tblParameters.getSelectionIndices()).boxed()
              .sorted(Collections.reverseOrder())
              .forEach(tblParameters::remove);
          tblParameters.deselectAll();
          btnParametersRemove.setEnabled(false);
        });
  }

  void decorate(Module module) {
    Stream.of(tblParameters.getItems()).map((TableItem ti) -> {
      CustomParameter parameter = UserdefinedFactory.eINSTANCE
          .createCustomParameter();
      parameter.setName(ti.getText(0));
      parameter.setDefaultValue(ti.getText(1));
      return parameter;
    }).forEach(module.getParameters()::add);
  }
}
