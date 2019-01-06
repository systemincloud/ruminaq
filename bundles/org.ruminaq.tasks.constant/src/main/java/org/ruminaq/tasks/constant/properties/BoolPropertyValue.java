/*
 * (C) Copyright 2018 Marek Jagielski.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ruminaq.tasks.constant.properties;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.ruminaq.model.util.ModelUtil;
import org.ruminaq.tasks.constant.api.PropertyValueComposite;
import org.ruminaq.tasks.constant.api.ValueSaveListener;
import org.ruminaq.tasks.constant.model.constant.Constant;
import org.ruminaq.util.NumericUtil;

public class BoolPropertyValue extends PropertyValueComposite{

    private static final String DEFINE = "define";
    private static final int WIDTH = 75;

    private Combo cmbValue;
    private Text txtValue;

    public BoolPropertyValue(
            ValueSaveListener listener,
            Composite valueRoot,
            PictogramElement pe,
            TransactionalEditingDomain ed) {
        super(listener, valueRoot, pe, ed);

        // initLayout
        composite = new Composite(this.valueRoot, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        composite.setLayout(layout);
        cmbValue = new Combo(composite, SWT.READ_ONLY);
        cmbValue.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        txtValue = new Text(composite, SWT.BORDER);
        GridData layoutDims = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
        layoutDims.minimumWidth = WIDTH;
        layoutDims.widthHint = WIDTH;
        txtValue.setLayoutData(layoutDims);

        // initComponents
        cmbValue.setItems(Boolean.FALSE.toString(), Boolean.TRUE.toString(), DEFINE);
        cmbValue.select(0);

        // initActions
        cmbValue.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ModelUtil.runModelChange(() -> {
                    Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
                    if (bo instanceof Constant) {
                        Constant c = (Constant) bo;
                        String value = cmbValue.getText();
                        if(value.equals(DEFINE)) {
                            c.setValue(txtValue.getText());
                            txtValue.setVisible(true);
                        } else {
                            c.setValue(value);
                            txtValue.setVisible(false);
                        }
                        saveListener.update();
                    }
                }, ed, "Change Constant");
            }
        });
        txtValue.addTraverseListener((TraverseEvent e) -> {
            if (!NumericUtil.isMultiDimsBoolAlsoGV(txtValue.getText())) {
                MessageDialog.openError(txtValue.getShell(), "Can't edit value", "Don't understant value");
            } else {
                if(e.detail == SWT.TRAVERSE_RETURN) {
                    ModelUtil.runModelChange(() -> {
                        Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
                        if (bo instanceof Constant) {
                            Constant c = (Constant) bo;
                            c.setValue(txtValue.getText());
                            saveListener.update();
                        }
                    }, ed, "Change Constant");
                }
            }
        });

        // addStyles
        composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

    }

    @Override
    public String getValue() {
        if (cmbValue.getText().equals(DEFINE)) {
            return txtValue.getText();
        } else {
            return cmbValue.getText();
        }
    }

    @Override
    public void refresh(String value) {
        int j = 0;
        for (String b : cmbValue.getItems()) {
            if (b.equals(value)) {
                break;
            }
            j++;
        }

        if (j == cmbValue.getItems().length) {
            txtValue.setText(value);
            txtValue.setVisible(true);
            j--;
        } else {
            txtValue.setVisible(false);
        }

        cmbValue.select(j);
    }
}
