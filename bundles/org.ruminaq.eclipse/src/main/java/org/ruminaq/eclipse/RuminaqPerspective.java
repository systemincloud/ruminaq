/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse;

import org.eclipse.graphiti.ui.internal.editor.ThumbNailView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;
import org.ruminaq.eclipse.wizards.diagram.CreateDiagramWizard;
import org.ruminaq.eclipse.wizards.diagram.CreateTestDiagramWizard;
import org.ruminaq.eclipse.wizards.project.CreateProjectWizard;

/**
 * Ruminaq default eclipse perspective.
 *
 * @author Marek Jagielski
 */
public class RuminaqPerspective implements IPerspectiveFactory {

  private static final String ID_MARKERS_VIEW = "org.eclipse.ui.views.AllMarkersView";
  private static final String SHORTCUT_FOLDER = "org.eclipse.ui.wizards.new.folder";
  private static final String SHORTCUT_FILE = "org.eclipse.ui.wizards.new.file";
  private static final String LAUNCH_COOLBAR_SHORTCUT = "org.eclipse.debug.ui.launchActionSet";

  private static final String TOP_LEFT_ID = "topLeft";
  private static final String BOTTOM_LEFT_ID = "bottomLeft";
  private static final String BOTTOM_RIGHT_ID = "bottomRight";

  private static final float TOP_LEFT_RATIO = 0.26F;
  private static final float BOTTOM_LEFT_RATIO = 0.50F;
  private static final float BOTTOM_RIGHT_RATIO = 0.66F;

  /**
   * Creates the initial layout for a page.
   *
   * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(IPageLayout
   *      layout)
   *
   * @param layout the page layout
   */
  @Override
  public void createInitialLayout(IPageLayout layout) {
    defineActions(layout);
    defineLayout(layout);
  }

  private static void defineActions(IPageLayout layout) {

    // Add "new wizards".
    layout.addNewWizardShortcut(SHORTCUT_FOLDER);
    layout.addNewWizardShortcut(SHORTCUT_FILE);
    layout.addNewWizardShortcut(CreateProjectWizard.ID);
    layout.addNewWizardShortcut(CreateDiagramWizard.ID);
    layout.addNewWizardShortcut(CreateTestDiagramWizard.ID);

    // Add "show views".
    layout.addShowViewShortcut(IPageLayout.ID_PROJECT_EXPLORER);
    layout.addShowViewShortcut(IPageLayout.ID_BOOKMARKS);
    layout.addShowViewShortcut(ThumbNailView.VIEW_ID);
    layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
    layout.addShowViewShortcut(ID_MARKERS_VIEW);
    layout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);

    layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);
    layout.addActionSet(LAUNCH_COOLBAR_SHORTCUT);
  }

  private static void defineLayout(IPageLayout layout) {
    String editorArea = layout.getEditorArea();

    IFolderLayout topLeft = layout.createFolder(TOP_LEFT_ID, IPageLayout.LEFT,
        TOP_LEFT_RATIO, editorArea);
    topLeft.addView(IPageLayout.ID_PROJECT_EXPLORER);
    topLeft.addPlaceholder(IPageLayout.ID_BOOKMARKS);

    IFolderLayout bottomLeft = layout.createFolder(BOTTOM_LEFT_ID,
        IPageLayout.BOTTOM, BOTTOM_LEFT_RATIO, TOP_LEFT_ID);
    bottomLeft.addView(ThumbNailView.VIEW_ID);

    IFolderLayout bottomRight = layout.createFolder(BOTTOM_RIGHT_ID,
        IPageLayout.BOTTOM, BOTTOM_RIGHT_RATIO, editorArea);

    bottomRight.addView(IPageLayout.ID_PROP_SHEET);
    bottomRight.addView(ID_MARKERS_VIEW);
    bottomRight.addView(IConsoleConstants.ID_CONSOLE_VIEW);
  }
}
