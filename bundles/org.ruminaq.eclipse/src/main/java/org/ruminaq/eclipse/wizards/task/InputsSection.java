/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.task;

import java.util.stream.IntStream;
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
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.ruminaq.eclipse.usertask.model.userdefined.In;
import org.ruminaq.eclipse.usertask.model.userdefined.Module;
import org.ruminaq.eclipse.usertask.model.userdefined.UserdefinedFactory;
import org.ruminaq.util.EclipseUtil;
import org.ruminaq.util.WidgetSelectedSelectionListener;

/**
 * Input Ports.
 *
 * @author Marek Jagielski
 */
class InputsSection extends AbstractSection {

  private static final int NAME_COLUMN = 0;
  private static final int DATATYPE_COLUMN = 1;
  private static final int ASYNCHRONOUS_COLUMN = 2;
  private static final int GROUP_COLUMN = 3;
  private static final int HOLD_COLUMN = 4;
  private static final int QUEUE_COLUMN = 5;

  private Table tblInputs;
  private TableColumn tblclInputsName;
  private TableColumn tblclInputsData;
  private TableColumn tblclInputsAsync;
  private TableColumn tblclInputsGroup;
  private TableColumn tblclInputsHold;
  private TableColumn tblclInputsQueue;
  private DragSource tblInputsDragSrc;
  private DropTarget tblInputsDropTrg;

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

  private Transfer[] types = new Transfer[] { RowTransfer.getInstance() };

  public InputsSection(CreateUserDefinedTaskPage page, Composite parent,
      int style) {
    super(page, parent, style);
  }

  @Override
  protected void initLayout() {
    setLayout(new GridLayout(2, false));
    setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));

    tblInputs = new Table(this, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
    tblInputs.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));

    tblclInputsName = new TableColumn(tblInputs, SWT.NONE);
    tblclInputsData = new TableColumn(tblInputs, SWT.NONE);
    tblclInputsAsync = new TableColumn(tblInputs, SWT.NONE);
    tblclInputsGroup = new TableColumn(tblInputs, SWT.NONE);
    tblclInputsHold = new TableColumn(tblInputs, SWT.NONE);
    tblclInputsQueue = new TableColumn(tblInputs, SWT.NONE);

    tblInputsDragSrc = new DragSource(tblInputs, DND.DROP_MOVE);
    tblInputsDropTrg = new DropTarget(tblInputs,
        DND.DROP_MOVE | DND.DROP_DEFAULT);

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
    tblInputs.setHeaderVisible(true);
    tblInputs.setLinesVisible(true);

    tblclInputsName.setText("Name");
    tblclInputsData.setText("Data type");
    tblclInputsAsync.setText("Async");
    tblclInputsGroup.setText("Grp");
    tblclInputsHold.setText("Hold");
    tblclInputsQueue.setText("Queue");
    tblInputsDragSrc.setTransfer(types);
    tblInputsDropTrg.setTransfer(types);
    Stream.of(tblInputs.getColumns()).forEach(TableColumn::pack);

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
    tblInputs.addSelectionListener(
        (WidgetSelectedSelectionListener) event -> btnInputsRemove
            .setEnabled(true));
    tblInputsDragSrc.addDragListener(new DragSourceAdapter() {
      @Override
      public void dragStart(DragSourceEvent event) {
        int[] is = tblInputs.getSelectionIndices();
        for (int i = 0; i < is.length; i++)
          if (i > 0 && (is[i] - is[i - 1]) != 1) {
            event.doit = false;
            return;
          }
        event.doit = true;
      }
    });
    tblInputsDropTrg.addDropListener(new DropTargetAdapter() {
      @Override
      public void dragOver(DropTargetEvent event) {
        event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
      }

      @Override
      public void drop(DropTargetEvent event) {
        DropTarget target = (DropTarget) event.widget;
        Table table = (Table) target.getControl();
        if (table != tblInputs)
          return;

        TableItem[] items = tblInputs.getSelection();

        TableItem ti = (TableItem) event.item;
        int idx = -1;
        int i = 0;
        for (TableItem it : tblInputs.getItems()) {
          if (it == ti) {
            idx = i;
            break;
          }
          i++;
        }
        if (i == -1 || i >= tblInputs.getItems().length)
          return;

        final int idx2 = idx;
        IntStream.range(0, items.length).forEach(
            j -> new TableItem(tblInputs, SWT.NONE, idx2 + 1 + j).setText(
                new String[] { items[j].getText(0), items[j].getText(1),
                    items[j].getText(2), items[j].getText(3),
                    items[j].getText(4), items[j].getText(5) }));
        Stream.of(items).forEach(TableItem::dispose);
        tblInputs.redraw();
      }
    });
    txtInputsAddName.addModifyListener((ModifyEvent event) -> {
      boolean exist = false;
      for (TableItem it : tblInputs.getItems())
        if (it.getText(0).equals(txtInputsAddName.getText()))
          exist = true;
      if ("".equals(txtInputsAddName.getText()) || exist)
        btnInputsAdd.setEnabled(false);
      else
        btnInputsAdd.setEnabled(true);
    });
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
          TableItem item = new TableItem(tblInputs, SWT.NONE);
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
          for (TableColumn tc : tblInputs.getColumns())
            tc.pack();
          tblInputs.layout();
          txtInputsAddName.setText("");
        });
    btnInputsRemove.addSelectionListener(
        (WidgetSelectedSelectionListener) (SelectionEvent event) -> {
          EclipseUtil.removeSelectedRows(tblInputs);
          tblInputs.deselectAll();
          btnInputsRemove.setEnabled(false);
        });
  }

  public void decorate(Module module) {
    Stream.of(tblInputs.getItems()).map((TableItem ti) -> {
      In in = UserdefinedFactory.eINSTANCE.createIn();
      in.setName(ti.getText(NAME_COLUMN));
      in.setDataType(ti.getText(DATATYPE_COLUMN));
      in.setAsynchronous(Boolean.parseBoolean(ti.getText(ASYNCHRONOUS_COLUMN)));
      in.setGroup(Integer.parseInt(ti.getText(GROUP_COLUMN)));
      in.setHold(Boolean.parseBoolean(ti.getText(HOLD_COLUMN)));
      in.setQueue(
          ti.getText(QUEUE_COLUMN).equals(AbstractCreateUserDefinedTaskPage.INF)
              ? -1
              : Integer.parseInt(ti.getText(QUEUE_COLUMN)));
      return in;
    }).forEach(module.getInputs()::add);
  }
}
