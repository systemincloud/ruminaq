/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.debug.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.ui.IDetailPane;
import org.eclipse.debug.ui.IDetailPaneFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.ruminaq.debug.InternalPortBreakpoint;

public class BreakpointDetailPaneFactory implements IDetailPaneFactory {

  private Map<String, String> paneNameMap;

  @Override
  public IDetailPane createDetailPane(String paneID) {
    if (InternalPortBreakpointDetailPane.DETAIL_PANE_INTERNAL_PORT_BREAKPOINT
        .equals(paneID))
      return new InternalPortBreakpointDetailPane();
    return null;
  }

  @Override
  public String getDefaultDetailPane(IStructuredSelection selection) {
    if (selection.size() == 1) {
      Object o = selection.getFirstElement();
      if (o instanceof IBreakpoint) {
        IBreakpoint b = (IBreakpoint) o;
        try {
          String type = b.getMarker().getType();
          if (InternalPortBreakpoint.INTERNAL_PORT_BREAKPOINT.equals(type))
            return InternalPortBreakpointDetailPane.DETAIL_PANE_INTERNAL_PORT_BREAKPOINT;
        } catch (CoreException e) {
        }
      }
    }
    return null;
  }

  @Override
  public Set<String> getDetailPaneTypes(IStructuredSelection selection) {
    if (selection.size() == 1) {
      Object o = selection.getFirstElement();
      if (o instanceof IBreakpoint) {
        IBreakpoint b = (IBreakpoint) o;
        try {
          String type = b.getMarker().getType();
          if (InternalPortBreakpoint.INTERNAL_PORT_BREAKPOINT.equals(type))
            return Collections.singleton(
                InternalPortBreakpointDetailPane.DETAIL_PANE_INTERNAL_PORT_BREAKPOINT);
        } catch (CoreException e) {
        }
      }
    }
    return Collections.emptySet();
  }

  @Override
  public String getDetailPaneDescription(String paneID) {
    return getNameMap().get(paneID);
  }

  @Override
  public String getDetailPaneName(String paneID) {
    return getNameMap().get(paneID);
  }

  private Map<String, String> getNameMap() {
    if (paneNameMap == null) {
      paneNameMap = new HashMap<String, String>();
      paneNameMap.put(
          InternalPortBreakpointDetailPane.DETAIL_PANE_INTERNAL_PORT_BREAKPOINT,
          InternalPortBreakpointDetailPane.DETAIL_PANE_INTERNAL_PORT_BREAKPOINT);
    }
    return paneNameMap;
  }
}
