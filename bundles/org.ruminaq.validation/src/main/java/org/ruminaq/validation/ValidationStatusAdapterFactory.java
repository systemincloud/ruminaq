/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.validation;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

public class ValidationStatusAdapterFactory extends AdapterFactoryImpl {

  @Override
  public boolean isFactoryForType(Object type) {
    return type instanceof Class
        && ValidationStatusAdapter.class.isAssignableFrom((Class<?>) type);
  }

  @Override
  protected Adapter createAdapter(Notifier target) {
    return new ValidationStatusAdapter();
  }
}
