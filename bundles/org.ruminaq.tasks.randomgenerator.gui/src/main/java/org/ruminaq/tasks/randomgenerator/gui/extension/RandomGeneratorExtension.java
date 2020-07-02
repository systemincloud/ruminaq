/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.randomgenerator.gui.extension;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.widgets.Composite;
import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.tasks.randomgenerator.gui.PropertySpecificComposite;
import org.ruminaq.tasks.randomgenerator.gui.ValueSaveListener;

public interface RandomGeneratorExtension {
  List<Class<? extends DataType>> getDataTypes();

  Map<String, PropertySpecificComposite> getSpecific(ValueSaveListener listener,
      Composite specificRoot, PictogramElement pe,
      TransactionalEditingDomain ed);

  Map<String, String> getInitSpecific();
}
