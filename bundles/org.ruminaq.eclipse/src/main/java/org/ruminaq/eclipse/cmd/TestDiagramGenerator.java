/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.cmd;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Predicate;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.osgi.framework.FrameworkUtil;
import org.ruminaq.eclipse.EclipseUtil;
import org.ruminaq.eclipse.prefs.ProjectProps;
import org.ruminaq.eclipse.wizards.diagram.CreateDiagramWizard;
import org.ruminaq.eclipse.wizards.project.CreateSourceFolders;

/**
 * 
 * @author Marek Jagielski
 */
public class TestDiagramGenerator {

  public void generateTestDiagram(IResource file) {
    IProject project = file.getProject();
    Optional.of(project)
        .map(p -> p.getFolder(CreateSourceFolders.TEST_DIAGRAM_FOLDER))
        .filter(Predicate.not(IFolder::exists))
        .ifPresent(f -> EclipseUtil.createFolderWithParents(project,
            CreateSourceFolders.TEST_DIAGRAM_FOLDER));

    IPath p = file.getFullPath();
    try {
      String path = getTestPath(p, project);

      String modelFileNameExt = p.segment(p.segmentCount() - 1);
      String modelFileName = modelFileNameExt.substring(0, modelFileNameExt
          .lastIndexOf(CreateDiagramWizard.DIAGRAM_EXTENSION_DOT));
      String modelTestName = modelFileName + "Test";
      String modelTestNameExt = modelTestName
          + CreateDiagramWizard.DIAGRAM_EXTENSION_DOT;

      String modelFilePath = p.removeFirstSegments(1).toString();

      IContainer container = project.getFolder(path);
      Diagram diagram = Graphiti.getPeCreateService().createDiagram("Ruminaq",
          modelTestNameExt, -1, false);

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

      String symbolicName = FrameworkUtil.getBundle(getClass())
          .getSymbolicName();
      String modelerVersion = ProjectProps.getInstance(project)
          .get(ProjectProps.RUMINAQ_VERSION);
      String versionToFill = "";

      InputStream is = this.getClass().getResourceAsStream("TestTask.template");
      String diagramContent = IOUtils.toString(is, "UTF-8")
          .replaceAll("nameTestTaskToFill", modelTestName)
          .replaceAll("nameTestedTaskToFill", modelFileName + ".sic")
          .replaceAll("idTestedTaskToFill", modelFileName)
          .replaceAll("pathTestedTaskToFill", modelFilePath)
          .replaceAll("modelerVersionFill", modelerVersion)
          .replaceAll("versionToFill", versionToFill);
      diagramFile.create(new ByteArrayInputStream(diagramContent.getBytes()),
          IResource.FORCE, new NullProgressMonitor());

      Display.getCurrent().asyncExec(new Runnable() {
        @Override
        public void run() {
          IWorkbenchPage page = PlatformUI.getWorkbench()
              .getActiveWorkbenchWindow().getActivePage();
          try {
            IDE.openEditor(page, diagramFile, true);
          } catch (PartInitException e) {
          }
        }
      });
    } catch (Exception e) {

    }
  }

  private String getTestPath(IPath p, IProject project) throws CoreException {
    String path = CreateSourceFolders.TEST_DIAGRAM_FOLDER;
    for (int i = 0; i < p.segmentCount() - 1; i++) {
      if (i < 5)
        continue;
      int j = 4;
      path = CreateSourceFolders.TEST_DIAGRAM_FOLDER;
      while (j++ < i) {
        path += "/" + p.segment(j);
        if (!project.getFolder(path).exists())
          project.getFolder(path).create(true, true, new NullProgressMonitor());
      }
    }
    return path;
  }

  public Integer versionCompare(String str1, String str2) {
    String[] vals1 = str1.split("\\.");
    String[] vals2 = str2.split("\\.");
    int i = 0;
    while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i]))
      i++;
    if (i < vals1.length && i < vals2.length) {
      int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
      return Integer.signum(diff);
    } else
      return Integer.signum(vals1.length - vals2.length);
  }
}
