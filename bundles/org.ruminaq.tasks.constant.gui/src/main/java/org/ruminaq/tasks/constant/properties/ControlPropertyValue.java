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
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.ruminaq.tasks.constant.api.PropertyValueComposite;
import org.ruminaq.tasks.constant.api.ValueSaveListener;

public class ControlPropertyValue extends PropertyValueComposite {

  private Label lblValue;

  public ControlPropertyValue(ValueSaveListener listener, Composite valueRoot,
      PictogramElement pe, TransactionalEditingDomain ed) {
    super(listener, valueRoot, pe, ed);
    // initLayout
    composite = new Composite(this.valueRoot, SWT.NONE);
    GridLayout layout = new GridLayout(1, false);
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    composite.setLayout(layout);
    lblValue = new Label(composite, SWT.NONE);
    lblValue.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

    // initComponents
    lblValue.setText("*");

    // addStyles
    composite
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    lblValue
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
  }

  @Override
  public String getValue() {
    return "*";
  }

  @Override
  public void refresh(String value) {
    lblValue.setText("*");
  }
}
