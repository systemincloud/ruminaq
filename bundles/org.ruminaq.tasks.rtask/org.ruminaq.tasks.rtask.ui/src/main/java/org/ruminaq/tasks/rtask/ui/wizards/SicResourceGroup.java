/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.rtask.ui.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.ui.wizards.NewElementWizardPage;
//import org.eclipse.ecommons.ui.dialogs.groups.Layouter;
//import org.eclipse.ecommons.ui.workbench.ContainerSelectionComposite;
import org.eclipse.jface.viewers.IStructuredSelection;

public class SicResourceGroup { // extends ResourceGroup {

  private IStructuredSelection fResourceSelection;
  private IProject project;

  public SicResourceGroup(NewElementWizardPage newElementWizardPage,
      String defaultResourceNameExtension, IStructuredSelection selection,
      IProject project) {
//        newElementWizardPage.super(defaultResourceNameExtension);
    this.fResourceSelection = selection;
    this.project = project;
  }

//    @Override
//    public void createGroup(final Layouter parentLayouter) {
//        super.createGroup(parentLayouter);
//
//        ContainerSelectionComposite fContainerGroup = null;
//
//        try {
//            Field ffContainerGroup = ResourceGroup.class.getDeclaredField("fContainerGroup"); ffContainerGroup.setAccessible(true);
//            fContainerGroup = (ContainerSelectionComposite) ffContainerGroup.get(this);
//        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
//            e.printStackTrace();
//        }
//
//        fContainerGroup.setToggleFilter(new ContainerSelectionComposite.ContainerFilter() {
//
//            @Override
//            public boolean select(final IContainer container) {
//                final IProject project = container.getProject();
//                try {
//                    if(project.hasNature(StatetProject.NATURE_ID) &&
//                       project.hasNature(RuminaqProjectNature.ID) &&
//                       SicResourceGroup.this.project != null ? SicResourceGroup.this.project == project : true) {
//                        if(container instanceof IProject) return true;
//                        if(container instanceof IFolder) {
//                            IFolder folder = (IFolder) container;
//                            String[] tmp = folder.getFullPath().segments();
//                            String path = StringUtils.join(Arrays.copyOfRange(tmp, 1, tmp.length), "/") + "/";
//                            if((Constants.MAIN_R + "/").startsWith(path) ||
//                               (Constants.TEST_R + "/").startsWith(path) ||
//                               path.startsWith(Constants.MAIN_R + "/") ||
//                               path.startsWith(Constants.TEST_R + "/")) return true;
//                        }
//                    }
//
//                } catch(final CoreException e) { }
//
//                return false;
//            }}, true);
//    }
//
//    @Override
//    protected void initFields() {
//        IPath path = null;
//
//        IPath fContainerFullPath = null;
//        ContainerSelectionComposite fContainerGroup = null;
//        String fResourceName = null;
//        Text fResourceNameControl = null;
//
//        try {
//            Field ffContainerFullPath = ResourceGroup.class.getDeclaredField("fContainerFullPath"); ffContainerFullPath.setAccessible(true);
//            fContainerFullPath = (IPath) ffContainerFullPath.get(this);
//
//            Field ffContainerGroup = ResourceGroup.class.getDeclaredField("fContainerGroup"); ffContainerGroup.setAccessible(true);
//            fContainerGroup = (ContainerSelectionComposite) ffContainerGroup.get(this);
//
//            Field ffResourceName = ResourceGroup.class.getDeclaredField("fResourceName"); ffResourceName.setAccessible(true);
//            fResourceName = (String) ffResourceName.get(this);
//
//            Field ffResourceNameControl = ResourceGroup.class.getDeclaredField("fResourceNameControl"); ffResourceNameControl.setAccessible(true);
//            fResourceNameControl = (Text) ffResourceNameControl.get(this);
//        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
//            e.printStackTrace();
//        }
//
//        if(fContainerFullPath != null) path = fContainerFullPath;
//        else {
//            final Iterator<?> it = fResourceSelection.iterator();
//            if(it.hasNext()) {
//                final Object object = it.next();
//                IResource selectedResource = null;
//                if      (object instanceof IResource)  selectedResource = (IResource) object;
//                else if (object instanceof IAdaptable) selectedResource = ((IAdaptable) object).getAdapter(IResource.class);
//
//                if (selectedResource != null) {
//                    if(selectedResource.getType() == IResource.FILE) selectedResource = selectedResource.getParent();
//                    if(selectedResource.isAccessible())              path             = selectedResource.getFullPath();
//                }
//            }
//        }
//        if(path != null)          fContainerGroup.selectContainer(path);
//        if(fResourceName != null) fResourceNameControl.setText(fResourceName);
//    }
}
