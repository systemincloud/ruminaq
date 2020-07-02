/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.debug.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.ruminaq.debug.model.vars.SicVariable;

public class SicDebugModelPresentation implements IDebugModelPresentation {

  public static final String ID = "org.ruminaq.debug.model.SicDebugModelPresentation";

  @Override
  public void addListener(ILabelProviderListener listener) {
  }

  @Override
  public void dispose() {
  }

  @Override
  public boolean isLabelProperty(Object element, String property) {
    return false;
  }

  @Override
  public void removeListener(ILabelProviderListener listener) {
  }

  @Override
  public IEditorInput getEditorInput(Object element) {
    if (element instanceof IFile)
      return new FileEditorInput((IFile) element);

    return null;
  }

  @Override
  public String getEditorId(IEditorInput input, Object element) {
    if (element instanceof IFile)
      return PlatformUI.getWorkbench().getEditorRegistry()
          .getDefaultEditor(((IFile) element).getName()).getId();

    return null;
  }

  @Override
  public void setAttribute(String attribute, Object value) {
  }

  @Override
  public Image getImage(Object element) {
    return null;
  }

  @Override
  public String getText(Object element) {
    return null;
  }

  @Override
  public void computeDetail(IValue value, IValueDetailListener listener) {
    if (value instanceof SicVariable)
      listener.detailComputed(value, ((SicVariable) value).getDetailText());
  }

}
