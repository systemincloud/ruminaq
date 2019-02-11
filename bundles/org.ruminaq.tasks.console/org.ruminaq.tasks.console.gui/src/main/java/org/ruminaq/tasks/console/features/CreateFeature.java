package org.ruminaq.tasks.console.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.osgi.framework.Version;
import org.ruminaq.consts.Constants.SicPlugin;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.tasks.TaskCategory;
import org.ruminaq.tasks.console.Images;
import org.ruminaq.tasks.console.impl.Port;
import org.ruminaq.tasks.console.model.console.Console;
import org.ruminaq.tasks.console.model.console.ConsoleFactory;
import org.ruminaq.tasks.console.model.console.ConsoleType;
import org.ruminaq.tasks.features.CreateTaskFeature;

public class CreateFeature extends CreateTaskFeature {

	public CreateFeature(IFeatureProvider fp, String bundleName, Version version) {
		super(fp, Console.class, bundleName, version);
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
		return TaskCategory.TEXT.name();
	}

	@Override
	public String getTestTaskCategory() {
		return TaskCategory.TEXT.name();
	}

	@Override
	public Object[] create(ICreateContext context) {
		Console console = ConsoleFactory.eINSTANCE.createConsole();
		console.setOnlyLocal(true);
		console.setConsoleType(ConsoleType.IN);
		console.setNewLine(true);
		return super.create(context, console);
	}

	@Override
	protected Class<? extends PortsDescr> getPortsDescription() {
		return Port.class;
	}

	@Override
	public String getCreateImageId() {
		return Images.K.IMG_CONSOLE_PALETTE.name();
	}
}
