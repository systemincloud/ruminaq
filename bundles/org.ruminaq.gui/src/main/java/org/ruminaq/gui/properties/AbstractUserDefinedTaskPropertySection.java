/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.properties;

import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * 
 * @author Marek Jagielski
 */
public abstract class AbstractUserDefinedTaskPropertySection extends GFPropertySection {

  protected CLabel lblClassSelect;
  protected Text txtClassName;
  protected Button btnClassSelect;
  protected Button btnClassNew;
  
  @Override
  public void createControls(Composite parent,
      TabbedPropertySheetPage tabbedPropertySheetPage) {
    super.createControls(parent, tabbedPropertySheetPage);

    initLayout(parent);
    initComponents();
    initActions();
  }

  protected abstract void initActions();

  protected abstract void initComponents();

  protected abstract void initLayout(Composite parent);
  
}
