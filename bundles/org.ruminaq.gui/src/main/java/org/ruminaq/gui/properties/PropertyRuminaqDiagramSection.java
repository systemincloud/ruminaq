/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.properties;

import java.util.Optional;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.ruminaq.gui.model.diagram.RuminaqDiagram;
import org.ruminaq.model.ruminaq.MainTask;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.util.WidgetSelectedSelectionListener;

/**
 *
 * @author Marek Jagielski
 */
public class PropertyRuminaqDiagramSection extends GFPropertySection
    implements ITabbedPropertyConstants {

  private static final int TWO_COLUMNS = 2;

  private CLabel lblVersion;
  private CLabel versionValue;
  private Button btnAtomic;
  private Button btnPreventLost;

  @Override
  public void createControls(Composite parent,
      TabbedPropertySheetPage tabbedPropertySheetPage) {
    super.createControls(parent, tabbedPropertySheetPage);

    initLayout(parent);
    initComponents();
  }

  private void initLayout(Composite parent) {
    ((GridData) parent.getLayoutData()).verticalAlignment = SWT.FILL;
    ((GridData) parent.getLayoutData()).grabExcessVerticalSpace = true;

    Composite composite = new Composite(parent, SWT.NULL);
    composite.setLayout(new GridLayout(TWO_COLUMNS, false));

    lblVersion = new CLabel(composite, SWT.NONE);
    versionValue = new CLabel(composite, SWT.NONE);

    btnAtomic = new Button(composite, SWT.CHECK);
    btnPreventLost = new Button(composite, SWT.CHECK);
  }

  private void initActions(MainTask mt) {
    btnAtomic.addSelectionListener(
        (WidgetSelectedSelectionListener) se -> ModelUtil.runModelChange(
            () -> mt.setAtomic(btnAtomic.getSelection()),
            getDiagramContainer().getDiagramBehavior().getEditingDomain(),
            "Model Update"));
    btnPreventLost.addSelectionListener(
        (WidgetSelectedSelectionListener) se -> ModelUtil.runModelChange(
            () -> mt.setPreventLosts(btnPreventLost.getSelection()),
            getDiagramContainer().getDiagramBehavior().getEditingDomain(),
            "Model Update"));
  }

  private void initComponents() {
    lblVersion.setText("Version:");
    btnAtomic.setText("Atomic");
    btnPreventLost.setText("Prevent data lost");
  }

  @Override
  public void refresh() {
    Optional.ofNullable(getSelectedPictogramElement())
        .filter(RuminaqDiagram.class::isInstance)
        .map(RuminaqDiagram.class::cast).map(RuminaqDiagram::getMainTask)
        .ifPresent((MainTask mt) -> {
          initActions(mt);
          versionValue.setText(mt.getVersion());
          btnAtomic.setSelection(mt.isAtomic());
          btnPreventLost.setSelection(mt.isPreventLosts());
        });
  }
}
