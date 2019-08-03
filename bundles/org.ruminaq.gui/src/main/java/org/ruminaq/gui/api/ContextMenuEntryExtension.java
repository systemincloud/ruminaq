package org.ruminaq.gui.api;

import java.util.function.Predicate;

import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;

public interface ContextMenuEntryExtension {

	Predicate<ICustomFeature> isAvailable(ICustomContext context);

}
