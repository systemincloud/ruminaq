/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.rtask.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.ruminaq.tasks.rtask.ui.IRTaskUiApi;

@SuppressWarnings("restriction")
public class CreateRTaskWizard extends NewElementWizard {

  public static final String ID = "org.ruminaq.tasks.rtask.ui.wizards.CreateRTaskWizard";

  private IRTaskUiApi irtua;
  private CustomNewClassWizardPage cncwp;
  private ICreateRTaskPage crtp;

  private CreateRTaskListener listener = null;

  public void setListener(CreateRTaskListener listener) {
    this.listener = listener;
  }

//    private NewRTaskCreator rtaskFile;

  public CreateRTaskWizard() {
//        setDialogSettings(DialogUtil.getDialogSettings(RUIPlugin.getDefault(), "NewElementWizard"));
//        setDefaultPageImageDescriptor(RUI.getImageDescriptor(RUIPlugin.IMG_WIZBAN_NEWRFILE));
  }

  @Override
  public void addPages() {
    super.addPages();
//        this.cncwp = new CustomNewClassWizardPage(getSelection());
//        this.crtp  = irtua.getCreatePythonTaskPage();
//        addPage(this.cncwp);
//        addPage(this.crtp);
  }

  @Override
  public boolean performFinish() {
//        this.rtaskFile = new NewRTaskCreator(this.cncwp.fResourceGroup.getContainerFullPath(),
//                                             this.cncwp.fResourceGroup.getResourceName());

    final boolean result = super.performFinish();
//
//        if(result && this.rtaskFile.getFileHandle() != null) {
//            selectAndReveal(this.rtaskFile.getFileHandle());
//            openResource(this.rtaskFile);
//        }
//
//        if(listener != null) listener.created(this.cncwp.fResourceGroup.getContainerFullPath() + "/" +
//                                              this.cncwp.fResourceGroup.getResourceName());

    return result;
  }

  @Override
  protected ISchedulingRule getSchedulingRule() {
//        final ISchedulingRule rule = createRule(rtaskFile.getFileHandle());
//        if(rule != null) return rule;
    return super.getSchedulingRule();
  }

  protected void doFinish(final IProgressMonitor monitor)
      throws InterruptedException, CoreException, InvocationTargetException {
    try {
      monitor.beginTask("Create new RTask ...", 1000);
//            rtaskFile.createFile(new NullProgressMonitor());
      this.cncwp.saveSettings();
      monitor.worked(100);
    } finally {
      monitor.done();
    }
  }

  @Override
  protected void finishPage(IProgressMonitor arg0)
      throws InterruptedException, CoreException {
    // TODO Auto-generated method stub

  }

  @Override
  public IJavaElement getCreatedElement() {
    // TODO Auto-generated method stub
    return null;
  }

//
//    private class NewRTaskCreator extends NewFileCreator {
//
//        public NewRTaskCreator(final IPath containerPath, final String resourceName) {
//            super(containerPath, resourceName, RCore.R_CONTENT_TYPE);
//        }
//
//        public void createFile(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException, CoreException {
//            Display.getDefault().syncExec(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        NewRTaskCreator.super.createFile(monitor);
//                    } catch (InvocationTargetException | InterruptedException | CoreException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        }
//
//        @Override
//        protected String getInitialFileContent(final IFile newFileHandle, final SubMonitor m) {
//            final IRSourceUnit su = (IRSourceUnit) LTK.getSourceUnitManager().getSourceUnit(LTK.PERSISTENCE_CONTEXT, newFileHandle, getContentType(newFileHandle), true, m );
//            try {
//                Module module = crtp.getModel();
//                String fileName = CreateRTaskWizard.this.cncwp.fResourceGroup.getResourceName();
//                module.setName(fileName.substring(0, fileName.length() - 2));
//                return crtp.generate(module);
//            } catch(Exception e) {
//                e.printStackTrace();
//            } finally {
//                if(su != null) su.disconnect(m);
//            }
//            return fResourceName;
//        }
//    }
}
