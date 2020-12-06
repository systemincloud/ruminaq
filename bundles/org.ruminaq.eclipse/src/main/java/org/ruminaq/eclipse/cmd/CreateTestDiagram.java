/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.cmd;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.ruminaq.eclipse.EclipseUtil;
import org.ruminaq.eclipse.prefs.ProjectProps;
import org.ruminaq.eclipse.wizards.diagram.CreateDiagramWizard;
import org.ruminaq.eclipse.wizards.project.CreateSourceFolders;
import org.ruminaq.util.Try;

/**
 * Create diagram with EmbeddedTask in it. InternalInput and InternalOutput
 * ports of EmbeddedTask corresponds to Input and Output ports of original
 * diagram.
 *
 * @author Marek Jagielski
 */
public class CreateTestDiagram {

  public void generateTestDiagram(IResource file) {
    IProject project = file.getProject();

    IPath p = file.getFullPath();
    try {
      String path = CreateSourceFolders.TEST_DIAGRAM_FOLDER + "/"
          + Stream.of(p.segments()).skip(4).collect(Collectors.joining("/"));
      EclipseUtil.createFolderWithParents(project, path);

      String modelFileNameExt = p.segment(p.segmentCount() - 1);
      String modelFileName = modelFileNameExt.substring(0, modelFileNameExt
          .lastIndexOf(CreateDiagramWizard.DIAGRAM_EXTENSION_DOT));
      String modelTestName = modelFileName + "Test";
      String modelTestNameExt = modelTestName
          + CreateDiagramWizard.DIAGRAM_EXTENSION_DOT;

      String modelFilePath = p.removeFirstSegments(1).toString();

      IContainer container = project.getFolder(path);

      IFolder diagramFolder = container.getFolder(null);
      IFile fileTmp = diagramFolder.getFile(modelTestNameExt);
      if (fileTmp.exists()) {
        int i = 1;
        do {
          modelTestName = modelFileNameExt.substring(0,
              modelFileNameExt
                  .lastIndexOf(CreateDiagramWizard.DIAGRAM_EXTENSION_DOT))
              + "Test_" + i;
          modelTestNameExt = modelTestName
              + CreateDiagramWizard.DIAGRAM_EXTENSION_DOT;
          fileTmp = diagramFolder.getFile(modelTestNameExt);
          i++;
        } while (fileTmp.exists());
      }
      final IFile diagramFile = fileTmp;

      String modelerVersion = ProjectProps.getInstance(project)
          .get(ProjectProps.RUMINAQ_VERSION);

      InputStream is = this.getClass().getResourceAsStream("TestTask.template");
      String diagramContent = new BufferedReader(
          new InputStreamReader(is, StandardCharsets.UTF_8)).lines()
              .collect(Collectors.joining("\n"))
              .replace("idTestedTaskToFill", modelFileName)
              .replace("implementationPathFill", modelFilePath)
              .replace("versionToFill", modelerVersion);
      diagramFile.create(new ByteArrayInputStream(diagramContent.getBytes()),
          IResource.FORCE, new NullProgressMonitor());

      Display.getCurrent().asyncExec(() -> {
        IWorkbenchPage page = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getActivePage();
        Try.check(() -> IDE.openEditor(page, diagramFile, true));
      });
    } catch (Exception e) {

    }
  }
}
