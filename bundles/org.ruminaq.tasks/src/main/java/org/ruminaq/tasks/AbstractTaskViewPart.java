/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.osgi.service.component.annotations.Reference;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.tasks.api.ITaskUiApi;
import org.ruminaq.tasks.api.IView;
import org.ruminaq.tasks.api.TasksUiManagerHandler;

public abstract class AbstractTaskViewPart extends ViewPart {

  @Reference
  private TasksUiManagerHandler tasks;

  protected IView view;
  private Composite parent;

  public void init(EObject bo, Class<? extends ViewPart> viewClass,
      TransactionalEditingDomain ed) {
    if (parent.getChildren().length > 0)
      return;
    if (bo instanceof Task) {
      Task task = (Task) bo;
      for (ITaskUiApi t : tasks.getTasks(getPrefix())) {
//	        	if(t.getSymbolicName().equals(task.getBundleName())
//	    	    && TaskProvider.compare(t.getVersion(), Version.parseVersion(task.getVersion()))) {
        view = t.createView(viewClass);
        setPartName(task.getId());
        view.createPartControl(parent, getSite().getShell());
        view.init(bo, ed);
//	        	}
      }
    }
    parent.layout();
  }

  protected abstract String getPrefix();

  @Override
  public void createPartControl(Composite parent) {
    this.parent = parent;
  }

  @Override
  public void setFocus() {
    if (view != null) {
      view.setFocus();
    }
  }

  @Override
  public void dispose() {
    if (view != null)
      view.dispose();
    super.dispose();
  }

}
