package org.ruminaq.tasks.rtask.ui.debug;

import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.ruminaq.debug.model.vars.SicVariable;
import org.ruminaq.tasks.rtask.ui.Activator;

public class RTasksDebugModelPresentation implements IDebugModelPresentation {

    public static final String ID = "org.ruminaq.tasks.javatask.ui.debug.JavaTasksDebugModelPresentation";

    private static final Image TARGET_TERMINATED       = Activator.getImage("target_terminated.png");
    private static final Image TARGET_PARTLY_SUSPENDED = Activator.getImage("target_partly_suspended.png");
    private static final Image TARGET_RUNNING          = Activator.getImage("target_running.png");
    private static final Image JAVATASK_SUSPENDED      = Activator.getImage("task_suspended.png");
    private static final Image JAVATASK_STEPPING       = Activator.getImage("task_stepping.png");
    private static final Image JAVATASK_RUNNING        = Activator.getImage("task_running.png");

    @Override
    public void addListener(ILabelProviderListener listener) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    @Override
    public void removeListener(ILabelProviderListener listener) {
    }

    @Override
    public IEditorInput getEditorInput(Object element) {
        return null;
    }

    @Override
    public String getEditorId(IEditorInput input, Object element) {
        return null;
    }

    @Override
    public void setAttribute(String attribute, Object value) {
    }

    @Override
    public Image getImage(Object element) {
        if(element instanceof RTasksDebugTarget) {
            RTasksDebugTarget target = (RTasksDebugTarget) element;
            if(target.isTerminated()) return TARGET_TERMINATED;
            if(target.hasSuspended()) return TARGET_PARTLY_SUSPENDED;
            return TARGET_RUNNING;
        }
        else if(element instanceof RTask) {
            RTask task = (RTask) element;
            if(task.isSuspended()) return JAVATASK_SUSPENDED;
            if(task.isStepping())  return JAVATASK_STEPPING;
            return JAVATASK_RUNNING;
        }
        return null;
    }

    @Override
    public String getText(Object element) {
        return null;
    }

    @Override
    public void computeDetail(IValue value, IValueDetailListener listener) {
        if(value instanceof SicVariable)
            listener.detailComputed(value, ((SicVariable) value).getDetailText());
    }
}
