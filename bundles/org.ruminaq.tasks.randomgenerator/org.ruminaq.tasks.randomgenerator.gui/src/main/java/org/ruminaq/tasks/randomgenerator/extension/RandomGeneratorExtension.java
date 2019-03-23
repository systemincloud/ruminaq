package org.ruminaq.tasks.randomgenerator.extension;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.widgets.Composite;
import org.ruminaq.model.sic.DataType;
import org.ruminaq.tasks.randomgenerator.PropertySpecificComposite;
import org.ruminaq.tasks.randomgenerator.ValueSaveListener;

public interface RandomGeneratorExtension {
	List<Class<? extends DataType>>        getDataTypes();
	Map<String, PropertySpecificComposite> getSpecific(ValueSaveListener listener, Composite specificRoot, PictogramElement pe, TransactionalEditingDomain ed);
	Map<String, String>                    getInitSpecific();
}
