package org.ruminaq.gui.features.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.palette.IToolEntry;
import org.eclipse.graphiti.palette.impl.ObjectCreationToolEntry;
import org.eclipse.graphiti.palette.impl.StackEntry;
import org.ruminaq.model.config.Config;
import org.ruminaq.model.config.ConfigCategory;
import org.ruminaq.model.config.ConfigEntry;
import org.ruminaq.model.ruminaq.BaseElement;

public final class FeatureUtil {

	@SuppressWarnings("unchecked")
	public static <T> T getLocalFeature(Class<?> packageClass, Class<T> returnInterface, Config config, Object o, IFeatureProvider fp) {
		if(o == null) return null;
		if(config.containsClass(o.getClass().getGenericInterfaces()[0])) {
			String name = o.getClass().getSimpleName();
			if(name.endsWith("Impl")) name = name.substring(0, name.length() - 4);
			Class<?> clazz;
			try {
				clazz = packageClass.getClassLoader().loadClass(packageClass.getPackage().getName() + "." + packageClass.getSimpleName().replace("Feature", "") + name + "Feature");
			} catch (ClassNotFoundException e) { return null; }

			if(returnInterface.isAssignableFrom(clazz)) {
				try {
				    return (T) clazz.getConstructor(IFeatureProvider.class).newInstance(fp);
				} catch (InvocationTargetException | InstantiationException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> Collection<? extends T> getAllLocalFeatures(Class<?> packageClass, Class<T> returnInterface, Config config, IFeatureProvider fp) {
		List<T> features = new ArrayList<T>();

		for(Class<? extends BaseElement> be : config.getAllClasses()) {
			Class<?> clazz;
			try {
				clazz = packageClass.getClassLoader().loadClass(packageClass.getPackage().getName() + "." + packageClass.getSimpleName().replace("Feature", "") + be.getSimpleName() + "Feature");
			} catch (ClassNotFoundException e) { continue; }
			if(returnInterface.isAssignableFrom(clazz)) {
				try {
				    features.add((T) clazz.getConstructor(IFeatureProvider.class).newInstance(fp));
				} catch (InvocationTargetException | InstantiationException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
			}
		}
		return features;
	}

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
