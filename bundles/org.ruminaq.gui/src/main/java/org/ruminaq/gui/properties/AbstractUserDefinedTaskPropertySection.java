/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.properties;

import java.util.Optional;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.ruminaq.gui.model.diagram.RuminaqShape;

/**
 * 
 * @author Marek Jagielski
 */
public abstract class AbstractUserDefinedTaskPropertySection
    extends GFPropertySection {

  protected CLabel lblImplementation;
  protected Text txtImplementation;
  protected Button btnSelect;
  protected Button btnCreate;

  protected static <T> Optional<T> toModelObject(PictogramElement pe,
      Class<T> type) {
    return Optional.ofNullable(pe).filter(RuminaqShape.class::isInstance)
        .map(RuminaqShape.class::cast).map(RuminaqShape::getModelObject)
        .filter(type::isInstance).map(type::cast);
  }

  protected <T> Optional<T> selectedModelObject(Class<T> type) {
    return toModelObject(getSelectedPictogramElement(), type);
  }

  @Override
  public void createControls(Composite parent,
      TabbedPropertySheetPage tabbedPropertySheetPage) {
    super.createControls(parent, tabbedPropertySheetPage);

    initLayout(parent);
    initComponents();
    initActions();
  }

  protected void initComponents() {
    lblImplementation.setText("Implementation:");
    btnSelect.setText("Select");
    btnCreate.setText("Create");
  }

  protected abstract void initActions();

  protected abstract void initLayout(Composite parent);

}
