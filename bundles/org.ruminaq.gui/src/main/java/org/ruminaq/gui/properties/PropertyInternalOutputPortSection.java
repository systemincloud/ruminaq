/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.gui.properties;

import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.model.ruminaq.InternalOutputPort;
import org.ruminaq.model.ruminaq.ModelUtil;

public class PropertyInternalOutputPortSection extends GFPropertySection
    implements ITabbedPropertyConstants {

  private Label lblId;
  private Label lblIdValue;
  private Label lblTypeOfData;
  private Label dataTypeValue;

  /**
   * @wbp.parser.entryPoint
   */
  @Override
  public void createControls(Composite parent,
      TabbedPropertySheetPage tabbedPropertySheetPage) {
    super.createControls(parent, tabbedPropertySheetPage);

    initLayout(parent);
    initActions();
    initComponents();
    addStyles();
  }

  private void initLayout(Composite parent) {
    FormToolkit toolkit = new FormToolkit(parent.getDisplay());
    Composite composite = toolkit.createComposite(parent, SWT.WRAP);
    composite.setLayout(new GridLayout(2, false));

    lblId = toolkit.createLabel(composite, "", SWT.NONE);
    lblIdValue = toolkit.createLabel(composite, "", SWT.NONE);

    lblTypeOfData = toolkit.createLabel(composite, "", SWT.NONE);
    dataTypeValue = toolkit.createLabel(composite, "", SWT.NONE);
  }

  private void initActions() {
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
    PictogramElement pe = getSelectedPictogramElement();
    if (pe != null) {
      Object bo = Graphiti.getLinkService()
          .getBusinessObjectForLinkedPictogramElement(pe);
      if (bo == null)
        return;
      final InternalOutputPort ip = (InternalOutputPort) bo;

      lblIdValue.setText(ip.getId());

      // Data type
      StringBuilder dataType = new StringBuilder();
      for (DataType dt : ip.getDataType())
        dataType.append(ModelUtil.getName(dt.getClass(), false)).append(", ");
      if (dataType.length() > 2)
        dataType.delete(dataType.length() - 2, dataType.length());

      dataTypeValue.setText(dataType.toString());
    }
    lblTypeOfData.getParent().layout();
  }

  @Override
  public void setInput(IWorkbenchPart part, ISelection selection) {
    super.setInput(part, selection);
  }
}
