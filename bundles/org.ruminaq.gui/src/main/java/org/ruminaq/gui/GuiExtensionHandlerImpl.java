package org.ruminaq.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICopyFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IMoveAnchorFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IPasteFeature;
import org.eclipse.graphiti.features.IReconnectionFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICopyContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IMoveAnchorContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.tb.IContextMenuEntry;
import org.eclipse.graphiti.tb.IDecorator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.ruminaq.gui.api.GuiExtension;
import org.ruminaq.gui.api.GuiExtensionHandler;
import org.ruminaq.gui.features.tools.IContextButtonPadTool;

@Component(immediate = true)
public class GuiExtensionHandlerImpl implements GuiExtensionHandler {

	private Collection<GuiExtension> extensions;

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	protected void bind(GuiExtension extension) {
		if (extensions == null) {
			extensions = new ArrayList<>();
		}
		extensions.add(extension);
	}

	protected void unbind(GuiExtension extension) {
		extensions.remove(extension);
	}

	@Override
	public Map<String, IPaletteCompartmentEntry> getPaletteCompartmentEntries(ICreateFeature[] cfs) {
		return extensions.stream()
				.map(ext -> ext.getPaletteCompartmentEntries(cfs))
				.map(Map::entrySet)
		        .flatMap(Collection::stream)
		        .collect(
		            Collectors.toMap(
		                Map.Entry::getKey,
		                Map.Entry::getValue
		            )
		        );
	}

	@Override
	public Map<String, IPaletteCompartmentEntry> getTestPaletteCompartmentEntries(ICreateFeature[] cfs) {
		return extensions.stream()
				.map(ext -> ext.getTestPaletteCompartmentEntries(cfs))
				.map(Map::entrySet)
		        .flatMap(Collection::stream)
		        .collect(
		            Collectors.toMap(
		                Map.Entry::getKey,
		                Map.Entry::getValue
		            )
		        );
		}

	@Override
	public Collection<ICreateFeature> getCreateFeatures(IFeatureProvider fp) {
		return extensions.stream()
				.map(ext -> ext.getCreateFeatures(fp))
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	@Override
	public Collection<ICreateConnectionFeature> getCreateConnectionFeatures(IFeatureProvider fp) {
		return extensions.stream()
				.map(ext -> ext.getCreateConnectionFeatures(fp))
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	@Override
	public Collection<ICustomFeature> getCustomFeatures(IFeatureProvider fp) {
		return extensions.stream()
				.map(ext -> ext.getCustomFeatures(fp))
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	@Override
	public Collection<IContextButtonPadTool> getContextButtonPadTools(Object bo) {
		return extensions.stream()
				.map(ext -> ext.getContextButtonPadTools(bo))
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	@Override
	public Collection<IDecorator> getDecorators(PictogramElement pe, IFeatureProvider fp) {
		return extensions.stream()
				.map(ext -> ext.getDecorators(pe, fp))
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	@Override
	public Collection<IContextMenuEntry> getContextMenu(ICustomContext cxt, IFeatureProvider fp) {
		return extensions.stream()
				.map(ext -> ext.getContextMenu(cxt, fp))
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	@Override
	public IAddFeature getAddFeature(IAddContext cxt, IFeatureProvider fp) {
		return extensions.stream()
				.map(ext -> ext.getAddFeature(cxt, fp))
				.findFirst()
				.get();
	}

	@Override
	public IResizeShapeFeature getResizeShapeFeature(IResizeShapeContext cxt, IFeatureProvider fp) {
		return extensions.stream()
				.map(ext -> ext.getResizeShapeFeature(cxt, fp))
				.findFirst()
				.get();
	}

	@Override
	public IMoveShapeFeature getMoveShapeFeature(IMoveShapeContext cxt, IFeatureProvider fp) {
		return extensions.stream()
				.map(ext -> ext.getMoveShapeFeature(cxt, fp))
				.findFirst()
				.get();
	}

	@Override
	public IMoveAnchorFeature getMoveAnchorFeature(IMoveAnchorContext cxt, IFeatureProvider fp) {
		return extensions.stream()
				.map(ext -> ext.getMoveAnchorFeature(cxt, fp))
				.findFirst()
				.get();
	}

	@Override
	public IDirectEditingFeature getDirectEditingFeature(IDirectEditingContext cxt, IFeatureProvider fp) {
		return extensions.stream()
				.map(ext -> ext.getDirectEditingFeature(cxt, fp))
				.findFirst()
				.get();
	}

	@Override
	public IUpdateFeature getUpdateFeature(IUpdateContext cxt, IFeatureProvider fp) {
		return extensions.stream()
				.map(ext -> ext.getUpdateFeature(cxt, fp))
				.findFirst()
				.get();
	}

	@Override
	public ILayoutFeature getLayoutFeature(ILayoutContext cxt, IFeatureProvider fp) {
		return extensions.stream()
				.map(ext -> ext.getLayoutFeature(cxt, fp))
				.findFirst()
				.get();
	}

	@Override
	public IReconnectionFeature getReconnectionFeature(IReconnectionContext cxt, IFeatureProvider fp) {
		return extensions.stream()
				.map(ext -> ext.getReconnectionFeature(cxt, fp))
				.findFirst()
				.get();
	}

	@Override
	public IDeleteFeature getDeleteFeature(IDeleteContext cxt, IFeatureProvider fp) {
		return extensions.stream()
				.map(ext -> ext.getDeleteFeature(cxt, fp))
				.findFirst()
				.get();
	}

	@Override
	public ICustomFeature getDoubleClickFeature(IDoubleClickContext cxt, IFeatureProvider fp) {
		return extensions.stream()
				.map(ext -> ext.getDoubleClickFeature(cxt, fp))
				.findFirst()
				.get();
	}

	@Override
	public ICopyFeature getCopyFeature(ICopyContext ctx, IFeatureProvider fp) {
		return extensions.stream()
				.map(ext -> ext.getCopyFeature(ctx, fp))
				.findFirst()
				.get();
	}

	@Override
	public IPasteFeature getPasteFeature(IPasteContext ctx, IFeatureProvider fp) {
		return extensions.stream()
				.map(ext -> ext.getPasteFeature(ctx, fp))
				.findFirst()
				.get();
	}

	@Override
	public Map<String, String> getImageKeyPath() {
		return extensions.stream()
				.map(GuiExtension::getImageKeyPath)
				.map(Map::entrySet)
		        .flatMap(Collection::stream)
		        .collect(
		            Collectors.toMap(
		                Map.Entry::getKey,
		                Map.Entry::getValue
		            )
		        );
	}

}
