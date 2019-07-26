package org.ruminaq.gui.features;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.eclipse.graphiti.features.context.IContext;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FeatureFilter {
	Class<? extends FeaturePredicate<IContext>> value();
}
