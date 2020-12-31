/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.EMFEventType;
import org.eclipse.emf.validation.IValidationContext;
import org.ruminaq.eclipse.EclipseUtil;
import org.ruminaq.model.ruminaq.EmbeddedTask;
import org.ruminaq.model.ruminaq.MainTask;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.util.Result;

/**
 * Check there is no circular dependencies among EmbeddedTasks.
 *
 * @author Marek Jagielski
 */
public class LoopedEmbeddedTaskConstraint extends AbstractModelConstraint {

  @Override
  public IStatus validate(IValidationContext ctx) {
    return Optional.ofNullable(ctx)
        .filter(c -> c.getEventType() == EMFEventType.NULL)
        .map(IValidationContext::getTarget)
        .filter(EmbeddedTask.class::isInstance).map(EmbeddedTask.class::cast)
        .map(et -> validate(ctx, et)).orElseGet(ctx::createSuccessStatus);
  }

  private IStatus validate(IValidationContext ctx, EmbeddedTask task) {
    String path = task.getImplementationPath();
    if (path == null || path.equals(""))
      return ctx.createSuccessStatus();
    URI modelPath = EclipseUtil.getUriOf(task);
    String prefix = "/" + modelPath.segment(0) + "/";
    MainTask embeddedTask = loadTask(modelPath);
    List<String> deph = new ArrayList<>();
    deph.add(URI.createURI(Stream.of(modelPath.segments()).skip(1)
        .collect(Collectors.joining("/"))).toString());
    return Optional.of(ctx).filter(c -> detectLoop(prefix, embeddedTask, deph))
        .map(IValidationContext::createFailureStatus)
        .orElseGet(ctx::createSuccessStatus);
  }

  private boolean detectLoop(String prefix, MainTask mainTask,
      List<String> deph) {
    for (Task t : mainTask.getTask()) {
      if (t instanceof EmbeddedTask) {
        String path = ((EmbeddedTask) t).getImplementationPath();
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

  private static MainTask loadTask(URI uri) {
    return Optional.of(uri)
        .map(u -> Result
            .attempt(() -> new ResourceSetImpl().getResource(u, true)))
        .map(r -> r.orElse(null)).filter(Objects::nonNull)
        .map(Resource::getContents).map(EList::stream).orElseGet(Stream::empty)
        .filter(MainTask.class::isInstance).map(MainTask.class::cast)
        .findFirst().orElse(null);
  }

}
