/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.task;

import java.util.Collection;
import java.util.stream.Stream;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.ruminaq.util.EclipseUtil;

/**
 * Outputs Table.
 *
 * @author Marek Jagielski
 */
public class TableSection {

  protected static final int TWO_ROWS = 2;

  protected final Transfer[] types = new Transfer[] {
      RowTransfer.getInstance() };

  protected final DeleteTableItemListener deleteListener;

  protected Table table;

  public TableSection(DeleteTableItemListener deleteListener) {
    this.deleteListener = deleteListener;
  }

  public Table getTable() {
    return table;
  }
  
  public void createItem(Collection<String> values) {
    TableItem item = new TableItem(table, SWT.NONE);
    item.setText(values.stream().toArray(String[]::new));
    Stream.of(table.getColumns()).forEach(TableColumn::pack);
    table.layout();
  }

  public void removeSelectedItems() {
    EclipseUtil.removeSelectedRows(table);
    table.deselectAll();
  }
}
