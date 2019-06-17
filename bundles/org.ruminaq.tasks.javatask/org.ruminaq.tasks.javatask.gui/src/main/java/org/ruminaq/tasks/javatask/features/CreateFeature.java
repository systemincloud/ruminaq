/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.javatask.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.osgi.framework.Version;
import org.ruminaq.consts.Constants.SicPlugin;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.tasks.TaskCategory;
import org.ruminaq.tasks.features.CreateTaskFeature;
import org.ruminaq.tasks.javatask.Images;
import org.ruminaq.tasks.javatask.Port;
import org.ruminaq.tasks.javatask.model.javatask.JavaTask;
import org.ruminaq.tasks.javatask.model.javatask.JavataskFactory;

public class CreateFeature extends CreateTaskFeature {

  public CreateFeature(IFeatureProvider fp, String bundleName, Version version) {
    super(fp, JavaTask.class, bundleName, version);
  }

  @Override
  public String getPaletteKey() {
    return SicPlugin.GUI_ID.s();
  }

  @Override
  public String getTestPaletteKey() {
    return SicPlugin.GUI_ID.s();
  }

  @Override
  public String getTaskCategory() {
    return TaskCategory.USERDEFINED.name();
  }

  @Override
  public String getTestTaskCategory() {
    return TaskCategory.USERDEFINED.name();
  }

  @Override
  public Object[] create(ICreateContext context) {
    return super.create(context, JavataskFactory.eINSTANCE.createJavaTask());
  }

  @Override
  protected Class<? extends PortsDescr> getPortsDescription() {
    return Port.class;
  }

  @Override
  public String getCreateImageId() {
    return Images.K.IMG_JAVATASK_PALETTE.name();
  }
}
