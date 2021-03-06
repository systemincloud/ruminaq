/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.sipo.gui;

import java.util.Optional;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.impl.InternalTransaction;
import org.eclipse.emf.transaction.impl.InternalTransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.ruminaq.gui.model.diagram.TaskShape;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.tasks.sipo.model.sipo.Sipo;
import org.ruminaq.util.GroovyExpressionUtil;
import org.ruminaq.util.NumericUtil;

public class PropertySection {

  private Composite root;

  private CLabel lblSize;
  private Composite cmpSize;
  private StackLayout stkSize;
  private Spinner spnSize;
  private Text txtSize;

  private Button btnIdx;
  private Button btnClk;
  private Button btnTrig;
  private Button btnSizeOut;

  public PropertySection(Composite parent, PictogramElement pe,
      TransactionalEditingDomain ed, IDiagramTypeProvider dtp) {
    initLayout(parent);
    initComponents();
    initActions(pe, ed, dtp);
  }

  public static Optional<TaskShape> shapeFrom(PictogramElement pe) {
    return Optional.ofNullable(pe).filter(TaskShape.class::isInstance)
        .map(TaskShape.class::cast);
  }

  public static Optional<Sipo> modelFrom(PictogramElement pe) {
    return shapeFrom(pe).map(TaskShape::getModelObject)
        .filter(Sipo.class::isInstance).map(Sipo.class::cast);
  }

  private void initLayout(Composite parent) {
    ((GridData) parent.getLayoutData()).verticalAlignment = SWT.FILL;
    ((GridData) parent.getLayoutData()).grabExcessVerticalSpace = true;

    root = new Composite(parent, SWT.NULL);
    root.setLayout(new GridLayout(2, false));

    lblSize = new CLabel(root, SWT.NONE);
    cmpSize = new Composite(root, SWT.NONE);
    stkSize = new StackLayout();
    cmpSize.setLayout(stkSize);
    spnSize = new Spinner(cmpSize, SWT.BORDER);
    txtSize = new Text(cmpSize, SWT.BORDER);
    GridData lytSize = new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1);
    lytSize.minimumWidth = 75;
    lytSize.widthHint = 75;
    txtSize.setLayoutData(lytSize);

    btnIdx = new Button(root, SWT.CHECK);
    new CLabel(root, SWT.NONE)
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    btnClk = new Button(root, SWT.CHECK);
    new CLabel(root, SWT.NONE)
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    btnTrig = new Button(root, SWT.CHECK);
    btnSizeOut = new Button(root, SWT.CHECK);
  }

  private void initComponents() {
    lblSize.setText("Size:");
    spnSize.setMinimum(1);
    spnSize.setMaximum(50);
    btnIdx.setText("Index");
    btnClk.setText("Clock");
    btnTrig.setText("Trigger");
    btnSizeOut.setText("Nb of elements");
  }

  private void initActions(final PictogramElement pe,
      final TransactionalEditingDomain ed, final IDiagramTypeProvider dtp) {
    spnSize.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent se) {
        ModelUtil.runModelChange(() -> {
          InternalTransaction a = ((InternalTransactionalEditingDomain) ed)
              .getActiveTransaction();
          if (a == null || a.isReadOnly() == true)
            return;
          modelFrom(pe).ifPresent((Sipo sipo) -> {
            sipo.setSize(Integer.toString(spnSize.getSelection()));
            UpdateContext context = new UpdateContext(pe);
            dtp.getFeatureProvider().updateIfPossible(context);
          });
          root.layout();
        }, ed, "Model Update");
      }
    });
    txtSize.addTraverseListener(new TraverseListener() {
      @Override
      public void keyTraversed(TraverseEvent event) {
        if (event.detail == SWT.TRAVERSE_RETURN) {
          ModelUtil.runModelChange(() -> {
            boolean parse = GroovyExpressionUtil
                .isOneDimIntegerAlsoGV(txtSize.getText());
            if (parse) {
              InternalTransaction a = ((InternalTransactionalEditingDomain) ed)
                  .getActiveTransaction();
              if (a == null || a.isReadOnly() == true)
                return;
              modelFrom(pe).ifPresent(sipo -> sipo.setSize(txtSize.getText()));
            } else
              MessageDialog.openError(txtSize.getShell(), "Can't edit value",
                  "Don't understant numeric value");
          }, ed, "Model Update");
        }
      }
    });
    btnIdx.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent se) {
        ModelUtil.runModelChange(() -> {
          InternalTransaction a = ((InternalTransactionalEditingDomain) ed)
              .getActiveTransaction();
          if (a == null || a.isReadOnly() == true)
            return;
          modelFrom(pe).ifPresent((Sipo sipo) -> {
            sipo.setIndex(btnIdx.getSelection());
            if (btnIdx.getSelection()) {
              txtSize.setVisible(true);
              spnSize.setVisible(false);
              txtSize.setText(Integer.toString(spnSize.getSelection()));
            } else {
              txtSize.setVisible(false);
              spnSize.setVisible(true);
              if (NumericUtil.isOneDimPositiveInteger(txtSize.getText()))
                spnSize.setSelection(Integer.parseInt(txtSize.getText()));
              else
                sipo.setSize(Integer.toString(spnSize.getSelection()));
            }
            UpdateContext context = new UpdateContext(pe);
            dtp.getFeatureProvider().updateIfPossible(context);
            if (btnIdx.getSelection())
              btnTrig.setEnabled(false);
            else
              btnTrig.setEnabled(true);
          });
        }, ed, "Model Update");
      }
    });
    btnClk.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent se) {
        ModelUtil.runModelChange(() -> {
          InternalTransaction a = ((InternalTransactionalEditingDomain) ed)
              .getActiveTransaction();
          if (a == null || a.isReadOnly() == true)
            return;
          modelFrom(pe).ifPresent((Sipo sipo) -> {
            sipo.setClock(btnClk.getSelection());
            UpdateContext context = new UpdateContext(pe);
            dtp.getFeatureProvider().updateIfPossible(context);
          });
        }, ed, "Model Update");
      }
    });
    btnTrig.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent se) {
        ModelUtil.runModelChange(() -> {
          InternalTransaction a = ((InternalTransactionalEditingDomain) ed)
              .getActiveTransaction();
          if (a == null || a.isReadOnly() == true)
            return;
          modelFrom(pe).ifPresent((Sipo sipo) -> {
            sipo.setTrigger(btnTrig.getSelection());
            UpdateContext context = new UpdateContext(pe);
            dtp.getFeatureProvider().updateIfPossible(context);
          });
        }, ed, "Model Update");
      }
    });
    btnSizeOut.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent se) {
        ModelUtil.runModelChange(() -> {
          InternalTransaction a = ((InternalTransactionalEditingDomain) ed)
              .getActiveTransaction();
          if (a == null || a.isReadOnly() == true)
            return;
          modelFrom(pe).ifPresent((Sipo sipo) -> {
            sipo.setSizeOut(btnSizeOut.getSelection());
            UpdateContext context = new UpdateContext(pe);
            dtp.getFeatureProvider().updateIfPossible(context);
          });
        }, ed, "Model Update");
      }
    });
  }

  public void refresh(PictogramElement pe, TransactionalEditingDomain ed) {
    if (pe != null) {
      Object bo = Graphiti.getLinkService()
          .getBusinessObjectForLinkedPictogramElement(pe);
      if (bo == null)
        return;
      Sipo sipo = (Sipo) bo;
      btnIdx.setSelection(sipo.isIndex());
      if (sipo.isIndex()) {
        stkSize.topControl = txtSize;
        txtSize.setText(sipo.getSize());
      } else {
        stkSize.topControl = spnSize;
        spnSize.setSelection(Integer.parseInt(sipo.getSize()));
      }
      cmpSize.layout();
      btnClk.setSelection(sipo.isClock());
      btnTrig.setSelection(sipo.isTrigger());
      btnSizeOut.setSelection(sipo.isSizeOut());
      if (btnIdx.getSelection())
        btnTrig.setEnabled(false);
      else
        btnTrig.setEnabled(true);
      root.layout();
    }
  }
}
