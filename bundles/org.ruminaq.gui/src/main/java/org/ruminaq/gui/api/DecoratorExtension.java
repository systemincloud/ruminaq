package org.ruminaq.gui.api;

import java.util.Collection;

import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.tb.IDecorator;

public interface DecoratorExtension {

	Collection<IDecorator> getDecorators(PictogramElement pe);

}
