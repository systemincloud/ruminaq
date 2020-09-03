/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.properties;

import java.util.Optional;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import winterwell.markdown.pagemodel.MarkdownPage;

/**
 *
 * @author Marek Jagielski
 */
public class PropertyDescriptionSection extends GFPropertySection
    implements ITabbedPropertyConstants {

  private Browser b;

  @Override
  public void createControls(Composite parent,
      TabbedPropertySheetPage tabbedPropertySheetPage) {
    super.createControls(parent, tabbedPropertySheetPage);

    ((GridData) parent.getLayoutData()).verticalAlignment = SWT.FILL;
    ((GridData) parent.getLayoutData()).grabExcessVerticalSpace = true;

    b = new Browser(parent, SWT.NONE);
    parent.setLayout(new GridLayout());
    GridData gd = new GridData(GridData.FILL_BOTH);
    gd.grabExcessVerticalSpace = true;
    gd.grabExcessHorizontalSpace = true;
    b.setLayoutData(gd);
    b.addListener(SWT.MenuDetect, event -> event.doit = false);
  }

  @Override
  public void refresh() {
    Optional.ofNullable(getSelectedPictogramElement())
        .filter(RuminaqShape.class::isInstance).map(RuminaqShape.class::cast)
        .map(RuminaqShape::getModelObject)
        .map(PropertyDescriptionSection::getPage).ifPresent(b::setText);
  }

  @Override
  public void setInput(IWorkbenchPart part, ISelection selection) {
    super.setInput(part, selection);
  }

  private static String getPage(EObject bo) {
    return new MarkdownPage("XXX").html();
  }
}
