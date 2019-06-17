/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.debug.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;

public class Stats extends RuminaqDebugElement implements IStackFrame, DiagramSource {

  private final IThread thread;

  public Stats(IDebugTarget target, IThread thread) {
    super(target);
    this.thread = thread;
  }

  @Override public IThread getThread()     { return thread; }
  @Override public int     getLineNumber() { return  0; }
  @Override public int     getCharStart()  { return -1; }
  @Override public int     getCharEnd()    { return -1; }
  @Override public String  getName()       { return "Stats"; }

  @Override public IFile getSourceFile() { return getDebugTarget().getFile(); }

  @Override
  public IVariable[] getVariables() {
    return new IVariable[0];
  }

  @Override
  public boolean hasVariables() {
    return getVariables().length > 0;
  }

  @Override
  public IRegisterGroup[] getRegisterGroups() {
    return new IRegisterGroup[0];
  }

  @Override
  public boolean hasRegisterGroups() {
    return getRegisterGroups().length > 0;
  }

}
