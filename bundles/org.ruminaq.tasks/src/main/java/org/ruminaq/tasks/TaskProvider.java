/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.IToolEntry;
import org.eclipse.graphiti.palette.impl.ObjectCreationToolEntry;
import org.eclipse.graphiti.palette.impl.StackEntry;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.tb.ContextMenuEntry;
import org.eclipse.graphiti.tb.IContextMenuEntry;
import org.eclipse.graphiti.tb.IDecorator;
import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Reference;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.features.tools.IContextButtonPadTool;
import org.ruminaq.model.ruminaq.InternalPort;
import org.ruminaq.model.ruminaq.MainTask;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.tasks.api.ITaskApi;
import org.ruminaq.tasks.api.TaskManagerHandler;
import org.ruminaq.tasks.features.AllInternalPortToggleBreakpointFeature;
import org.ruminaq.tasks.features.CreateTaskFeature;
import org.ruminaq.tasks.features.DecorateInternalPortFeature;
import org.ruminaq.tasks.features.DecorateTaskFeature;
import org.ruminaq.tasks.features.InternalPortBreakpointPropertiesFeature;
import org.ruminaq.tasks.features.InternalPortDisableBreakpointFeature;
import org.ruminaq.tasks.features.InternalPortDoubleClickFeature;
import org.ruminaq.tasks.features.InternalPortEnableBreakpointFeature;
import org.ruminaq.tasks.features.InternalPortToggleBreakpointFeature;
import org.ruminaq.tasks.features.UpdateTaskFeature;

public enum TaskProvider implements ITaskProvider {

    INSTANCE;

	@Reference
	private TaskManagerHandler taskManager;

    @Override
    public List<ICreateFeature> getCreateFeatures(IFeatureProvider fp) {
        return taskManager.getProjectVersionTasks().stream()
                .map(t -> t.getCreateFeatures(fp))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Override
    public void getPaletteCompartmentEntries(ICreateFeature[] cfs, Map<String, IPaletteCompartmentEntry> compartments) {
        LOOP:for(ICreateFeature cf : cfs) {
            if(cf instanceof CreateTaskFeature) {
                CreateTaskFeature ctf = (CreateTaskFeature) cf;
                String pk = ctf.getPaletteKey();
                if(pk == null) continue;
                IPaletteCompartmentEntry ipce = compartments.get(pk);
                if(ipce == null) continue;
                ObjectCreationToolEntry octe = new ObjectCreationToolEntry(cf.getCreateName(),    cf.getCreateDescription(),
                                                                           cf.getCreateImageId(), cf.getCreateImageId(), cf);
                String tk = ctf.getTaskCategory();
                if(tk == null) ipce.getToolEntries().add(octe);
                else {
                    for(IToolEntry te : ipce.getToolEntries())
                        if(te instanceof StackEntry && te.getLabel().equals(tk)) { ((StackEntry) te).addCreationToolEntry(octe); continue LOOP; };
                    StackEntry stackEntry = new StackEntry(tk, tk, null);
                    stackEntry.addCreationToolEntry(octe);
                    ipce.getToolEntries().add(stackEntry);
                }
            }
        }
    }

    @Override
    public void getTestPaletteCompartmentEntries(ICreateFeature[] cfs, Map<String, IPaletteCompartmentEntry> compartments) {
        LOOP:for(ICreateFeature cf : cfs) {
            if(cf instanceof CreateTaskFeature) {
                CreateTaskFeature ctf = (CreateTaskFeature) cf;
                String pk = ctf.getTestPaletteKey();
                if(pk == null) continue;
                IPaletteCompartmentEntry ipce = compartments.get(pk);
                if(ipce == null) continue;
                ObjectCreationToolEntry octe = new ObjectCreationToolEntry(cf.getCreateName(),    cf.getCreateDescription(),
                                                                           cf.getCreateImageId(), cf.getCreateLargeImageId(), cf);
                String tk = ctf.getTestTaskCategory();
                if (tk == null) ipce.getToolEntries().add(octe);
                else {
                    for (IToolEntry te : ipce.getToolEntries()) {
                        if(te instanceof StackEntry && te.getLabel().equals(tk)) {
                            ((StackEntry) te).addCreationToolEntry(octe);
                            continue LOOP;
                        }
                    }
                    StackEntry stackEntry = new StackEntry(tk, tk, null);
                    stackEntry.addCreationToolEntry(octe);
                    ipce.getToolEntries().add(stackEntry);
                }
            }
        }
    }

    @Override
    public List<ICustomFeature> getCustomFeatures(IFeatureProvider fp) {
        List<ICustomFeature> features = new ArrayList<>();

        features.add(new InternalPortToggleBreakpointFeature(fp));
        features.add(new InternalPortDisableBreakpointFeature(fp));
        features.add(new InternalPortEnableBreakpointFeature(fp));
        features.add(new InternalPortBreakpointPropertiesFeature(fp));
        features.add(new AllInternalPortToggleBreakpointFeature(fp));

        features.addAll(taskManager.getProjectVersionTasks().stream()
                .map(t -> t.getCustomFeatures(fp))
                .flatMap(List::stream)
                .collect(Collectors.toList()));

        return features;
    }

    public List<IContextMenuEntry> getContextMenu(ICustomContext context, IFeatureProvider fp) {
        List<IContextMenuEntry> entries = new ArrayList<>();

        PictogramElement[] pes = context.getPictogramElements();
        List<Object> bos = new ArrayList<>();
        for(PictogramElement pe : pes) bos.add(fp.getBusinessObjectForPictogramElement(pe));

        ICustomFeature[] customFeatures = fp.getCustomFeatures(context);

        for(ICustomFeature customFeature : customFeatures) {
            ContextMenuEntry menuEntry = new ContextMenuEntry(customFeature, context);
            menuEntry.setText(customFeature.getName());
            if(customFeature.isAvailable(context)) {
                if(bos.size() == 1) {
                    if(customFeature.getName().equals(InternalPortToggleBreakpointFeature    .NAME) && bos.get(0) instanceof InternalPort) entries.add(menuEntry);
                    if(customFeature.getName().equals(InternalPortDisableBreakpointFeature   .NAME) && bos.get(0) instanceof InternalPort) entries.add(menuEntry);
                    if(customFeature.getName().equals(InternalPortEnableBreakpointFeature    .NAME) && bos.get(0) instanceof InternalPort) entries.add(menuEntry);
                    if(customFeature.getName().equals(InternalPortBreakpointPropertiesFeature.NAME) && bos.get(0) instanceof InternalPort) entries.add(menuEntry);
                    if(customFeature.getName().equals(AllInternalPortToggleBreakpointFeature .NAME) && bos.get(0) instanceof MainTask)     entries.add(menuEntry);
                } else if(bos.size() > 1) {
                }
            }
        }


        taskManager.getProjectVersionTasks().stream()
            .forEach(t -> entries.addAll(t.getContextMenu(context, fp)));

        return entries;
    }

    @Override
    public List<IContextButtonPadTool> getContextButtonPadTools(IFeatureProvider fp, Object bo) {
        List<IContextButtonPadTool> features = new ArrayList<>();
        if(bo instanceof Task) {
            Task task = (Task) bo;
            for(ITaskApi t : taskManager.getTasks()) {
                if(t.getSymbolicName().equals(task.getBundleName()) && compare(t.getVersion(), Version.parseVersion(task.getVersion()))) {
                    Optional<List<IContextButtonPadTool>> fs = t.getContextButtonPadTools(fp, task);
                    if (fs.isPresent()) {
                        features.addAll(fs.get());
                    }
                }
            }
        }
        return features;
    }

    @Override
    public List<IDecorator> getDecorators(IFeatureProvider fp, PictogramElement pe) {
        List<IDecorator> decorators = new ArrayList<>();
        if(Graphiti.getPeService().getPropertyValue(pe, Constants.LABEL_PROPERTY) != null) return decorators;
        Object bo = fp.getBusinessObjectForPictogramElement(pe);
        if (bo instanceof Task) {
            Task task = (Task) bo;
            decorators.addAll(new DecorateTaskFeature().getDecorators(fp, task));
            for (ITaskApi t : taskManager.getTasks()) {
                if(t.getSymbolicName().equals(task.getBundleName()) && compare(t.getVersion(), Version.parseVersion(task.getVersion()))) {
                    decorators.addAll(t.getDecorators(fp, task));
                }
            }
        }
        decorators.addAll(new DecorateInternalPortFeature().getDecorators(fp, pe));
        return decorators;
    }

    @Override
    public IAddFeature getAddFeature(IAddContext context, IFeatureProvider fp) {
        Object bo = context.getNewObject();
        if(bo instanceof Task) {
            Task task = (Task) bo;
            for(ITaskApi t : taskManager.getTasks()) {
                if(t.getSymbolicName().equals(task.getBundleName()) && compare(t.getVersion(), Version.parseVersion(task.getVersion()))) {
                    Optional<IAddFeature> feature = t.getAddFeature(context, task, fp);
                    if(feature.isPresent()) {
                        return feature.get();
                    }
                }
            }
        }
        return null;
    }

    @Override
    public ICustomFeature getDoubleClickFeature(IDoubleClickContext context, IFeatureProvider fp) {
        Object bo = fp.getBusinessObjectForPictogramElement(context.getPictogramElements()[0]);
        if(bo instanceof Task) {
            Task task = (Task) bo;
            for(ITaskApi t : taskManager.getTasks()) {
                if(t.getSymbolicName().equals(task.getBundleName()) && compare(t.getVersion(), Version.parseVersion(task.getVersion()))) {
                    Optional<ICustomFeature> feature = t.getDoubleClickFeature(context, task, fp);
                    if (feature.isPresent()) {
                        return feature.get();
                    }
                }
            }
        } else if(bo instanceof InternalPort) {
            return new InternalPortDoubleClickFeature(context, (InternalPort) bo, fp);
        }
        return null;
    }

    @Override
    public IResizeShapeFeature getResizeShapeFeature(IResizeShapeContext context, IFeatureProvider fp) {
        Object bo = fp.getBusinessObjectForPictogramElement(context.getShape());
        if(bo instanceof Task) {
            Task task = (Task) bo;
            for(ITaskApi t : taskManager.getTasks()) {
                if(t.getSymbolicName().equals(task.getBundleName()) && compare(t.getVersion(), Version.parseVersion(task.getVersion()))) {
                    Optional<IResizeShapeFeature> feature = t.getResizeShapeFeature(context, task, fp);
                    if(feature.isPresent()) {
                        return feature.get();
                    }
                }
            }
        }
        return null;
    }

    @Override
    public IUpdateFeature getUpdateFeature(IUpdateContext context, IFeatureProvider fp) {
        Object bo = fp.getBusinessObjectForPictogramElement(context.getPictogramElement());
        if(bo instanceof Task) {
            Task task = (Task) bo;
            for(ITaskApi t : taskManager.getTasks()) {
                if(t.getSymbolicName().equals(task.getBundleName()) && compare(t.getVersion(), Version.parseVersion(task.getVersion()))) {
                    Optional<IUpdateFeature> feature = t.getUpdateFeature(context, task, fp);
                    if (feature.isPresent()) {
                        return feature.get();
                    }
                }
            }
            return new UpdateTaskFeature(fp);
        }
        return null;
    }

    @Override
    public Map<String, String> getImageKeyPath() {
        Map<String, String> images = new HashMap<>();
        images.putAll(Images.getImageKeyPath());
        taskManager.getTasks().stream()
            .forEach(t -> images.putAll(t.getImageKeyPath()));
        return images;
    }

    public static boolean compare(Version v1, Version v2) {
        return v1.getMajor() == v2.getMajor()
            && v1.getMinor() == v2.getMinor()
            && v1.getMicro() == v2.getMicro();
    }
}
