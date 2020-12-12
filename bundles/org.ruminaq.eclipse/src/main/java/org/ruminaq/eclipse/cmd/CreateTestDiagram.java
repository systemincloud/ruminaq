/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.cmd;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.util.Try;
import org.slf4j.Logger;

/**
 * Create diagram with EmbeddedTask in it. InternalInput and InternalOutput
 * ports of EmbeddedTask corresponds to Input and Output ports of original
 * diagram.
 *
 * @author Marek Jagielski
 */
public final class CreateTestDiagram {

  private static final Logger LOGGER = ModelerLoggerFactory
      .getLogger(CreateTestDiagram.class);

  private static final int SEGMENTS_TO_DIAGRAMS = 1
      + CreateSourceFolders.DIAGRAM_FOLDER.split("/").length;

  /**
   * New diagram with EmbeddedTask wrapping given diagram.
   *
   * @param file eclipse resource of diagram
   */
  public void generateTestDiagram(IResource file) {
    IProject project = file.getProject();

    IPath p = file.getFullPath();
    String dirctoryPath = CreateSourceFolders.TEST_DIAGRAM_FOLDER
        + CreateSourceFolders.DELIMITER
        + Stream.of(p.segments()).skip(SEGMENTS_TO_DIAGRAMS)
            .takeWhile(
                s -> !s.endsWith(CreateDiagramWizard.DIAGRAM_EXTENSION_DOT))
            .collect(Collectors.joining("/"));
    EclipseUtil.createFolderWithParents(project, dirctoryPath);
    String modelFileNameExt = p.segment(p.segmentCount() - 1);
    String modelFileName = modelFileNameExt.substring(0, modelFileNameExt
        .lastIndexOf(CreateDiagramWizard.DIAGRAM_EXTENSION_DOT));
    String modelTestName = modelFileName + "Test";
    String modelTestNameExt = modelTestName
        + CreateDiagramWizard.DIAGRAM_EXTENSION_DOT;
    String modelFilePath = p.removeFirstSegments(1).toString();

    IContainer container = project.getFolder(dirctoryPath);

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

    try (BufferedReader br = new BufferedReader(new InputStreamReader(
        this.getClass().getResourceAsStream("TestTask.template"),
        StandardCharsets.UTF_8))) {
      String diagramContent = br.lines().collect(Collectors.joining("\n"))
          .replace("idTestedTaskToFill", modelFileName)
          .replace("implementationPathFill", modelFilePath)
          .replace("versionToFill", modelerVersion);
      Try.check(() -> diagramFile.create(
          new ByteArrayInputStream(
              diagramContent.getBytes(StandardCharsets.UTF_8)),
          IResource.FORCE, new NullProgressMonitor()));
    } catch (IOException e) {
      LOGGER.error("Can't create test diagram", e);
    }

    Display.getCurrent().asyncExec(() -> {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
          .getActivePage();
      Try.check(() -> IDE.openEditor(page, diagramFile, true));
    });
  }
}
