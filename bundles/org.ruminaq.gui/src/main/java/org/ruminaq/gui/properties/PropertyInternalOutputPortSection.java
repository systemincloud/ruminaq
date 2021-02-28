/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.properties;

import java.util.Optional;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.ruminaq.gui.model.diagram.InternalPortShape;
import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.model.ruminaq.InternalOutputPort;
import org.ruminaq.model.ruminaq.ModelUtil;

/**
 * PropertySection for InternalOutputPort.
 *
 * @author Marek Jagielski
 */
public class PropertyInternalOutputPortSection extends GFPropertySection
    implements ITabbedPropertyConstants {

  private static final int TWO_COLUMNS = 2;

  private Label lblId;
  private Label lblIdValue;
  private Label lblTypeOfData;
  private Label dataTypeValue;

  private static Optional<InternalPortShape> shapeFrom(PictogramElement pe) {
    return Optional.ofNullable(pe).filter(InternalPortShape.class::isInstance)
        .map(InternalPortShape.class::cast);
  }

  private static Optional<InternalOutputPort> modelFrom(PictogramElement pe) {
    return shapeFrom(pe).map(InternalPortShape::getModelObject)
        .filter(InternalOutputPort.class::isInstance)
        .map(InternalOutputPort.class::cast);
  }

  @Override
  public void createControls(Composite parent,
      TabbedPropertySheetPage tabbedPropertySheetPage) {
    super.createControls(parent, tabbedPropertySheetPage);

    initLayout(parent);
    initComponents();
    addStyles();
  }

  private void initLayout(Composite parent) {
    FormToolkit toolkit = new FormToolkit(parent.getDisplay());
    Composite composite = toolkit.createComposite(parent, SWT.WRAP);
    composite.setLayout(new GridLayout(TWO_COLUMNS, false));

    lblId = toolkit.createLabel(composite, "", SWT.NONE);
    lblIdValue = toolkit.createLabel(composite, "", SWT.NONE);

    lblTypeOfData = toolkit.createLabel(composite, "", SWT.NONE);
    dataTypeValue = toolkit.createLabel(composite, "", SWT.NONE);
  }

  private void initComponents() {
    lblId.setText("Name:");
    lblTypeOfData.setText("Type of data:");
  }

  private void addStyles() {
    dataTypeValue.setFont(JFaceResources.getFontRegistry().getItalic(""));
  }

  @Override
  public void refresh() {
    modelFrom(getSelectedPictogramElement())
        .ifPresent((InternalOutputPort ip) -> {
          lblIdValue.setText(ip.getId());
          StringBuilder dataType = new StringBuilder();
          for (DataType dt : ip.getDataType())
            dataType.append(ModelUtil.getName(dt.getClass(), false))
                .append(", ");
          if (dataType.length() > 2)
            dataType.delete(dataType.length() - 2, dataType.length());
          dataTypeValue.setText(dataType.toString());
          lblTypeOfData.getParent().layout();
        });
  }
}
