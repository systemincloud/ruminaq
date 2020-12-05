/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.EMFEventType;
import org.eclipse.emf.validation.IValidationContext;
import org.ruminaq.eclipse.EclipseUtil;
import org.ruminaq.model.ruminaq.EmbeddedTask;
import org.ruminaq.model.ruminaq.MainTask;
import org.ruminaq.model.ruminaq.Task;

public class LoopedEmbeddedTaskConstraint extends AbstractModelConstraint {

  @Override
  public IStatus validate(IValidationContext ctx) {
    EObject eObj = ctx.getTarget();
    if (ctx.getEventType() == EMFEventType.NULL && eObj instanceof EmbeddedTask)
      return validate(ctx, (EmbeddedTask) eObj);
    return ctx.createSuccessStatus();
  }

  private IStatus validate(IValidationContext ctx, EmbeddedTask task) {
    String path = task.getImplementationTask();
    if (path == null || path.equals(""))
      return ctx.createSuccessStatus();
    URI modelPath = EclipseUtil.getUriOf(task);
    String prefix = "/" + modelPath.segment(0) + "/";
    MainTask embeddedTask = loadTask(modelPath);

    List<String> deph = new ArrayList<>();
    deph.add(URI.createURI(Stream.of(modelPath.segments()).skip(1)
        .collect(Collectors.joining("/"))).toString());
    boolean loop = detectLoop(prefix, embeddedTask, deph);

    if (loop)
      return ctx.createFailureStatus();
    else
      return ctx.createSuccessStatus();
  }

  private boolean detectLoop(String prefix, MainTask mainTask,
      List<String> deph) {
    for (Task t : mainTask.getTask()) {
      if (t instanceof EmbeddedTask) {
        String path = ((EmbeddedTask) t).getImplementationTask();
        if (deph.contains(path))
          return true;
        else {
          MainTask embeddedTask = loadTask(URI.createURI(prefix + path));
          if (embeddedTask == null)
            continue;
          deph.add(path);
          boolean loop = detectLoop(prefix, embeddedTask, deph);
          deph.remove(deph.size() - 1);
          if (loop)
            return true;
        }
      }
    }
    return false;
  }

  private MainTask loadTask(URI uri) {
    MainTask mt = null;
    ResourceSet resSet = new ResourceSetImpl();
    Resource resource = null;
    try {
      resource = resSet.getResource(uri, true);
    } catch (Exception e) {
    }
    if (resource == null)
      return null;

    if (resource.getContents().size() > 0)
      mt = (MainTask) resource.getContents().get(1);
    return mt;
  }

}
