/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse;

import java.util.Map;
import java.util.Optional;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ruminaq.util.Try;
import org.ruminaq.validation.ProjectValidator;

/**
 * Ruminaq builder.
 *
 * @author Marek Jagielski
 */
public class RuminaqBuilder extends IncrementalProjectBuilder {

  public static final String ID = "org.ruminaq.eclipse.ruminaqBuilder";

  @Override
  protected IProject[] build(int kind, Map<String, String> args,
      IProgressMonitor monitor) {
    if (kind == FULL_BUILD) {
      fullBuild(monitor);
    } else {
      Optional.ofNullable(getDelta(getProject()))
          .ifPresent(d -> Try.check(() -> incrementalBuild(d, monitor)));
    }
    return new IProject[0];
  }

  protected void incrementalBuild(IResourceDelta delta,
      IProgressMonitor monitor) throws CoreException {
    delta.accept((IResourceDelta d) -> {
      if (d.getKind() == IResourceDelta.ADDED
          || d.getKind() == IResourceDelta.CHANGED) {
        ProjectValidator.validate(d, monitor);
      }
      return true;
    });
  }

  protected void fullBuild(IProgressMonitor monitor) {
    Try.check(() -> getProject().accept((IResource resource) -> {
      ProjectValidator.validate(resource, monitor);
      return true;
    }));
  }
}
