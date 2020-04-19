package org.ruminaq.tasks.demux;

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
import org.ruminaq.tasks.demux.model.demux.Demux;

public class PropertySection implements IPropertySection {

  private Composite root;
  private CLabel lblNbOutputs;
  private Spinner spnNbOutputs;

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

    root = new Composite(parent, SWT.NULL);
    root.setLayout(new GridLayout(2, false));

    lblNbOutputs = new CLabel(root, SWT.NONE);
    spnNbOutputs = new Spinner(root, SWT.BORDER);
  }

  private void initComponents() {
    lblNbOutputs.setText("Nb of outputs:");
    spnNbOutputs.setMinimum(2);
  }

  private void initActions(final PictogramElement pe,
      final TransactionalEditingDomain ed, final IDiagramTypeProvider dtp) {
    spnNbOutputs.addSelectionListener(new SelectionAdapter() {
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
            if (bo instanceof Demux) {
              Demux element = (Demux) bo;
              element.setSize(spnNbOutputs.getSelection());
              UpdateContext context = new UpdateContext(pe);
              dtp.getFeatureProvider().updateIfPossible(context);
            }
          }
        }, ed, "Model Update");
        refresh(pe, ed);
      }
    });
  }

  private void addStyles() {
    root.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    lblNbOutputs
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    spnNbOutputs
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
  }

  @Override
  public void refresh(PictogramElement pe, TransactionalEditingDomain ed) {
    if (pe != null) {
      Object bo = Graphiti.getLinkService()
          .getBusinessObjectForLinkedPictogramElement(pe);
      if (bo == null)
        return;
      Demux m = (Demux) bo;
      spnNbOutputs.setSelection(m.getSize());
    }
  }
}
