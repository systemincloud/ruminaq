package org.ruminaq.tasks.console;

import java.util.Arrays;
import java.util.List;

import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.CreateFeaturesExtension;
import org.ruminaq.gui.features.create.PaletteCreateFeature;
import org.ruminaq.gui.palette.CommonPaletteCompartmentEntry;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.tasks.console.impl.Port;
import org.ruminaq.tasks.console.model.console.Console;
import org.ruminaq.tasks.console.model.console.ConsoleFactory;
import org.ruminaq.tasks.console.model.console.ConsoleType;
import org.ruminaq.tasks.features.CreateTaskFeature;

@Component(property = { "service.ranking:Integer=5" })
public class CreateFeaturesImpl implements CreateFeaturesExtension {

	@Override
	public List<Class<? extends ICreateFeature>> getFeatures() {
		return Arrays.asList(CreateFeature.class);
	}
	
	public static class CreateFeature extends CreateTaskFeature implements PaletteCreateFeature {

		public CreateFeature(IFeatureProvider fp) {
			super(fp, Console.class);
		}

		@Override
		public String getCompartment() {
			return CommonPaletteCompartmentEntry.DEFAULT_COMPARTMENT;
		}
		
		@Override
		public String getStack() {
			return CommonPaletteCompartmentEntry.SINKS_STACK;
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
}
