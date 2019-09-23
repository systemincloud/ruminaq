package org.ruminaq.tasks.pythontask.ui.wizards;

import java.lang.reflect.Field;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.python.pydev.ui.dialogs.PythonPackageSelectionDialog;
import org.python.pydev.ui.dialogs.SourceFolder;
import org.python.pydev.ui.wizards.files.AbstractPythonWizardPage;

public class CustomNewClassWizardPage extends AbstractPythonWizardPage {

  private IProject project = null;

  private Button btBrowseSourceFolder = null;
  private Text textSourceFolder = null;
  private Button btBrowsePackage = null;
  private Text textPackage = null;

  public CustomNewClassWizardPage(IStructuredSelection selection) {
    super("", selection);

    if (selection.getFirstElement() instanceof IResource)
      project = ((IResource) selection.getFirstElement()).getProject();
    else if (selection.getFirstElement() instanceof IJavaElement)
      project = ((IJavaElement) selection.getFirstElement()).getJavaProject()
          .getProject();

    setTitle("System in Cloud - Python Task");
    setDescription("Here you define Python class");
  }

  @Override
  public void createControl(Composite parent) {
    super.createControl(parent);

    try {
      Field btBrowseSourceFolderField = AbstractPythonWizardPage.class
          .getDeclaredField("btBrowseSourceFolder");
      btBrowseSourceFolderField.setAccessible(true);
      btBrowseSourceFolder = (Button) btBrowseSourceFolderField.get(this);
      Field textSourceFolderField = AbstractPythonWizardPage.class
          .getDeclaredField("textSourceFolder");
      textSourceFolderField.setAccessible(true);
      textSourceFolder = (Text) textSourceFolderField.get(this);
    } catch (NoSuchFieldException | SecurityException | IllegalArgumentException
        | IllegalAccessException e2) {
    }

    if (btBrowseSourceFolder == null || textSourceFolder == null)
      return;

    btBrowseSourceFolder.removeListener(SWT.Selection,
        btBrowseSourceFolder.getListeners(SWT.Selection)[0]);

    btBrowseSourceFolder.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        try {
          PythonPackageSelectionDialog dialog = new PythonPackageSelectionDialog(
              getShell(), true);
          dialog.setTitle("Source folder selection");
          dialog.setMessage("Select a source folder.");

          try {
            Field fContentProviderField = ElementTreeSelectionDialog.class
                .getDeclaredField("fContentProvider");
            fContentProviderField.setAccessible(true);
            fContentProviderField.set(dialog,
                new CustomPackageContentProvider(project, true));
          } catch (NoSuchFieldException | SecurityException
              | IllegalArgumentException | IllegalAccessException e2) {
          }

          dialog.open();
          Object firstResult = dialog.getFirstResult();
          if (firstResult instanceof SourceFolder) {
            SourceFolder f = (SourceFolder) firstResult;
            textSourceFolder.setText(f.folder.getFullPath().toString());
          }
        } catch (Exception e1) {
        }
      }
    });

    try {
      Field btBrowsePackageField = AbstractPythonWizardPage.class
          .getDeclaredField("btBrowsePackage");
      btBrowsePackageField.setAccessible(true);
      btBrowsePackage = (Button) btBrowsePackageField.get(this);
      Field textPackageField = AbstractPythonWizardPage.class
          .getDeclaredField("textPackage");
      textPackageField.setAccessible(true);
      textPackage = (Text) textPackageField.get(this);
    } catch (NoSuchFieldException | SecurityException | IllegalArgumentException
        | IllegalAccessException e2) {
    }

    if (btBrowsePackage == null || textPackage == null)
      return;

    btBrowsePackage.removeListener(SWT.Selection,
        btBrowsePackage.getListeners(SWT.Selection)[0]);

    btBrowsePackage.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        try {
          PythonPackageSelectionDialog dialog = new PythonPackageSelectionDialog(
              getShell(), false);
          dialog.setTitle("Package selection");
          dialog.setMessage(
              "Select a package (or a source folder). You may also enter the\nname of a new package in the text bar on the previous page.");

          try {
            Field fContentProviderField = ElementTreeSelectionDialog.class
                .getDeclaredField("fContentProvider");
            fContentProviderField.setAccessible(true);
            fContentProviderField.set(dialog,
                new CustomPackageContentProvider(project, false));
          } catch (NoSuchFieldException | SecurityException
              | IllegalArgumentException | IllegalAccessException e2) {
          }

          dialog.open();
          Object firstResult = dialog.getFirstResult();
          if (firstResult instanceof SourceFolder) { // it is the default
                                                     // package
            SourceFolder f = (SourceFolder) firstResult;
            textPackage.setText("");
            textSourceFolder.setText(f.folder.getFullPath().toString());

          }
          if (firstResult instanceof org.python.pydev.ui.dialogs.Package) {
            org.python.pydev.ui.dialogs.Package f = (org.python.pydev.ui.dialogs.Package) firstResult;
            textPackage.setText(f.getPackageName());
            textSourceFolder
                .setText(f.sourceFolder.folder.getFullPath().toString());
          }
        } catch (Exception e1) {
        }
      }
    });

  }

  @Override
  protected String checkNameText(String text) {
    String result = super.checkNameText(text);
    if (result != null)
      return result;

    IContainer p = getValidatedPackage();
    if (p != null && p.findMember(text.concat(".py")) != null)
      return "The module " + text + " already exists in " + p.getName() + ".";

    return null;
  }

  @Override
  protected boolean shouldCreatePackageSelect() {
    return true;
  }
}
