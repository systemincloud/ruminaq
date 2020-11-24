/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.task;

import java.util.Optional;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;

/**
 * DropTargetAdapter that prohibits drag incompatible item.
 *
 * @author Marek Jagielski
 */
public class DropTargetInWizard extends DropTargetAdapter {

  @Override
  public void dragEnter(DropTargetEvent event) {
    if (Optional.ofNullable(event.item).isEmpty()) {
      event.detail = DND.DROP_NONE;
    }
  }

  @Override
  public void dragOver(DropTargetEvent event) {
    event.feedback = DND.FEEDBACK_INSERT_BEFORE | DND.FEEDBACK_SCROLL;
  }

}
