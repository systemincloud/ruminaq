/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.builder;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ruminaq.validation.ProjectValidator;

/**
 *
 * @author Marek Jagielski
 */
public class RuminaqBuilder extends IncrementalProjectBuilder {

  public static final String ID = "org.ruminaq.eclipse.ruminaqBuilder";

  class RuminaqDeltaVisitor implements IResourceDeltaVisitor {
    IProgressMonitor monitor;

    public RuminaqDeltaVisitor(IProgressMonitor monitor) {
      this.monitor = monitor;
    }

    @Override
    public boolean visit(IResourceDelta delta) throws CoreException {
      switch (delta.getKind()) {
        case IResourceDelta.ADDED:
          validate(delta, monitor);
          break;
        case IResourceDelta.REMOVED:
          break;
        case IResourceDelta.CHANGED:
          validate(delta, monitor);
          break;
        default:
          break;
      }
      return true;
    }
  }

  class RuminaqResourceVisitor implements IResourceVisitor {
    IProgressMonitor monitor;

    public RuminaqResourceVisitor(IProgressMonitor monitor) {
      this.monitor = monitor;
    }

    @Override
    public boolean visit(IResource resource) {
      validate(resource, monitor);
      return true;
    }
  }

  @Override
  @SuppressWarnings("rawtypes")
  protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
      throws CoreException {
    if (kind == FULL_BUILD)
      fullBuild(monitor);
    else {
      IResourceDelta delta = getDelta(getProject());
      if (delta == null)
        fullBuild(monitor);
      else
        incrementalBuild(delta, monitor);
    }
    return null;
  }

  protected void incrementalBuild(IResourceDelta delta,
      IProgressMonitor monitor) throws CoreException {
    delta.accept(new RuminaqDeltaVisitor(monitor));
  }

  void validate(IResourceDelta delta, IProgressMonitor monitor) {
    ProjectValidator.validate(delta, monitor);
  }

  void validate(IResource resource, IProgressMonitor monitor) {
    ProjectValidator.validate(resource, monitor);
  }

  protected void fullBuild(final IProgressMonitor monitor)
      throws CoreException {
    try {
      getProject().accept(new RuminaqResourceVisitor(monitor));
    } catch (CoreException e) {
    }
  }

}
