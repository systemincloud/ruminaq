/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.ruminaq.tasks.console.gui;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Spinner;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.tasks.api.IPropertySection;
import org.ruminaq.tasks.console.model.console.Console;
import org.ruminaq.tasks.console.model.console.ConsoleType;

public class PropertySection implements IPropertySection {

  private Composite composite;
  private CLabel lblType;
  private Combo cmbType;
  private CLabel lblNewLine;
  private Combo newLineCombo;
  private CLabel lblMatricesPretty;
  private Button btnMatricesPretty;
  private CLabel lblNbLines;
  private Spinner spnNbLines;

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

    lblType = new CLabel(composite, SWT.NONE);

    cmbType = new Combo(composite, SWT.READ_ONLY);
    cmbType.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

    lblNewLine = new CLabel(composite, SWT.NONE);

    newLineCombo = new Combo(composite, SWT.READ_ONLY);

    lblMatricesPretty = new CLabel(composite, SWT.NONE);
    btnMatricesPretty = new Button(composite, SWT.CHECK);

    lblNbLines = new CLabel(composite, SWT.NONE);
    spnNbLines = new Spinner(composite, SWT.BORDER);
    GridData layoutLines = new GridData();
    layoutLines.minimumWidth = 40;
    layoutLines.widthHint = 40;
    spnNbLines.setLayoutData(layoutLines);
  }

  private void initComponents() {
    lblType.setText("Type:");

    cmbType.setItems(new String[] { ConsoleType.IN.toString(),
        ConsoleType.OUT.toString(), ConsoleType.INOUT.toString() });

    lblNewLine.setText("New line:");

    newLineCombo.setItems(
        new String[] { Boolean.TRUE.toString(), Boolean.FALSE.toString() });

    lblMatricesPretty.setText("Pretty Matrices:");

    lblNbLines.setText("Number of lines:");
    spnNbLines.setMinimum(0);
    spnNbLines.setMaximum(Integer.MAX_VALUE);
  }

  private void initActions(final PictogramElement pe,
      final TransactionalEditingDomain ed, final IDiagramTypeProvider dtp) {
    cmbType.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        final ConsoleType ct = ConsoleType.getByName(cmbType.getText());
        ModelUtil.runModelChange(new Runnable() {
          public void run() {
            Object bo = Graphiti.getLinkService()
                .getBusinessObjectForLinkedPictogramElement(pe);
            if (bo == null)
              return;
            if (ct != null) {
              if (bo instanceof Console) {
                Console console = (Console) bo;
                console.setConsoleType(ct);
                UpdateContext context = new UpdateContext(pe);
                dtp.getFeatureProvider().updateIfPossible(context);
              }
            }
          }
        }, ed, "Change console type");
      }
    });
    newLineCombo.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        final boolean newLine = Boolean.parseBoolean(newLineCombo.getText());
        ModelUtil.runModelChange(new Runnable() {
          public void run() {
            Object bo = Graphiti.getLinkService()
                .getBusinessObjectForLinkedPictogramElement(pe);
            if (bo == null)
              return;
            if (bo instanceof Console) {
              Console console = (Console) bo;
              console.setNewLine(newLine);
            }
          }
        }, ed, "Change console type");
      }
    });
    btnMatricesPretty.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        ModelUtil.runModelChange(new Runnable() {
          public void run() {
            Object bo = Graphiti.getLinkService()
                .getBusinessObjectForLinkedPictogramElement(pe);
            if (bo == null)
              return;
            if (bo instanceof Console) {
              Console console = (Console) bo;
              console.setMatricesPretty(btnMatricesPretty.getSelection());
            }
          }
        }, ed, "Change console type");
      }
    });
    spnNbLines.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent se) {
        ModelUtil.runModelChange(new Runnable() {
          public void run() {
            Object bo = Graphiti.getLinkService()
                .getBusinessObjectForLinkedPictogramElement(pe);
            if (bo == null)
              return;
            if (bo instanceof Console) {
              Console console = (Console) bo;
              console.setNbOfLines(spnNbLines.getSelection());
            }
          }
        }, ed, "Model Update");
        refresh(pe, ed);
      }
    });
  }

  private void addStyles() {
    composite
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    lblType.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    lblNewLine
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    lblMatricesPretty
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    lblNbLines
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    spnNbLines
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
  }

  @Override
  public void refresh(PictogramElement pe, TransactionalEditingDomain ed) {
    if (pe != null) {
      Object bo = Graphiti.getLinkService()
          .getBusinessObjectForLinkedPictogramElement(pe);
      if (bo == null || !(bo instanceof Console))
        return;
      Console console = (Console) bo;

      int i = 0;
      for (String dt : cmbType.getItems()) {
        if (dt.equals(console.getConsoleType().toString()))
          break;
        i++;
      }
      cmbType.select(i);

      i = 0;
      for (String dt : newLineCombo.getItems()) {
        if (dt.equals(Boolean.toString(console.isNewLine())))
          break;
        i++;
      }
      newLineCombo.select(i);

      btnMatricesPretty.setSelection(console.isMatricesPretty());

      spnNbLines.setSelection(console.getNbOfLines());
      ((GridData) spnNbLines.getLayoutData()).widthHint = 20
          + 40 * String.valueOf(console.getNbOfLines()).length();
      spnNbLines.redraw();
    }
  }

}
