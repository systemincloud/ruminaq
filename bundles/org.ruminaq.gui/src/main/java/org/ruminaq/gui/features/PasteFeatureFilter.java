package org.ruminaq.gui.features;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.ruminaq.model.ruminaq.BaseElement;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PasteFeatureFilter {
	Class<? extends FeaturePredicate<BaseElement>> value();
}
