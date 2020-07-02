/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.embeddedtask.features;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.ruminaq.model.ruminaq.EmbeddedTask;
import org.ruminaq.util.EclipseUtil;

public class DoubleClickFeature extends AbstractCustomFeature {

  public DoubleClickFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canExecute(ICustomContext context) {
    return true;
  }

  @Override
  public boolean hasDoneChanges() {
    return false;
  }

  @Override
  public void execute(ICustomContext context) {
    EmbeddedTask bo = null;
    for (Object o : Graphiti.getLinkService()
        .getAllBusinessObjectsForLinkedPictogramElement(
            context.getInnerPictogramElement()))
      if (o instanceof EmbeddedTask) {
        bo = (EmbeddedTask) o;
        break;
      }
    if (bo == null)
      return;
    if (bo.getImplementationTask().equals(""))
      return;

    final IFile ifile = ResourcesPlugin.getWorkspace().getRoot()
        .getFile(new Path(getEmbeddedTaskPath(bo)));

    Display.getCurrent().asyncExec(new Runnable() {
      public void run() {
        IWorkbenchPage page = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getActivePage();
        try {
          IDE.openEditor(page, ifile, true);
        } catch (PartInitException e) {
        }
      }
    });
  }

  public String getEmbeddedTaskPath(EmbeddedTask bo) {
    return EclipseUtil.getModelPathFromEObject(bo).segment(0).toString() + "/"
        + bo.getImplementationTask();
  }
}
