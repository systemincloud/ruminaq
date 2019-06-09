/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.eclipse.prefs;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 *
 * @author Marek Jagielski
 */
public class CommonsPropsPage extends PropertyPage {

  public CommonsPropsPage() {
  }

  @Override
  protected Control createContents(Composite parent) {
    noDefaultAndApplyButton();
    return null;
  }
}
