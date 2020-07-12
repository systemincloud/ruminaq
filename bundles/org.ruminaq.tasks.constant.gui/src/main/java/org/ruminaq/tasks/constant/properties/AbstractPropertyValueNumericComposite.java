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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.tasks.constant.api.PropertyValueComposite;
import org.ruminaq.tasks.constant.api.ValueSaveListener;
import org.ruminaq.tasks.constant.model.constant.Constant;

public abstract class AbstractPropertyValueNumericComposite
    extends PropertyValueComposite {

  private static final int WIDTH = 75;

  private Text txtValue;

  public AbstractPropertyValueNumericComposite(ValueSaveListener saveListener,
      Composite valueRoot, PictogramElement pe, TransactionalEditingDomain ed) {
    super(saveListener, valueRoot, pe, ed);
    initLayout();
    initActions();
    addStyles();
  }

  private void initLayout() {
    composite = new Composite(this.valueRoot, SWT.NONE);
    GridLayout layout = new GridLayout(1, false);
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    layout.horizontalSpacing = 0;
    composite.setLayout(layout);

    txtValue = new Text(composite, SWT.BORDER);
    GridData layoutValue = new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1);
    layoutValue.minimumWidth = WIDTH;
    layoutValue.widthHint = WIDTH;
    txtValue.setLayoutData(layoutValue);
  }

  private void initActions() {
    txtValue.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent event) {
        Shell shell = txtValue.getShell();
        if (verify(txtValue.getText())) {
          ModelUtil.runModelChange(() -> {
            Object bo = Graphiti.getLinkService()
                .getBusinessObjectForLinkedPictogramElement(pe);
            if (bo instanceof Constant) {
              Constant c = (Constant) bo;
              c.setValue(txtValue.getText());
              saveListener.update();
            }
          }, ed, "Change Constant");
        } else {
          MessageDialog.openError(shell, "Can't edit value",
              "Don't understant numeric value");
        }
      }
    });
    txtValue.addTraverseListener((TraverseEvent event) -> {
      if (event.detail == SWT.TRAVERSE_RETURN) {
        saveListener.setFocus();
      }
    });
  }

  private void addStyles() {
    composite
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    txtValue
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
  }

  @Override
  public String getValue() {
    return txtValue.getText();
  }

  @Override
  public void refresh(String value) {
    if (verify(value)) {
      txtValue.setText(value);
    } else {
      txtValue.setText(getDefault());
    }
    txtValue.redraw();
  }

  protected abstract boolean verify(String value);

  protected abstract String getDefault();
}
