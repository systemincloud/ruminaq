package org.ruminaq.tasks.embeddedtask.ui.cmd;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

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
import org.eclipse.graphiti.mm.pictograms.PictogramLink;
import org.eclipse.graphiti.mm.pictograms.PictogramsFactory;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Reference;
import org.ruminaq.eclipse.wizards.diagram.CreateDiagramWizard;
import org.ruminaq.eclipse.wizards.project.SourceFolders;
import org.ruminaq.prefs.ProjectProps;
import org.ruminaq.tasks.api.ITaskUiApi;
import org.ruminaq.tasks.api.TasksUiManagerHandler;
import org.ruminaq.tasks.embeddedtask.ui.IEmbeddedTaskUiApi;
import org.ruminaq.util.EclipseUtil;

public class TestDiagramGenerator {

  @Reference
  private TasksUiManagerHandler tasks;

  public void generateTestDiagram(IResource file) throws Exception {
    IProject project = file.getProject();
    IFolder sourceFolder = project.getFolder(SourceFolders.TEST_DIAGRAM_FOLDER);
    if (!sourceFolder.exists())
      EclipseUtil.createFolderWithParents(project,
          SourceFolders.TEST_DIAGRAM_FOLDER);

    IPath p = file.getFullPath();
    String path = getTestPath(p, project);

    String modelFileNameExt = p.segment(p.segmentCount() - 1);
    String modelFileName = modelFileNameExt.substring(0,
        modelFileNameExt.lastIndexOf(CreateDiagramWizard.DIAGRAM_EXTENSION_DOT));
    String modelTestName = modelFileName + "Test";
    String modelTestNameExt = modelTestName + CreateDiagramWizard.DIAGRAM_EXTENSION_DOT;

    String modelFilePath = p.removeFirstSegments(1).toString();

    IContainer container = project.getFolder(path);
    Diagram diagram = Graphiti.getPeCreateService().createDiagram("Ruminaq",
        modelTestNameExt, -1, false);

    PictogramLink pl = PictogramsFactory.eINSTANCE.createPictogramLink();
    pl.setPictogramElement(diagram);
    diagram.getPictogramLinks().add(pl);

    IFolder diagramFolder = container.getFolder(null);
    IFile fileTmp = diagramFolder.getFile(modelTestNameExt);
    if (fileTmp.exists()) {
      int i = 1;
      do {
        modelTestName = modelFileNameExt.substring(0,
            modelFileNameExt.lastIndexOf(CreateDiagramWizard.DIAGRAM_EXTENSION_DOT))
            + "Test_" + i;
        modelTestNameExt = modelTestName + CreateDiagramWizard.DIAGRAM_EXTENSION_DOT;
        fileTmp = diagramFolder.getFile(modelTestNameExt);
        i++;
      } while (fileTmp.exists());
    }
    final IFile diagramFile = fileTmp;

    String symbolicName = FrameworkUtil.getBundle(getClass()).getSymbolicName();
    List<ITaskUiApi> ts = tasks.getTasks(
        symbolicName.substring(0, symbolicName.length() - ".ui".length()));
    String modelerVersion = ProjectProps.getInstance(project)
        .get(ProjectProps.MODELER_VERSION);
    String versionToFill = "";
    String maxModelerVer = "0.0.0";
    for (ITaskUiApi t : ts) {
      String tmp = ((IEmbeddedTaskUiApi) t).getModelerVersion();
      if (versionCompare(tmp, maxModelerVer) > 0)
        maxModelerVer = tmp;
    }
    for (ITaskUiApi t : ts) {
      if (maxModelerVer.equals(((IEmbeddedTaskUiApi) t).getModelerVersion())) {
        versionToFill = t.getVersion().getMajor() + "."
            + t.getVersion().getMinor() + "." + t.getVersion().getMicro();
      }

    }

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

  }

  private String getTestPath(IPath p, IProject project) throws CoreException {
    String path = SourceFolders.TEST_DIAGRAM_FOLDER;
    for (int i = 0; i < p.segmentCount() - 1; i++) {
      if (i < 5)
        continue;
      int j = 4;
      path = SourceFolders.TEST_DIAGRAM_FOLDER;
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
