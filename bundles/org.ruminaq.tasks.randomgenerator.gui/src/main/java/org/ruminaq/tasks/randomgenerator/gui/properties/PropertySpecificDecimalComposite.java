/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.randomgenerator.gui.properties;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.model.ruminaq.NumericUtil;
import org.ruminaq.tasks.randomgenerator.gui.ValueSaveListener;
import org.ruminaq.tasks.randomgenerator.impl.strategy.DecimalStrategy;
import org.ruminaq.tasks.randomgenerator.model.randomgenerator.RandomGenerator;
import org.ruminaq.util.GlobalUtil;

public class PropertySpecificDecimalComposite
    extends PropertySpecificNumericComposite {

  private CLabel lblDecimalNumber;
  private Text txtDecimalNumber;

  public PropertySpecificDecimalComposite(ValueSaveListener saveListener,
      Composite specificRoot, PictogramElement pe,
      TransactionalEditingDomain ed) {
    super(saveListener, specificRoot, pe, ed);
    initLayout();
    initComponents();
    initActions();
    addStyles();
  }

  private void initLayout() {
    lblDecimalNumber = new CLabel(composite, SWT.NONE);
    txtDecimalNumber = new Text(composite, SWT.BORDER);
    GridData layoutDecimalNumber = new GridData(SWT.LEFT, SWT.CENTER, false,
        false, 1, 1);
    layoutDecimalNumber.minimumWidth = 75;
    layoutDecimalNumber.widthHint = 75;
    txtDecimalNumber.setLayoutData(layoutDecimalNumber);
  }

  private void initComponents() {
    lblDecimalNumber.setText("Number of Decimals:");
  }

  private void initActions() {
    txtDecimalNumber.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent event) {
        Shell shell = txtDecimalNumber.getShell();
        boolean parse = GlobalUtil
            .isIntegerAlsoGVandRand(txtDecimalNumber.getText());
        if (parse) {
          ModelUtil.runModelChange(new Runnable() {
            public void run() {
              Object bo = Graphiti.getLinkService()
                  .getBusinessObjectForLinkedPictogramElement(pe);
              if (bo == null)
                return;
              if (bo instanceof RandomGenerator) {
                RandomGenerator rg = (RandomGenerator) bo;
                rg.getSpecific().put(DecimalStrategy.DECIMAL_NUMBER,
                    txtDecimalNumber.getText());
              }
            }
          }, ed, "Change dimensions");
        } else
          MessageDialog.openError(shell, "Can't edit value",
              "Don't understant dimensions");
      }
    });
    txtDecimalNumber.addTraverseListener(new TraverseListener() {
      @Override
      public void keyTraversed(TraverseEvent event) {
        if (event.detail == SWT.TRAVERSE_RETURN)
          saveListener.setFocus();
      }
    });
  }

  private void addStyles() {
    lblDecimalNumber
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    txtDecimalNumber
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
  }

  @Override
  public void initValues(EMap<String, String> eMap) {
    String number = eMap.get(DecimalStrategy.DECIMAL_NUMBER);
    if (number == null) {
      eMap.put(DecimalStrategy.DECIMAL_NUMBER,
          Integer.toString(DecimalStrategy.DEFAULT_DECIMAL_NUMBER));
    }
  }

  @Override
  public void refresh(EMap<String, String> eMap) {
    txtDecimalNumber.setText(eMap.get(DecimalStrategy.DECIMAL_NUMBER));
  }

  @Override
  protected boolean checkIfValue(String value) {
    return NumericUtil.isMultiDimsNumeric(value);
  }
}
