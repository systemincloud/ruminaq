/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.custom;

import java.util.Optional;
import java.util.stream.Stream;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.ruminaq.gui.image.Images;
import org.ruminaq.gui.model.diagram.RuminaqDiagram;
import org.ruminaq.model.ruminaq.InternalInputPort;
import org.ruminaq.model.ruminaq.InternalOutputPort;
import org.ruminaq.model.ruminaq.InternalPort;
import org.ruminaq.model.ruminaq.MainTask;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.tasks.debug.ui.InternalPortBreakpoint;
import org.ruminaq.util.EclipseUtil;

/**
 * Toggle breakpoints.
 *
 * @author Marek Jagielski
 */
public class AllInternalPortToggleBreakpointFeature
    extends AbstractCustomFeature {

  public static final String NAME = "Toggle Breakpoints on all Internal Ports";

  public AllInternalPortToggleBreakpointFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public String getImageId() {
    return Images.IMG_TOGGLE_BREAKPOINT;
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean canExecute(ICustomContext context) {
    return true;
  }

  @Override
  public boolean hasDoneChanges() {
    return false;
  }

  private static Optional<RuminaqDiagram> shapeFromContext(
      ICustomContext context) {
    return Optional.of(context).map(ICustomContext::getPictogramElements)
        .map(Stream::of).orElseGet(Stream::empty).findFirst()
        .filter(RuminaqDiagram.class::isInstance)
        .map(RuminaqDiagram.class::cast);
  }

  private static Optional<MainTask> modelFromContext(ICustomContext context) {
    return shapeFromContext(context).map(RuminaqDiagram::getMainTask);
  }

  @Override
  public void execute(ICustomContext context) {
    IResource resource = EclipseUtil.emfResourceToIResource(
        getFeatureProvider().getDiagramTypeProvider().getDiagram().eResource());
    String path = resource.getRawLocation().toOSString();
    MainTask mt = modelFromContext(context).orElseThrow();

    try {
      for (Task t : mt.getTask()) {
        for (InternalInputPort p : t.getInputPort()) {
          boolean tmp = false;
          IBreakpoint[] breakpoints = DebugPlugin.getDefault()
              .getBreakpointManager().getBreakpoints(InternalPortBreakpoint.ID);
          for (int i = 0; i < breakpoints.length; i++) {
            IBreakpoint breakpoint = breakpoints[i];
            if (resource.equals(breakpoint.getMarker().getResource())) {
              if (breakpoint.getMarker()
                  .getAttribute(Task.class.getSimpleName()).equals(t.getId())
                  && breakpoint.getMarker()
                      .getAttribute(InternalPort.class.getSimpleName())
                      .equals(p.getId())) {
                breakpoint.delete();
                tmp = true;
              }
            }
          }
          if (!tmp) {
            InternalPortBreakpoint internalPortBreakpoint = new InternalPortBreakpoint(
                resource, path, t.getId(), p.getId());
            DebugPlugin.getDefault().getBreakpointManager()
                .addBreakpoint(internalPortBreakpoint);
          }
        }
        for (InternalOutputPort p : t.getOutputPort()) {
          boolean tmp = false;
          IBreakpoint[] breakpoints = DebugPlugin.getDefault()
              .getBreakpointManager().getBreakpoints(InternalPortBreakpoint.ID);
          for (int i = 0; i < breakpoints.length; i++) {
            IBreakpoint breakpoint = breakpoints[i];
            if (resource.equals(breakpoint.getMarker().getResource())) {
              if (breakpoint.getMarker()
                  .getAttribute(Task.class.getSimpleName()).equals(t.getId())
                  && breakpoint.getMarker()
                      .getAttribute(InternalPort.class.getSimpleName())
                      .equals(p.getId())) {
                breakpoint.delete();
                tmp = true;
              }
            }
          }
          if (!tmp) {
            InternalPortBreakpoint internalPortBreakpoint = new InternalPortBreakpoint(
                resource, path, t.getId(), p.getId());
            DebugPlugin.getDefault().getBreakpointManager()
                .addBreakpoint(internalPortBreakpoint);
          }
        }
      }
    } catch (CoreException e) {

    }

  }
}
