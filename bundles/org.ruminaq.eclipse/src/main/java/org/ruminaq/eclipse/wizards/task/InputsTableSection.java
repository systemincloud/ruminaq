/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.task;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.ruminaq.eclipse.EclipseUtil;
import org.ruminaq.eclipse.usertask.model.userdefined.In;
import org.ruminaq.eclipse.usertask.model.userdefined.Module;
import org.ruminaq.eclipse.usertask.model.userdefined.UserdefinedFactory;
import org.ruminaq.util.WidgetSelectedSelectionListener;

/**
 * Inputs Table.
 *
 * @author Marek Jagielski
 */
class InputsTableSection extends TableSection {

  private static final int NAME_COLUMN = 0;
  private static final int DATATYPE_COLUMN = 1;
  private static final int ASYNCHRONOUS_COLUMN = 2;
  private static final int GROUP_COLUMN = 3;
  private static final int HOLD_COLUMN = 4;
  private static final int QUEUE_COLUMN = 5;

  private static final int DEFAULT_QUEUE = -1;

  private TableColumn tblclInputsName;
  private TableColumn tblclInputsData;
  private TableColumn tblclInputsAsync;
  private TableColumn tblclInputsGroup;
  private TableColumn tblclInputsHold;
  private TableColumn tblclInputsQueue;
  private DragSource tblInputsDragSrc;
  private DropTarget tblInputsDropTrg;

  protected InputsTableSection(DeleteTableItemListener canDelete) {
    super(canDelete);
  }

  protected void initLayout(Composite parent) {
    table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
    table.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true, 1, TWO_ROWS));

    tblclInputsName = new TableColumn(table, SWT.NONE);
    tblclInputsData = new TableColumn(table, SWT.NONE);
    tblclInputsAsync = new TableColumn(table, SWT.NONE);
    tblclInputsGroup = new TableColumn(table, SWT.NONE);
    tblclInputsHold = new TableColumn(table, SWT.NONE);
    tblclInputsQueue = new TableColumn(table, SWT.NONE);

    tblInputsDragSrc = new DragSource(table, DND.DROP_MOVE);
    tblInputsDropTrg = new DropTarget(table, DND.DROP_MOVE | DND.DROP_DEFAULT);
  }

  protected void initComponents() {
    table.setHeaderVisible(true);
    table.setLinesVisible(true);

    tblclInputsName.setText("Name");
    tblclInputsData.setText("Data type");
    tblclInputsAsync.setText("Async");
    tblclInputsGroup.setText("Grp");
    tblclInputsHold.setText("Hold");
    tblclInputsQueue.setText("Queue");
    tblInputsDragSrc.setTransfer(types);
    tblInputsDropTrg.setTransfer(types);
    Stream.of(table.getColumns()).forEach(TableColumn::pack);
  }

  protected void initActions() {
    table.addSelectionListener(
        (WidgetSelectedSelectionListener) event -> deleteListener.canDelete());
    tblInputsDragSrc.addDragListener(new DragSourceAdapter() {
      @Override
      public void dragStart(DragSourceEvent event) {
        event.doit = EclipseUtil.tableSelectionsConsecutive(table);
      }
    });
    tblInputsDropTrg.addDropListener(new DropTargetInWizard() {
      @Override
      public void drop(DropTargetEvent event) {
        TableItem[] items = table.getItems();
        TableItem[] selectedItems = table.getSelection();
        TableItem ti = (TableItem) event.item;
        int idx = IntStream.range(0, items.length)
            .filter(i -> items[i].equals(ti)).findFirst().orElse(items.length);
        IntStream.range(0, selectedItems.length)
            .forEach(j -> new TableItem(table, SWT.NONE, idx + j)
                .setText(new String[] { selectedItems[j].getText(NAME_COLUMN),
                    selectedItems[j].getText(DATATYPE_COLUMN),
                    selectedItems[j].getText(ASYNCHRONOUS_COLUMN),
                    selectedItems[j].getText(GROUP_COLUMN),
                    selectedItems[j].getText(HOLD_COLUMN),
                    selectedItems[j].getText(QUEUE_COLUMN) }));
        Stream.of(selectedItems).forEach(TableItem::dispose);
        table.redraw();
      }
    });
  }

  public void decorate(Module module) {
    Stream.of(table.getItems()).map((TableItem ti) -> {
      In in = UserdefinedFactory.eINSTANCE.createIn();
      in.setName(ti.getText(NAME_COLUMN));
      in.setDataType(ti.getText(DATATYPE_COLUMN));
      in.setAsynchronous(Boolean.parseBoolean(ti.getText(ASYNCHRONOUS_COLUMN)));
      in.setGroup(Integer.parseInt(ti.getText(GROUP_COLUMN)));
      in.setHold(Boolean.parseBoolean(ti.getText(HOLD_COLUMN)));
      in.setQueue(Optional.of(ti.getText(QUEUE_COLUMN))
          .filter(Predicate.not(AbstractCreateUserDefinedTaskPage.INF::equals))
          .map(Integer::parseInt).orElseGet(() -> DEFAULT_QUEUE));
      return in;
    }).forEach(module.getInputs()::add);
  }
}
