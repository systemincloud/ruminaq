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
import org.eclipse.swt.widgets.Composite;
import org.ruminaq.model.ruminaq.NumericUtil;
import org.ruminaq.tasks.constant.api.ValueSaveListener;

public class NumericPropertyValue
    extends AbstractPropertyValueNumericComposite {

  public static final String DEFAULT_VALUE = "0";

  public NumericPropertyValue(ValueSaveListener saveListener,
      Composite valueRoot, PictogramElement pe, TransactionalEditingDomain ed) {
    super(saveListener, valueRoot, pe, ed);
  }

  @Override
  protected boolean verify(String value) {
    return NumericUtil.isMultiDimsIntegerAlsoGV(value);
  }

  @Override
  protected String getDefault() {
    return DEFAULT_VALUE;
  }
}
