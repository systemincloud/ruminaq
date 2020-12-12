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
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.model.ruminaq.InternalPort;
import org.ruminaq.util.EclipseUtil;
import org.ruminaq.util.Result;
import org.slf4j.Logger;

/**
 * InternalPortBreakpoint toggle.
 *
 * @author Marek Jagielski
 */
public class InternalPortToggleBreakpointFeature extends AbstractCustomFeature {

  private static final Logger LOGGER = ModelerLoggerFactory
      .getLogger(InternalPortToggleBreakpointFeature.class);

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
    return Optional.ofNullable(context)
        .map(ICustomContext::getPictogramElements).map(Stream::of)
        .orElseGet(Stream::empty).findFirst()
        .filter(InternalPortShape.class::isInstance)
        .map(InternalPortShape.class::cast);
  }

  private static Optional<InternalPort> modelFromContext(
      ICustomContext context) {
    return shapeFromContext(context).map(InternalPortShape::getModelObject)
        .filter(InternalPort.class::isInstance).map(InternalPort.class::cast);
  }

  /**
   * Retrieve breakpoint from InternalPort.
   *
   * @param resource file
   * @param ip       InternalPort domian object
   * @return breakpoint
   */
  public static Optional<IBreakpoint> breakpointFromModel(IResource resource,
      InternalPort ip) {
    return Stream
        .of(DebugPlugin.getDefault().getBreakpointManager()
            .getBreakpoints(InternalPortBreakpoint.ID))
        .filter(b -> resource.equals(b.getMarker().getResource()))
        .filter(b -> ip.getTask().getId().equals(Result.attempt(
            () -> b.getMarker().getAttribute(InternalPortBreakpoint.TASK_ID))
            .orElse(null)))
        .filter(b -> ip.getId().equals(Result.attempt(
            () -> b.getMarker().getAttribute(InternalPortBreakpoint.PORT_ID))
            .orElse(null)))
        .findFirst();
  }

  /**
   * Retrieve breakpoint from ICustomContext that points to InternalPort.
   *
   * @param context ICustomContext
   * @param fp      IFeatureProvider
   * @return breakpoint
   */
  public static Optional<IBreakpoint> breakpointFromContext(
      ICustomContext context, IFeatureProvider fp) {
    IResource resource = EclipseUtil.emfResourceToIResource(
        fp.getDiagramTypeProvider().getDiagram().eResource());
    return InternalPortToggleBreakpointFeature.modelFromContext(context)
        .flatMap(ip -> breakpointFromModel(resource, ip));
  }

  /**
   * Toggle breakpoint action.
   *
   * @param context context of action
   * @param fp      IFeatureProvider to be able to get resource
   */
  public static void doExecute(ICustomContext context, IFeatureProvider fp) {
    IResource resource = EclipseUtil.emfResourceToIResource(
        fp.getDiagramTypeProvider().getDiagram().eResource());
    InternalPort ip = modelFromContext(context).orElseThrow();

    Optional<IBreakpoint> breakpoint = breakpointFromContext(context, fp);
    try {
      if (breakpoint.isPresent()) {
        breakpoint.get().delete();
      } else {
        InternalPortBreakpoint internalPortBreakpoint = new InternalPortBreakpoint(
            resource, ip.getTask().getId(), ip.getId());
        DebugPlugin.getDefault().getBreakpointManager()
            .addBreakpoint(internalPortBreakpoint);
      }
    } catch (CoreException e) {
      LOGGER.error("Can't toggle breakpoint", e);
    }
  }
}
