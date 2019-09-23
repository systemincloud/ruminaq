/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.console.gui;

import java.util.Map;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.modeler.tasks.console.ui.views.ConsoleViewPart;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.tasks.api.IPropertySection;
import org.ruminaq.tasks.api.ITaskUiApi;
import org.ruminaq.tasks.api.IView;

@Component
public class TaskUiApi implements ITaskUiApi {

  private String symbolicName;
  private Version version;

  @Activate
  void activate(Map<String, Object> properties) {
    Bundle b = FrameworkUtil.getBundle(getClass());
    symbolicName = b.getSymbolicName();
    version = b.getVersion();
  }

  @Override
  public String getSymbolicName() {
    return symbolicName;
  }

  @Override
  public Version getVersion() {
    return version;
  }

  @Override
  public IPropertySection createPropertySection(Composite parent,
      PictogramElement pe, TransactionalEditingDomain ed,
      IDiagramTypeProvider dtp) {
    return new PropertySection(parent, pe, ed, dtp);
  }

  @Override
  public IView createView(Class<? extends ViewPart> viewClass) {
    return viewClass.isAssignableFrom(ConsoleViewPart.class) ? new ConsoleView()
        : null;
  }
}
