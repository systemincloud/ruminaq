package org.ruminaq.tasks.rtask.ui.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.IStructuredSelection;

import de.walware.ecommons.ui.dialogs.groups.Layouter;
import de.walware.statet.ext.ui.wizards.NewElementWizardPage;

public class CustomNewClassWizardPage extends NewElementWizardPage {

    public static final String extension = ".R";

    ResourceGroup fResourceGroup;

    private IProject project;

    public CustomNewClassWizardPage(IStructuredSelection selection) {
        super("CustomNewClassWizardPage", selection);
        setTitle("System in Cloud - R Task");
        setDescription("Here you define R class");

        if     (selection.getFirstElement() instanceof IResource)    project = ((IResource) selection.getFirstElement()).getProject();
        else if(selection.getFirstElement() instanceof IJavaElement) project = ((IJavaElement) selection.getFirstElement()).getJavaProject().getProject();

        fResourceGroup = new SicResourceGroup(this, extension, selection, project);
    }

    @Override
    protected void createContents(final Layouter layouter) {
        fResourceGroup.createGroup(layouter);
    }

    @Override
    public void setVisible(final boolean visible) {
        super.setVisible(visible);
        if(visible) fResourceGroup.setFocus();
    }

    public void saveSettings() {
        fResourceGroup.saveSettings();
    }

    @Override
    protected void validatePage() {
        updateStatus(fResourceGroup.validate());
    }
}
