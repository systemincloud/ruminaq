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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.ruminaq.eclipse.usertask.model.userdefined.In;
import org.ruminaq.eclipse.usertask.model.userdefined.Module;
import org.ruminaq.eclipse.usertask.model.userdefined.UserdefinedFactory;
import org.ruminaq.util.WidgetSelectedSelectionListener;

/**
 * Inputs Table.
 *
 * @author Marek Jagielski
 */
class InputsTableSection {

  protected static final int TWO_ROWS = 2;

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

  private final Transfer[] types = new Transfer[] { RowTransfer.getInstance() };
  private final InputsSection inputsSection;

  protected InputsTableSection(InputsSection inputsSection, Composite parent,
      int style) {
    this.inputsSection = inputsSection;
  }

  public Table getTable() {
    return tblInputs;
  }

  protected void initLayout() {
    tblInputs = new Table(inputsSection,
        SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
    tblInputs.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true, 1, TWO_ROWS));

    tblclInputsName = new TableColumn(tblInputs, SWT.NONE);
    tblclInputsData = new TableColumn(tblInputs, SWT.NONE);
    tblclInputsAsync = new TableColumn(tblInputs, SWT.NONE);
    tblclInputsGroup = new TableColumn(tblInputs, SWT.NONE);
    tblclInputsHold = new TableColumn(tblInputs, SWT.NONE);
    tblclInputsQueue = new TableColumn(tblInputs, SWT.NONE);

    tblInputsDragSrc = new DragSource(tblInputs, DND.DROP_MOVE);
    tblInputsDropTrg = new DropTarget(tblInputs,
        DND.DROP_MOVE | DND.DROP_DEFAULT);
  }

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
  }

  protected void initActions() {
    tblInputs.addSelectionListener(
        (WidgetSelectedSelectionListener) event -> inputsSection.canDelete());
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
        IntStream.range(0, items.length)
            .forEach(j -> new TableItem(tblInputs, SWT.NONE, idx2 + 1 + j)
                .setText(new String[] { items[j].getText(NAME_COLUMN),
                    items[j].getText(DATATYPE_COLUMN),
                    items[j].getText(ASYNCHRONOUS_COLUMN),
                    items[j].getText(GROUP_COLUMN),
                    items[j].getText(HOLD_COLUMN),
                    items[j].getText(QUEUE_COLUMN) }));
        Stream.of(items).forEach(TableItem::dispose);
        tblInputs.redraw();
      }
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
