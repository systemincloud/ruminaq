/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.editor;

import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.navigator.ILinkHelper;
import org.eclipse.ui.part.FileEditorInput;

/**
 * Provides information to the Navigator on how to link Ruminaq editor with
 * selection of diagram nodes.
 *
 * @author Marek Jagielski
 */
public class EditorLinkHelper implements ILinkHelper {

  @Override
  public IStructuredSelection findSelection(IEditorInput editorInput) {
    return Optional.ofNullable(editorInput).filter(IEditorInput::exists)
        .filter(ei -> ei instanceof DiagramEditorInput)
        .map(ei -> (DiagramEditorInput) ei).map(DiagramEditorInput::getUri)
        .map(uri -> getFile(uri)).map(file -> new StructuredSelection(file))
        .orElse(StructuredSelection.EMPTY);
  }

  @Override
  public void activateEditor(IWorkbenchPage page,
      IStructuredSelection selection) {
    Optional.ofNullable(selection).filter(s -> !s.isEmpty())
        .map(IStructuredSelection::getFirstElement)
        .filter(o -> o instanceof IFile).map(s -> (IFile) s)
        .map(f -> new FileEditorInput(f)).map(fei -> page.findEditor(fei))
        .ifPresent(e -> page.bringToTop(e));
  }

  private IFile getFile(URI uri) {
    IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
    return (IFile) Optional.ofNullable(uri).map(URI::trimFragment)
        .map(u -> getWorkspaceFilePath(u)).map(fp -> workspaceRoot.findMember(fp)).orElseGet(() -> {
          IPath location = Path.fromOSString(uri.toString());
          return workspaceRoot.getFileForLocation(location);
        });
  }

  private static String getWorkspaceFilePath(URI uri) {
    if (uri.isPlatform()) {
      return uri.toPlatformString(true);
    } else {
      return null;
    }
  }
}
