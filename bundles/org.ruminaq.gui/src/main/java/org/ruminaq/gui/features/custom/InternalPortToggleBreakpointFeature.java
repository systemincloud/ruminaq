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
import org.ruminaq.debug.InternalPortBreakpoint;
import org.ruminaq.gui.image.Images;
import org.ruminaq.gui.model.diagram.InternalPortShape;
import org.ruminaq.model.ruminaq.InternalPort;
import org.ruminaq.util.EclipseUtil;

public class InternalPortToggleBreakpointFeature extends AbstractCustomFeature {

  public static final String NAME = "Toggle Breakpoint";

  public InternalPortToggleBreakpointFeature(IFeatureProvider fp) {
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

  @Override
  public void execute(ICustomContext context) {
    doExecute(context, getFeatureProvider());
  }

  private static Optional<InternalPortShape> shapeFromContext(
      ICustomContext context) {
    return Optional.of(context).map(ICustomContext::getPictogramElements)
        .map(Stream::of).orElseGet(Stream::empty).findFirst()
        .filter(InternalPortShape.class::isInstance)
        .map(InternalPortShape.class::cast);
  }

  private static Optional<InternalPort> modelFromContext(
      ICustomContext context) {
    return shapeFromContext(context).map(InternalPortShape::getModelObject)
        .filter(InternalPort.class::isInstance).map(InternalPort.class::cast);
  }

  public static void doExecute(ICustomContext context, IFeatureProvider fp) {
    IResource resource = EclipseUtil.emfResourceToIResource(
        fp.getDiagramTypeProvider().getDiagram().eResource());
    InternalPort ip = modelFromContext(context).orElseThrow();
    String path = resource.getRawLocation().toOSString();

    try {
      IBreakpoint[] breakpoints = DebugPlugin.getDefault()
          .getBreakpointManager().getBreakpoints(InternalPortBreakpoint.ID);
      for (int i = 0; i < breakpoints.length; i++) {
        IBreakpoint breakpoint = breakpoints[i];
        if (resource.equals(breakpoint.getMarker().getResource())) {
          if (breakpoint.getMarker()
              .getAttribute(InternalPortBreakpoint.TASK_ID)
              .equals(ip.getTask().getId())
              && breakpoint.getMarker()
                  .getAttribute(InternalPortBreakpoint.PORT_ID)
                  .equals(ip.getId())) {
            breakpoint.delete();
            return;
          }
        }
      }
      InternalPortBreakpoint internalPortBreakpoint = new InternalPortBreakpoint(
          resource, path, ip.getTask().getId(), ip.getId());
      DebugPlugin.getDefault().getBreakpointManager()
          .addBreakpoint(internalPortBreakpoint);
    } catch (CoreException e) {
    }
  }
}
