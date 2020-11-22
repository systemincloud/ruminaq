/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.task;

import java.util.stream.Stream;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.ruminaq.eclipse.usertask.model.userdefined.Module;
import org.ruminaq.eclipse.usertask.model.userdefined.Out;
import org.ruminaq.eclipse.usertask.model.userdefined.UserdefinedFactory;
import org.ruminaq.util.EclipseUtil;
import org.ruminaq.util.WidgetSelectedSelectionListener;

/**
 * Output Ports.
 *
 * @author Marek Jagielski
 */
class OutputsSection extends AbstractSection {

  private Table tblOutputs;
  private TableColumn tblclOutputsName;
  private TableColumn tblclOutputsData;
  private DragSource tblOutputsDragSrc;
  private DropTarget tblOutputsDropTrg;

  private Label lblOutputsAddName;
  private Text txtOutputsAddName;
  private Combo cmbOutputsAddData;
  private Button btnOutputsAdd;

  private Button btnOutputsRemove;

  private CreateUserDefinedTaskPage userDefinedTaskPage;

  private Transfer[] types = new Transfer[] { RowTransfer.getInstance() };

  public OutputsSection(CreateUserDefinedTaskPage page, Composite parent,
      int style) {
    super(page, parent, style);
  }

  @Override
  protected void initLayout() {
    setLayout(new GridLayout(TWO_COLUMNS, false));
    setLayoutData(
        new GridData(SWT.LEFT, SWT.CENTER, false, false, TWO_COLUMNS, 1));

    tblOutputs = new Table(this, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
    tblOutputs
        .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));

    tblclOutputsName = new TableColumn(tblOutputs, SWT.NONE);
    tblclOutputsData = new TableColumn(tblOutputs, SWT.NONE);

    tblOutputsDragSrc = new DragSource(tblOutputs, DND.DROP_MOVE);
    tblOutputsDropTrg = new DropTarget(tblOutputs,
        DND.DROP_MOVE | DND.DROP_DEFAULT);

    Group grpOutputsAdd = new Group(this, SWT.NONE);
    grpOutputsAdd.setLayout(new GridLayout(5, false));

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
    tblOutputs.setHeaderVisible(true);
    tblOutputs.setLinesVisible(true);

    tblclOutputsName.setText("Name");
    tblclOutputsData.setText("Data type");
    tblOutputsDragSrc.setTransfer(types);
    tblOutputsDropTrg.setTransfer(types);
    Stream.of(tblOutputs.getColumns()).forEach(TableColumn::pack);

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
    tblOutputs.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        btnOutputsRemove.setEnabled(true);
      }
    });
    tblOutputsDragSrc.addDragListener(new DragSourceAdapter() {
      @Override
      public void dragStart(DragSourceEvent event) {
        int[] is = tblOutputs.getSelectionIndices();
        for (int i = 0; i < is.length; i++)
          if (i > 0 && (is[i] - is[i - 1]) != 1) {
            event.doit = false;
            return;
          }
        event.doit = true;
      }
    });
    tblOutputsDropTrg.addDropListener(new DropTargetAdapter() {
      @Override
      public void dragOver(DropTargetEvent event) {
        event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
      }

      @Override
      public void drop(DropTargetEvent event) {
        DropTarget target = (DropTarget) event.widget;
        Table table = (Table) target.getControl();
        if (table != tblOutputs)
          return;

        TableItem[] items = tblOutputs.getSelection();

        TableItem ti = (TableItem) event.item;
        int idx = -1;
        int i = 0;
        for (TableItem it : tblOutputs.getItems()) {
          if (it == ti) {
            idx = i;
            break;
          }
          i++;
        }
        if (i == -1 || i >= tblOutputs.getItems().length)
          return;

        i = idx + 1;
        for (TableItem it : items) {
          new TableItem(tblOutputs, SWT.NONE, i).setText(
              new String[] { it.getText(0), it.getText(1), it.getText(2) });
          i++;
        }
        for (TableItem it : items)
          it.dispose();
        tblOutputs.redraw();
      }
    });
    txtOutputsAddName.addModifyListener((ModifyEvent event) -> btnOutputsAdd
        .setEnabled(EclipseUtil.hasNonEmptyValueInTable(tblOutputs, 0,
            txtOutputsAddName.getText())));
    btnOutputsAdd.addSelectionListener(
        (WidgetSelectedSelectionListener) (SelectionEvent event) -> {
          TableItem item = new TableItem(tblOutputs, SWT.NONE);
          item.setText(new String[] { txtOutputsAddName.getText(),
              cmbOutputsAddData.getText() });
          Stream.of(tblOutputs.getColumns()).forEach(TableColumn::pack);
          tblOutputs.layout();
          txtOutputsAddName.setText("");
        });
    btnOutputsRemove.addSelectionListener(
        (WidgetSelectedSelectionListener) (SelectionEvent event) -> {
          EclipseUtil.removeSelectedRows(tblOutputs);
          tblOutputs.deselectAll();
          btnOutputsRemove.setEnabled(false);
        });
  }

  @Override
  public void decorate(Module module) {
    Stream.of(tblOutputs.getItems()).map((TableItem ti) -> {
      Out out = UserdefinedFactory.eINSTANCE.createOut();
      out.setName(ti.getText(0));
      out.setDataType(ti.getText(1));
      return out;
    }).forEach(module.getOutputs()::add);
  }
}
