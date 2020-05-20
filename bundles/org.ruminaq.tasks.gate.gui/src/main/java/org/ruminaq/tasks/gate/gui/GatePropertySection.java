package org.ruminaq.tasks.gate.gui;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.impl.InternalTransaction;
import org.eclipse.emf.transaction.impl.InternalTransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Spinner;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.tasks.api.IPropertySection;
import org.ruminaq.tasks.gate.model.gate.Gate;

public class GatePropertySection implements IPropertySection {

  private Composite root;
  private CLabel lblNumberOfInputs;
  private Spinner spnNumberOfInputs;

  private PictogramElement pe;
  private IDiagramTypeProvider dtp;

  public GatePropertySection(Composite parent, PictogramElement pe,
      TransactionalEditingDomain ed, IDiagramTypeProvider dtp) {
    this.pe = pe;
    this.dtp = dtp;
    initLayout(parent);
    initActions(ed);
    initComponents(ed);
    addStyles();
  }

  private void initLayout(Composite parent) {
    ((GridData) parent.getLayoutData()).verticalAlignment = SWT.FILL;
    ((GridData) parent.getLayoutData()).grabExcessVerticalSpace = true;
    root = new Composite(parent, SWT.NULL);
    root.setLayout(new GridLayout(2, false));

    lblNumberOfInputs = new CLabel(root, SWT.NONE);
    spnNumberOfInputs = new Spinner(root, SWT.BORDER);
  }

  private void initActions(final TransactionalEditingDomain ed) {
    spnNumberOfInputs.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent se) {
        ModelUtil.runModelChange(new Runnable() {
          public void run() {
            Object bo = Graphiti.getLinkService()
                .getBusinessObjectForLinkedPictogramElement(pe);
            InternalTransaction a = ((InternalTransactionalEditingDomain) ed)
                .getActiveTransaction();
            if (a == null || a.isReadOnly() == true)
              return;
            if (bo == null)
              return;
            if (bo instanceof Gate) {
              Gate element = (Gate) bo;
              element.setInputNumber(spnNumberOfInputs.getSelection());
              UpdateContext context = new UpdateContext(pe);
              dtp.getFeatureProvider().updateIfPossible(context);
            }
          }
        }, ed, "Model Update");
        refresh(pe, ed);
      }
    });
  }

  private void initComponents(TransactionalEditingDomain ed) {
    lblNumberOfInputs.setText("Number of inputs:");
    spnNumberOfInputs.setMinimum(2);
  }

  private void addStyles() {
    root.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    lblNumberOfInputs
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    spnNumberOfInputs
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
  }

  @Override
  public void refresh(PictogramElement pe, TransactionalEditingDomain ed) {
    if (pe != null) {
      Object bo = Graphiti.getLinkService()
          .getBusinessObjectForLinkedPictogramElement(pe);
      if (bo == null)
        return;
      final Gate g = (Gate) bo;
      // Number of Inputs
      spnNumberOfInputs.setSelection(g.getInputNumber());
    }
  }

}
