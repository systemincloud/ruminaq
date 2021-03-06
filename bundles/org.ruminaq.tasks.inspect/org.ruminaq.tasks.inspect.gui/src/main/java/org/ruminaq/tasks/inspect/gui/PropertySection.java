/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.inspect.gui;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.tasks.inspect.model.inspect.Inspect;

public class PropertySection {

  private Composite composite;
  private CLabel lblMatricesPretty;
  private Button btnMatricesPretty;

  public PropertySection(Composite parent, PictogramElement pe,
      TransactionalEditingDomain ed, IDiagramTypeProvider dtp) {
    initLayout(parent);
    initComponents();
    initActions(pe, ed, dtp);
    addStyles();
  }

  private void initLayout(Composite parent) {
    ((GridData) parent.getLayoutData()).verticalAlignment = SWT.FILL;
    ((GridData) parent.getLayoutData()).grabExcessVerticalSpace = true;

    composite = new Composite(parent, SWT.NULL);
    composite.setLayout(new GridLayout(2, false));

    lblMatricesPretty = new CLabel(composite, SWT.NONE);
    btnMatricesPretty = new Button(composite, SWT.CHECK);
  }

  private void initComponents() {
    lblMatricesPretty.setText("Pretty Matrices:");
  }

  private void initActions(final PictogramElement pe,
      final TransactionalEditingDomain ed, final IDiagramTypeProvider dtp) {
    btnMatricesPretty.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        ModelUtil.runModelChange(() -> {
          Object bo = Graphiti.getLinkService()
              .getBusinessObjectForLinkedPictogramElement(pe);
          if (bo instanceof Inspect) {
            Inspect inspect = (Inspect) bo;
            inspect.setMatricesPretty(btnMatricesPretty.getSelection());
          }
        }, ed, "Change console type");
      }
    });
  }

  private void addStyles() {
    composite
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    lblMatricesPretty
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
  }

  public void refresh(PictogramElement pe, TransactionalEditingDomain ed) {
    if (pe != null) {
      Object bo = Graphiti.getLinkService()
          .getBusinessObjectForLinkedPictogramElement(pe);
      if (bo instanceof Inspect) {
        Inspect inspect = (Inspect) bo;
        btnMatricesPretty.setSelection(inspect.isMatricesPretty());
      }
    }
  }

}
