/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.tasks.constant.api.PropertyValueComposite;
import org.ruminaq.tasks.constant.api.ValueSaveListener;
import org.ruminaq.tasks.constant.model.constant.Constant;

public class TextPropertyValue extends PropertyValueComposite {

  private Button btnSave;
  private Text txtValue;

  public TextPropertyValue(ValueSaveListener listener, Composite valuecRoot,
      PictogramElement pe, TransactionalEditingDomain ed) {
    super(listener, valuecRoot, pe, ed);

    // initLayout
    composite = new Composite(this.valueRoot, SWT.NONE);
    GridLayout layout = new GridLayout(1, false);
    composite.setLayout(layout);
    btnSave = new Button(composite, SWT.BORDER | SWT.FLAT);
    txtValue = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.MULTI);
    txtValue.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

    // initComponents
    btnSave.setText("Save");
    txtValue.setText("");

    // initActions
    btnSave.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        save();
        saveListener.update();
      }
    });
    txtValue.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent event) {
        save();
        saveListener.update();
      }
    });

    // addStyles
    composite
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    btnSave.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
  }

  @Override
  public String getValue() {
    return txtValue.getText();
  }

  @Override
  public void refresh(String value) {
    if (value != null) {
      txtValue.setText(value);
    } else {
      txtValue.setText("");
    }
    txtValue.redraw();
  }

  private void save() {
    ModelUtil.runModelChange(() -> {
      Object bo = Graphiti.getLinkService()
          .getBusinessObjectForLinkedPictogramElement(pe);
      if (bo instanceof Constant) {
        Constant c = (Constant) bo;
        c.setValue(txtValue.getText());
      }
    }, ed, "Change Constant");
  }
}
