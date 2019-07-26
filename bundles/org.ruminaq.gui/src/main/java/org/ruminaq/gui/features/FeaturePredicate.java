package org.ruminaq.gui.features;

import java.util.function.Predicate;

import org.eclipse.graphiti.features.IFeatureProvider;

public interface FeaturePredicate<T> extends Predicate<T> {

	default boolean test(T context, IFeatureProvider fp) {
		return test(context);
	}

	default boolean test(T context) {
		return true;
	}
}
