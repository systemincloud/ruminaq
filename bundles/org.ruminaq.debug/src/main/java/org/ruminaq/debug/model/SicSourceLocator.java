/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.debug.model;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IPersistableSourceLocator;
import org.eclipse.debug.core.model.IStackFrame;

public class SicSourceLocator implements IPersistableSourceLocator {

  @Override
  public Object getSourceElement(IStackFrame stackFrame) {
    if (stackFrame instanceof DiagramSource)
      return ((DiagramSource) stackFrame).getSourceFile();
    return null;
  }

  @Override
  public String getMemento() throws CoreException {
    return null;
  }

  @Override
  public void initializeFromMemento(String memento) throws CoreException {
  }

  @Override
  public void initializeDefaults(ILaunchConfiguration configuration)
      throws CoreException {
  }
}
