package org.ruminaq.tasks.randomgenerator.extension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.widgets.Composite;
import org.osgi.framework.BundleContext;
import org.ruminaq.consts.Constants;
import org.ruminaq.model.sic.DataType;
import org.ruminaq.tasks.randomgenerator.PropertySpecificComposite;
import org.ruminaq.tasks.randomgenerator.ValueSaveListener;
import org.ruminaq.util.ExtensionUtil;

public enum RandomGeneratorExtensionManager {
	INSTANCE;
	
	private List<RandomGeneratorExtension> extensions = new ArrayList<>();
	
	public void init(BundleContext ctx) {
		extensions.addAll(ExtensionUtil.getExtensions(RandomGeneratorExtension.class, Constants.EXTENSION_PREFIX, ctx));
	}

	public List<Class<? extends DataType>> getDataTypes() {
		List<Class<? extends DataType>> ret = new LinkedList<>();
		for(RandomGeneratorExtension ext : extensions) {
			List<Class<? extends DataType>> dts = ext.getDataTypes();
			if(dts != null) ret.addAll(dts);
		}
		return ret;
	}

	public Map<String, PropertySpecificComposite> getComposites(ValueSaveListener listener, Composite specificRoot, PictogramElement pe, TransactionalEditingDomain ed) {
		Map<String, PropertySpecificComposite> ret = new HashMap<>();
		for(RandomGeneratorExtension ext : extensions) {
			Map<String, PropertySpecificComposite> cs = ext.getSpecific(listener, specificRoot, pe, ed);
			if(cs != null) ret.putAll(cs);
		}
		return ret;
	}
}
