package org.ruminaq.tasks.pythontask.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.python.pydev.core.log.Log;
import org.python.pydev.core.preferences.FileTypesPreferences;
import org.python.pydev.editor.PyEdit;
import org.python.pydev.ui.wizards.files.AbstractPythonWizardPage;
import org.python.pydev.ui.wizards.files.PythonModuleWizard;
import org.python.pydev.ui.wizards.files.PythonPackageWizard;
import org.ruminaq.tasks.TasksManagerHandlerImpl;
import org.ruminaq.tasks.pythontask.ui.IPythonTaskUiApi;
import org.ruminaq.eclipse.usertask.model.userdefined.Module;

public class CreatePythonTaskWizard extends PythonModuleWizard {

  public static final String ID = "org.ruminaq.tasks.pythontask.ui.wizards.CreatePythonTaskWizard";

  private IPythonTaskUiApi ictua;
  private ICreatePythonTaskPage cptp;

  private CreatePythonTaskListener listener = null;

  public void setListener(CreatePythonTaskListener listener) {
    this.listener = listener;
  }

  @Override
  public void addPages() {
    filePage = createPathPage();
    cptp = ictua.getCreatePythonTaskPage();
    addPage(filePage);
    addPage(cptp);
  }

  @Override
  protected AbstractPythonWizardPage createPathPage() {
    return new CustomNewClassWizardPage(selection);
  }

  @Override
  protected IFile doCreateNew(IProgressMonitor monitor) throws CoreException {
    IContainer validatedSourceFolder = filePage.getValidatedSourceFolder();
    if (validatedSourceFolder == null) {
      return null;
    }
    IContainer validatedPackage = filePage.getValidatedPackage();
    if (validatedPackage == null) {
      String packageText = filePage.getPackageText();
      if (packageText == null) {
        Log.log("Package text not available");
        return null;
      }
      IFile packageInit = PythonPackageWizard.createPackage(monitor,
          validatedSourceFolder, packageText);
      if (packageInit == null) {
        Log.log("Package not created");
        return null;
      }
      validatedPackage = packageInit.getParent();
    }
    String validatedName = filePage.getValidatedName()
        + FileTypesPreferences.getDefaultDottedPythonExtension();

    IFile file = validatedPackage.getFile(new Path(validatedName));
    if (file.exists()) {
      Log.log("Module already exists.");
      return null;
    }
    file.create(new ByteArrayInputStream(new byte[0]), true, monitor);
    return file;
  }

  @Override
  protected void afterEditorCreated(final IEditorPart openEditor) {
    if (!(openEditor instanceof PyEdit))
      return;
    PyEdit pyEdit = (PyEdit) openEditor;
    if (pyEdit.isDisposed())
      return;

    Display.getDefault().syncExec(new Runnable() {
      @Override
      public void run() {
        PyEdit pyEdit = (PyEdit) openEditor;
        if (pyEdit.isDisposed())
          return;
        Module module = cptp.getModel();
        module.setName(filePage.getValidatedName());
        String code = cptp.generate(module);
        try {
          pyEdit.getIFile().setContents(IOUtils.toInputStream(code, "UTF-8"),
              IResource.FORCE, new NullProgressMonitor());
        } catch (CoreException | IOException e) {
        }
      }
    });
  }

  @Override
  public boolean performFinish() {
    boolean ret = super.performFinish();
    String pack = filePage.getValidatedPackage() != null
        ? filePage.getValidatedPackage().toString()
            .replace(filePage.getValidatedSourceFolder().toString(), "")
        : filePage.getPackageText() != null ? filePage.getPackageText() : "";
    if (pack.startsWith("/"))
      pack = pack.substring(1);
    pack = pack.replace("/", ".");
    if (!"".equals(pack))
      pack += ".";
    if (listener != null)
      listener.created(pack + filePage.getValidatedName());
    return ret;
  }
}
