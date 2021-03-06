/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.debug;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.internal.ui.model.elements.BreakpointLabelProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementLabelProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.jface.viewers.TreePath;

@SuppressWarnings("restriction")
public class InternalPortBreakpointWorkbenchAdapterFactory
    implements IAdapterFactory {

  @Override
  @SuppressWarnings("rawtypes")
  public Object getAdapter(final Object adaptableObject, Class adapterType) {
    if (adapterType != IElementLabelProvider.class
        || !(adaptableObject instanceof InternalPortBreakpoint))
      return null;
    return new BreakpointLabelProvider() {
      @Override
      protected String getLabel(TreePath tp, IPresentationContext pc, String s)
          throws CoreException {
        InternalPortBreakpoint breakpoint = (InternalPortBreakpoint) adaptableObject;
        StringBuilder sb = new StringBuilder();
        Path p = Paths.get(breakpoint.getDiagramPath());
        sb.append(".../");
        sb.append(p.subpath(p.getNameCount() - 2, p.getNameCount()));
        sb.append(" - ");
        sb.append(breakpoint.getTaskId());
        sb.append(":");
        sb.append(breakpoint.getPortId());
        return sb.toString();
      }
    };
  }

  @Override
  public Class<?>[] getAdapterList() {
    return new Class[] { IElementLabelProvider.class };
  }
}
