package org.ruminaq.tasks;

import java.util.List;
import java.util.Map;

import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.tb.IDecorator;
import org.ruminaq.gui.features.tools.IContextButtonPadTool;

public interface ITaskProvider {

    List<ICreateFeature> getCreateFeatures(IFeatureProvider fp);

    void getPaletteCompartmentEntries(ICreateFeature[] cfs, Map<String, IPaletteCompartmentEntry> compartments);

    void getTestPaletteCompartmentEntries(ICreateFeature[] cfs, Map<String, IPaletteCompartmentEntry> compartments);

    List<ICustomFeature> getCustomFeatures(IFeatureProvider fp);

    List<IContextButtonPadTool> getContextButtonPadTools(IFeatureProvider fp, Object bo);

    List<IDecorator> getDecorators(IFeatureProvider fp, PictogramElement pe);

    IAddFeature getAddFeature(IAddContext cxt, IFeatureProvider fp);

    ICustomFeature getDoubleClickFeature(IDoubleClickContext cxt, IFeatureProvider fp);

    IResizeShapeFeature getResizeShapeFeature(IResizeShapeContext cxt, IFeatureProvider fp);

    IUpdateFeature getUpdateFeature(IUpdateContext cxt, IFeatureProvider fp);

    Map<String, String> getImageKeyPath();
}
