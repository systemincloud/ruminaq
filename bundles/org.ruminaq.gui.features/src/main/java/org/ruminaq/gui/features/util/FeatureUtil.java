package org.ruminaq.gui.features.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.palette.IToolEntry;
import org.eclipse.graphiti.palette.impl.ObjectCreationToolEntry;
import org.eclipse.graphiti.palette.impl.StackEntry;
import org.ruminaq.model.config.Config;
import org.ruminaq.model.config.ConfigCategory;
import org.ruminaq.model.config.ConfigEntry;

public final class FeatureUtil {

    public static Collection<? extends IToolEntry> getStackEntries(ConfigCategory[] ccs, ICreateFeature[] createFeatures, Config conf, boolean test) {
        List<IToolEntry> entries = new ArrayList<IToolEntry>();
        Map<ConfigCategory, StackEntry> stacks = new HashMap<>();

        for(ConfigCategory cc : ccs) {
            StackEntry stackEntry = new StackEntry(cc.name(), cc.name(), null);
            stacks.put(cc, stackEntry);
            entries.add(stackEntry);
        }

        for (ICreateFeature cf : createFeatures) {
            ObjectCreationToolEntry objectCreationToolEntry = new ObjectCreationToolEntry(cf.getCreateName(), cf.getCreateDescription(),
                                                                                          cf.getCreateImageId(), cf.getCreateLargeImageId(), cf);
            ConfigEntry ce = conf.getEntryForClassName(cf.getName().replace(" ", ""));
            if(ce == null) continue;
            if     (!test && ce.entry.getValue2()) stacks.get(ce.entry.getValue1()).addCreationToolEntry(objectCreationToolEntry);
            else if( test && ce.entry.getValue3()) stacks.get(ce.entry.getValue1()).addCreationToolEntry(objectCreationToolEntry);
        }

        return entries;
    }
}
