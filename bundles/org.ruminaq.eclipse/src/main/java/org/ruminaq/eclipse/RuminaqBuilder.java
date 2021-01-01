/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse;

import java.util.Map;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
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

  class RuminaqDeltaVisitor implements IResourceDeltaVisitor {
    private IProgressMonitor monitor;

    public RuminaqDeltaVisitor(IProgressMonitor monitor) {
      this.monitor = monitor;
    }

    @Override
    public boolean visit(IResourceDelta delta) throws CoreException {
      if (delta.getKind() == IResourceDelta.ADDED
          || delta.getKind() == IResourceDelta.CHANGED) {
        ProjectValidator.validate(delta, monitor);
      }
      return true;
    }
  }

  class RuminaqResourceVisitor implements IResourceVisitor {
    private IProgressMonitor monitor;

    public RuminaqResourceVisitor(IProgressMonitor monitor) {
      this.monitor = monitor;
    }

    @Override
    public boolean visit(IResource resource) {
      ProjectValidator.validate(resource, monitor);
      return true;
    }
  }

  @Override
  protected IProject[] build(int kind, Map<String, String> args,
      IProgressMonitor monitor) throws CoreException {
    if (kind == FULL_BUILD) {
      fullBuild(monitor);
    } else {
      IResourceDelta delta = getDelta(getProject());
      if (delta == null) {
        fullBuild(monitor);
      } else {
        incrementalBuild(delta, monitor);
      }
    }
    return new IProject[0];
  }

  protected void incrementalBuild(IResourceDelta delta,
      IProgressMonitor monitor) throws CoreException {
    delta.accept(new RuminaqDeltaVisitor(monitor));
  }

  protected void fullBuild(IProgressMonitor monitor) {
    Try.check(() -> getProject().accept(new RuminaqResourceVisitor(monitor)));
  }
}
