/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.debug;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;

public class InternalPortBreakpointPage extends PropertyPage {

  private InternalPortBreakpointDetailPane dp;

  @Override
  @SuppressWarnings("restriction")
  protected Control createContents(Composite parent) {
    dp = new InternalPortBreakpointDetailPane();
    Control ret = dp.createControl(parent);
    try {
      dp.getEditor().setInput(getBreakpoint());
    } catch (CoreException e) {
    }
    return ret;
  }

  @Override
  @SuppressWarnings("restriction")
  public boolean performOk() {
    try {
      dp.getEditor().doSave();
    } catch (CoreException e) {
    }
    return super.performOk();
  }

  private InternalPortBreakpoint getBreakpoint() {
    return (InternalPortBreakpoint) getElement();
  }

  @Override
  public boolean performCancel() {
    return super.performCancel();
  }

  @Override
  public void createControl(Composite parent) {
    super.createControl(parent);
  }
}
