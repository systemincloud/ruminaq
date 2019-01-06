package org.ruminaq.tasks.rtask.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.swt.widgets.Display;
import org.ruminaq.tasks.userdefined.model.userdefined.Module;
import org.ruminaq.tasks.TasksManagerHandlerImpl;
import org.ruminaq.tasks.rtask.ui.Activator;
import org.ruminaq.tasks.rtask.ui.IRTaskUiApi;

import de.walware.ecommons.ltk.LTK;
import de.walware.ecommons.ui.util.DialogUtil;
import de.walware.statet.ext.ui.wizards.NewElementWizard;
import de.walware.statet.r.core.RCore;
import de.walware.statet.r.core.model.IRSourceUnit;
import de.walware.statet.r.internal.ui.RUIPlugin;
import de.walware.statet.r.ui.RUI;

@SuppressWarnings("restriction")
public class CreateRTaskWizard extends NewElementWizard {

    public static final String ID = "org.ruminaq.tasks.rtask.ui.wizards.CreateRTaskWizard";

    private IRTaskUiApi              irtua = (IRTaskUiApi) Activator.getDefault().getTasksUiManager().getTask(
    		TasksManagerHandlerImpl.INSTANCE.getProjectVersionForTask(Activator.getDefault().getBundlePrefix()));
    private CustomNewClassWizardPage cncwp;
    private ICreateRTaskPage         crtp;

    private CreateRTaskListener listener = null;

    public void setListener(CreateRTaskListener listener) { this.listener  = listener; }

    private NewRTaskCreator rtaskFile;

    public CreateRTaskWizard() {
        setDialogSettings(DialogUtil.getDialogSettings(RUIPlugin.getDefault(), "NewElementWizard"));
        setDefaultPageImageDescriptor(RUI.getImageDescriptor(RUIPlugin.IMG_WIZBAN_NEWRFILE));
    }

    @Override
    public void addPages() {
        super.addPages();
        this.cncwp = new CustomNewClassWizardPage(getSelection());
        this.crtp  = irtua.getCreatePythonTaskPage();
        addPage(this.cncwp);
        addPage(this.crtp);
    }

    @Override
    public boolean performFinish() {
        this.rtaskFile = new NewRTaskCreator(this.cncwp.fResourceGroup.getContainerFullPath(),
                                             this.cncwp.fResourceGroup.getResourceName());

        final boolean result = super.performFinish();

        if(result && this.rtaskFile.getFileHandle() != null) {
            selectAndReveal(this.rtaskFile.getFileHandle());
            openResource(this.rtaskFile);
        }

        if(listener != null) listener.created(this.cncwp.fResourceGroup.getContainerFullPath() + "/" +
                                              this.cncwp.fResourceGroup.getResourceName());

        return result;
    }

    @Override
    protected ISchedulingRule getSchedulingRule() {
        final ISchedulingRule rule = createRule(rtaskFile.getFileHandle());
        if(rule != null) return rule;
        return super.getSchedulingRule();
    }

    @Override
    protected void doFinish(final IProgressMonitor monitor) throws InterruptedException, CoreException, InvocationTargetException {
        try {
            monitor.beginTask("Create new RTask ...", 1000);
            rtaskFile.createFile(new NullProgressMonitor());
            this.cncwp.saveSettings();
            monitor.worked(100);
        } finally {
            monitor.done();
        }
    }


    private class NewRTaskCreator extends NewFileCreator {

        public NewRTaskCreator(final IPath containerPath, final String resourceName) {
            super(containerPath, resourceName, RCore.R_CONTENT_TYPE);
        }

        public void createFile(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException, CoreException {
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    try {
                        NewRTaskCreator.super.createFile(monitor);
                    } catch (InvocationTargetException | InterruptedException | CoreException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        protected String getInitialFileContent(final IFile newFileHandle, final SubMonitor m) {
            final IRSourceUnit su = (IRSourceUnit) LTK.getSourceUnitManager().getSourceUnit(LTK.PERSISTENCE_CONTEXT, newFileHandle, getContentType(newFileHandle), true, m );
            try {
                Module module = crtp.getModel();
                String fileName = CreateRTaskWizard.this.cncwp.fResourceGroup.getResourceName();
                module.setName(fileName.substring(0, fileName.length() - 2));
                return crtp.generate(module);
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                if(su != null) su.disconnect(m);
            }
            return fResourceName;
        }
    }
}

