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
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.ruminaq.eclipse.usertask.model.userdefined.Module;
import org.ruminaq.eclipse.usertask.model.userdefined.Out;
import org.ruminaq.eclipse.usertask.model.userdefined.UserdefinedFactory;
import org.ruminaq.util.EclipseUtil;
import org.ruminaq.util.WidgetSelectedSelectionListener;

/**
 * Outputs Table.
 *
 * @author Marek Jagielski
 */
public class OutputsTableSection extends TableSection {

  private static final int NAME_COLUMN = 0;
  private static final int DATATYPE_COLUMN = 1;

  private TableColumn tblclOutputsName;
  private TableColumn tblclOutputsData;
  private DragSource tblOutputsDragSrc;
  private DropTarget tblOutputsDropTrg;

  public OutputsTableSection(DeleteTableItemListener canDelete) {
    super(canDelete);
  }

  protected void initLayout(Composite parent) {
    table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
    table.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true, 1, TWO_ROWS));

    tblclOutputsName = new TableColumn(table, SWT.NONE);
    tblclOutputsData = new TableColumn(table, SWT.NONE);

    tblOutputsDragSrc = new DragSource(table, DND.DROP_MOVE);
    tblOutputsDropTrg = new DropTarget(table, DND.DROP_MOVE | DND.DROP_DEFAULT);
  }

  public void initComponents() {
    table.setHeaderVisible(true);
    table.setLinesVisible(true);

    tblclOutputsName.setText("Name");
    tblclOutputsData.setText("Data type");
    tblOutputsDragSrc.setTransfer(types);
    tblOutputsDropTrg.setTransfer(types);
    Stream.of(table.getColumns()).forEach(TableColumn::pack);
  }

  public void initActions() {
    table.addSelectionListener((WidgetSelectedSelectionListener) (
        SelectionEvent event) -> deleteListener.canDelete());
    tblOutputsDragSrc.addDragListener(new DragSourceAdapter() {
      @Override
      public void dragStart(DragSourceEvent event) {
        event.doit = EclipseUtil.tableSelectionsConsecutive(table);
      }
    });
    tblOutputsDropTrg.addDropListener(new DropTargetInWizard() {
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
                    selectedItems[j].getText(DATATYPE_COLUMN) }));
        Stream.of(selectedItems).forEach(TableItem::dispose);
        table.redraw();
      }
    });
  }

  public void decorate(Module module) {
    Stream.of(table.getItems()).map((TableItem ti) -> {
      Out out = UserdefinedFactory.eINSTANCE.createOut();
      out.setName(ti.getText(NAME_COLUMN));
      out.setDataType(ti.getText(DATATYPE_COLUMN));
      return out;
    }).forEach(module.getOutputs()::add);
  }
}
