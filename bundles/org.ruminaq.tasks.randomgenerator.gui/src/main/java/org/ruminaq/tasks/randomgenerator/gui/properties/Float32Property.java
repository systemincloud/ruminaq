/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.randomgenerator.gui.properties;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.widgets.Composite;
import org.ruminaq.tasks.randomgenerator.gui.PropertySpecificComposite;
import org.ruminaq.tasks.randomgenerator.gui.ValueSaveListener;
import org.ruminaq.util.NumericUtil;

public class Float32Property {

  public static PropertySpecificComposite createSpecificComposite(
      ValueSaveListener listener, Composite specificRoot, PictogramElement pe,
      TransactionalEditingDomain ed) {
    return new PropertySpecificNumericComposite(listener, specificRoot, pe,
        ed) {
      @Override
      protected boolean checkIfValue(String value) {
        return NumericUtil.isMultiDimsNumeric(value);
      }
    };
  }
}
