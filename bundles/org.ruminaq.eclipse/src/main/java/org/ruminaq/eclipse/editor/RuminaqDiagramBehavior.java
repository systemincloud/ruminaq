/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.editor;

import org.eclipse.graphiti.ui.editor.DefaultPaletteBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.IDiagramContainerUI;

public class RuminaqDiagramBehavior extends DiagramBehavior {

  public RuminaqDiagramBehavior(IDiagramContainerUI diagramContainer) {
    super(diagramContainer);
  }

  @Override
  protected DefaultPaletteBehavior createPaletteBehaviour() {
    return new RuminaqPaletteBehavior(this);
  }
}
