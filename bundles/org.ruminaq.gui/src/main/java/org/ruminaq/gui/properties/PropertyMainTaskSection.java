/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.properties;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.ruminaq.eclipse.RuminaqDiagramUtil;
import org.ruminaq.model.ruminaq.MainTask;
import org.ruminaq.model.util.ModelUtil;

public class PropertyMainTaskSection extends GFPropertySection
    implements ITabbedPropertyConstants {

  private Composite composite;

  private CLabel lblVersion;
  private CLabel versionValue;
  private Button btnAtomic;
  private Button btnPreventLost;
  private Button btnOnlyLocal;

  /**
   * @wbp.parser.entryPoint
   */
  @Override
  public void createControls(Composite parent,
      TabbedPropertySheetPage tabbedPropertySheetPage) {
    super.createControls(parent, tabbedPropertySheetPage);

    initLayout(parent);
    initComponents();
    addStyles();
  }

  private void initLayout(Composite parent) {
    ((GridData) parent.getLayoutData()).verticalAlignment = SWT.FILL;
    ((GridData) parent.getLayoutData()).grabExcessVerticalSpace = true;

    composite = new Composite(parent, SWT.NULL);
    composite.setLayout(new GridLayout(2, false));

    lblVersion = new CLabel(composite, SWT.NONE);
    versionValue = new CLabel(composite, SWT.NONE);

    btnAtomic = new Button(composite, SWT.CHECK);
    btnPreventLost = new Button(composite, SWT.CHECK);
    new CLabel(composite, SWT.NONE)
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    btnOnlyLocal = new Button(composite, SWT.CHECK);
  }

  private void initActions(final MainTask mt) {
    btnAtomic.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent se) {
        TransactionalEditingDomain editingDomain = getDiagramContainer()
            .getDiagramBehavior().getEditingDomain();
        ModelUtil.runModelChange(new Runnable() {
          @Override
          public void run() {
            mt.setAtomic(btnAtomic.getSelection());
          }
        }, editingDomain, "Model Update");
      }
    });
    btnPreventLost.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent se) {
        TransactionalEditingDomain editingDomain = getDiagramContainer()
            .getDiagramBehavior().getEditingDomain();
        ModelUtil.runModelChange(new Runnable() {
          @Override
          public void run() {
            mt.setPreventLosts(btnPreventLost.getSelection());
          }
        }, editingDomain, "Model Update");
      }
    });
    btnOnlyLocal.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent se) {
        TransactionalEditingDomain editingDomain = getDiagramContainer()
            .getDiagramBehavior().getEditingDomain();
        ModelUtil.runModelChange(new Runnable() {
          @Override
          public void run() {
            mt.setOnlyLocal(btnOnlyLocal.getSelection());
          }
        }, editingDomain, "Model Update");
      }
    });
  }

  private void initComponents() {
    lblVersion.setText("Version:");
    btnAtomic.setText("Atomic");
    btnPreventLost.setText("Prevent data lost");
    btnOnlyLocal.setText("Only local");
  }

  private void addStyles() {
    composite
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    lblVersion
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    versionValue
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
  }

  @Override
  public void refresh() {
    PictogramElement pe = getSelectedPictogramElement();
    if (pe != null) {
      Object bo = Graphiti.getLinkService()
          .getBusinessObjectForLinkedPictogramElement(pe);
      if (bo == null)
        return;
      MainTask mt = (MainTask) bo;
      initActions(mt);
      versionValue.setText(mt.getVersion());
      btnAtomic.setSelection(mt.isAtomic());
      btnPreventLost.setSelection(mt.isPreventLosts());
      btnOnlyLocal.setSelection(mt.isOnlyLocal());
      btnOnlyLocal.setVisible(!RuminaqDiagramUtil.isTest(mt.eResource().getURI()));
    }
  }

  @Override
  public void setInput(IWorkbenchPart part, ISelection selection) {
    super.setInput(part, selection);
  }
}
