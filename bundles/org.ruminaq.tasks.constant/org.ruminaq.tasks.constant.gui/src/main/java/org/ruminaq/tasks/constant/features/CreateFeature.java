/*
 * (C) Copyright 2018 Marek Jagielski.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ruminaq.tasks.constant.features;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.services.Graphiti;
import org.osgi.framework.Version;
import org.ruminaq.consts.Constants.SicPlugin;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.tasks.TaskCategory;
import org.ruminaq.tasks.constant.Images;
import org.ruminaq.tasks.constant.impl.Port;
import org.ruminaq.tasks.constant.impl.strategy.Int32Strategy;
import org.ruminaq.tasks.constant.model.constant.Constant;
import org.ruminaq.tasks.constant.model.constant.ConstantFactory;
import org.ruminaq.tasks.features.CreateTaskFeature;

public class CreateFeature extends CreateTaskFeature {

	public CreateFeature(IFeatureProvider fp, String bundleName, Version version) {
		super(fp, Constant.class, bundleName, version);
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
		return TaskCategory.SOURCES.name();
	}

	@Override
	public String getTestTaskCategory() {
		return TaskCategory.SOURCES.name();
	}

	@Override
	public Object[] create(ICreateContext context) {
		Object[] os = super.create(context, ConstantFactory.eINSTANCE.createConstant());
		((Constant) os[0]).setDataType(EcoreUtil.copy(((Task) os[0]).getOutputPort().get(0).getDataType().get(0)));
		((Constant) os[0]).setValue(Int32Strategy.DEFAULT_VALUE);
		UpdateContext updateCtx = new UpdateContext(
				Graphiti.getLinkService().getPictogramElements(getDiagram(), (Constant) os[0]).get(0));
		getFeatureProvider().updateIfPossible(updateCtx);
		return os;
	}

	@Override
	protected Class<? extends PortsDescr> getPortsDescription() {
		return Port.class;
	}

	@Override
	public String getCreateImageId() {
		return Images.K.IMG_CONSTANT_PALETTE.name();
	}
}
