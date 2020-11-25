/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.cmd;

import java.util.Optional;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class UpdateDiagramCmd extends AbstractHandler {

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    Optional.of(event).map(HandlerUtil::getCurrentSelection)
        .filter(IStructuredSelection.class::isInstance)
        .map(IStructuredSelection.class::cast)
        .map(IStructuredSelection::getFirstElement)
        .filter(IResource.class::isInstance).map(IResource.class::cast)
        .ifPresent((IResource file) -> {
          try {
            new UpdateDiagram().updateDiagram(file);
          } catch (Exception e) {
            e.printStackTrace();
          }
        });
    return event;
  }

}
