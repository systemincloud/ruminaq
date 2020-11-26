/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse;

import java.util.Collections;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.eclipse.swt.widgets.Table;

/**
 * Util class.
 *
 * @author Marek Jagielski
 */
public final class EclipseUtil {
  
  private EclipseUtil() {
    // util class
  }

  /**
   * Remove selected rows from given swt Table.
   *
   * @param table swt Table
   */
  public static void removeSelectedRows(Table table) {
    IntStream.of(table.getSelectionIndices()).boxed()
        .sorted(Collections.reverseOrder()).forEach(table::remove);
  }

  /**
   * Check if non-empty value is present in column table.
   *
   * @param table  table to check
   * @param column column index
   * @param value
   * @return nonempty value in column
   */
  public static boolean hasNonEmptyValueInTable(Table table, int column,
      String value) {
    return !"".equals(value) && Stream.of(table.getItems())
        .map(i -> i.getText(column)).noneMatch(value::equals);
  }

  /**
   * Check if selected elements in table are one block.
   *
   * @param table table to check
   * @return selections in one block
   */
  public static boolean tableSelectionsConsecutive(Table table) {
    int[] s = table.getSelectionIndices();
    return IntStream.range(0, s.length - 1)
        .noneMatch(i -> Math.abs(s[i] - s[i + 1]) > 1);
  }

}
