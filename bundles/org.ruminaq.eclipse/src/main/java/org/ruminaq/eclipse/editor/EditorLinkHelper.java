/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.navigator.ILinkHelper;
import org.eclipse.ui.part.FileEditorInput;

/**
 * Provides information to the Navigator on how to
 * link Ruminaq editor with selection of diagram nodes.
 *
 * @author Marek Jagielski
 */
public class EditorLinkHelper implements ILinkHelper {

  @Override
  public IStructuredSelection findSelection(IEditorInput editorInput) {
    if (editorInput instanceof DiagramEditorInput) {
      DiagramEditorInput diagramEditorInput = (DiagramEditorInput) editorInput;
      final IFile file = getFile(diagramEditorInput.getUri());
      if (editorInput.exists() && file != null) {
        return new StructuredSelection(file);
      }
    }
    return StructuredSelection.EMPTY;
  }

  @Override
  public void activateEditor(IWorkbenchPage page,
      IStructuredSelection selection) {
    if (selection == null || selection.isEmpty())
      return;
    if (selection.getFirstElement() instanceof IFile) {
      IEditorInput fileInput = new FileEditorInput(
          (IFile) selection.getFirstElement());
      IEditorPart editor = page.findEditor(fileInput);
      if (editor != null)
        page.bringToTop(editor);
    }
  }

  private IFile getFile(URI uri) {
    if (uri == null)
      return null;

    final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace()
        .getRoot();

    final String filePath = getWorkspaceFilePath(uri.trimFragment());
    if (filePath == null) {
      final IPath location = Path.fromOSString(uri.toString());
      final IFile file = workspaceRoot.getFileForLocation(location);
      return file != null ? file : null;
    } else {
      final IResource workspaceResource = workspaceRoot.findMember(filePath);
      return (IFile) workspaceResource;
    }
  }

  private String getWorkspaceFilePath(URI uri) {
    return uri.isPlatform() ? uri.toPlatformString(true) : null;
  }
}
