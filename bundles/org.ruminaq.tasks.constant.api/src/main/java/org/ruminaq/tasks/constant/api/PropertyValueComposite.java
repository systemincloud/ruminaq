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
package org.ruminaq.tasks.constant.api;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.widgets.Composite;

public abstract class PropertyValueComposite {
  protected ValueSaveListener saveListener;
  protected Composite valueRoot;
  protected PictogramElement pe;
  protected TransactionalEditingDomain ed;

  protected Composite composite;

  public Composite getComposite() {
    return composite;
  }

  public PropertyValueComposite(ValueSaveListener saveListener,
      Composite valueRoot, PictogramElement pe, TransactionalEditingDomain ed) {
    this.saveListener = saveListener;
    this.valueRoot = valueRoot;
    this.pe = pe;
    this.ed = ed;
  }

  public abstract String getValue();

  public abstract void refresh(String value);
}
