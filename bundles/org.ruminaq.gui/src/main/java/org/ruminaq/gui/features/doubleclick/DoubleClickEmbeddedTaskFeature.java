/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.doubleclick;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.ruminaq.eclipse.EclipseUtil;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.FeaturePredicate;
import org.ruminaq.model.ruminaq.EmbeddedTask;
import org.ruminaq.model.ruminaq.UserDefinedTask;
import org.ruminaq.util.Try;

/**
 * EmbeddedTask DoubleClickFeature.
 *
 * @author Marek Jagielski
 */
@FeatureFilter(DoubleClickEmbeddedTaskFeature.Filter.class)
public class DoubleClickEmbeddedTaskFeature
    extends AbstractUserDefinedTaskDoubleClickFeature {

  public static class Filter implements FeaturePredicate<IContext> {
    @Override
    public boolean test(IContext context) {
      return toModel(context, EmbeddedTask.class).isPresent();
    }
  }

  public DoubleClickEmbeddedTaskFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public void execute(ICustomContext context) {
    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
        .getActivePage();
    toModel(context, EmbeddedTask.class)
        .map(UserDefinedTask::getImplementationPath).filter(""::equals)
        .map(p -> new Path(EclipseUtil.getProjectOf(getDiagram()) + "/" + p))
        .map(p -> ResourcesPlugin.getWorkspace().getRoot().getFile(p))
        .ifPresent(f -> Try.check(() -> IDE.openEditor(page, f, true)));
  }
}
