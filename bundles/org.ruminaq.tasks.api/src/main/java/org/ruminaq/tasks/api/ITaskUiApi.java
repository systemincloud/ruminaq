/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.api;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Version;
import org.ruminaq.model.ruminaq.Task;

public interface ITaskUiApi {

  String getSymbolicName();

  Version getVersion();

  default boolean checkPropertyFilter(Task task) {
    return true;
  }

  IPropertySection createPropertySection(Composite parent, PictogramElement pe,
      TransactionalEditingDomain ed, IDiagramTypeProvider dtp);

  default IView createView(Class<? extends ViewPart> viewClass) {
    return null;
  }

}
