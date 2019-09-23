/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.constant;

import java.util.Optional;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.tasks.api.ITaskApi;
import org.ruminaq.tasks.constant.features.ResizeShapeFeature;
import org.ruminaq.tasks.constant.features.UpdateFeature;
import org.ruminaq.tasks.constant.model.constant.Constant;

@Component
public class TaskApi implements ITaskApi {

  @Override
  public Optional<IResizeShapeFeature> getResizeShapeFeature(
      IResizeShapeContext cxt, Task t, IFeatureProvider fp) {
    return ITaskApi.ifInstance(t, Constant.class, new ResizeShapeFeature(fp));
  }

  @Override
  public Optional<IUpdateFeature> getUpdateFeature(IUpdateContext cxt, Task t,
      IFeatureProvider fp) {
    return ITaskApi.ifInstance(t, Constant.class, new UpdateFeature(fp));
  }

}
