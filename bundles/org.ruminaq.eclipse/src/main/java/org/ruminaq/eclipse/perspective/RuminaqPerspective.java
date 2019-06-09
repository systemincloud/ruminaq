/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.eclipse.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

/**
 *
 * @author Marek Jagielski
 */
public class RuminaqPerspective implements IPerspectiveFactory {

  @Override
  public void createInitialLayout(IPageLayout layout) {
    defineActions(layout);
    defineLayout(layout);
  }

  private void defineActions(IPageLayout layout) {

    // Add "new wizards".
    layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder"); //$NON-NLS-1$
    layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file"); //$NON-NLS-1$
    layout.addNewWizardShortcut(
        "org.ruminaq.eclipse.wizards.project.CreateProjectWizard"); //$NON-NLS-1$
    layout.addNewWizardShortcut(
        "org.ruminaq.eclipse.wizards.diagram.CreateDiagramWizard"); //$NON-NLS-1$
    layout.addNewWizardShortcut(
        "org.ruminaq.eclipse.wizards.diagram.CreateTestDiagramWizard"); //$NON-NLS-1$

    // Add "show views".
    layout.addShowViewShortcut(IPageLayout.ID_PROJECT_EXPLORER);
    layout.addShowViewShortcut(IPageLayout.ID_BOOKMARKS);
    layout.addShowViewShortcut(
        "org.eclipse.graphiti.ui.internal.editor.thumbnailview"); //$NON-NLS-1$
    layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
    layout.addShowViewShortcut("org.eclipse.ui.views.AllMarkersView"); //$NON-NLS-1$
    layout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW); // $NON-NLS-1$

    layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);
    layout.addActionSet("org.eclipse.debug.ui.launchActionSet"); //$NON-NLS-1$
  }

  private void defineLayout(IPageLayout layout) {
    // Editors are placed for free.
    String editorArea = layout.getEditorArea();

    // Top left.
    IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, //$NON-NLS-1$
        (float) 0.26, editorArea);
    topLeft.addView(IPageLayout.ID_PROJECT_EXPLORER);
    topLeft.addPlaceholder(IPageLayout.ID_BOOKMARKS);

    // Bottom left.
    IFolderLayout bottomLeft = layout.createFolder("bottomLeft", //$NON-NLS-1$
        IPageLayout.BOTTOM, (float) 0.50, "topLeft");
    bottomLeft.addView("org.eclipse.graphiti.ui.internal.editor.thumbnailview");//$NON-NLS-1$

    // Bottom right.
    IFolderLayout bottomRight = layout.createFolder("bottomRight", //$NON-NLS-1$
        IPageLayout.BOTTOM, (float) 0.66, editorArea);

    bottomRight.addView(IPageLayout.ID_PROP_SHEET);
    bottomRight.addView("org.eclipse.ui.views.AllMarkersView");//$NON-NLS-1$
    bottomRight.addView(IConsoleConstants.ID_CONSOLE_VIEW);
  }
}
