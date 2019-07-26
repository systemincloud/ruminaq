package org.ruminaq.gui.api;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.ruminaq.gui.features.FeatureFilter;

public interface BestFeatureExtension<T> extends MultipleFeaturesExtension<T> {

	default T getFeature(IContext context, IFeatureProvider fp) {
		return createFeatures(getFeatures().stream().filter(filter(context, fp))
		    .findFirst().stream().collect(Collectors.toList()), fp).stream()
		        .findFirst().orElse(null);
	}

	default Predicate<? super Class<? extends T>> filter(IContext context,
	    IFeatureProvider fp) {
		return clazz -> {
			return Optional.ofNullable(clazz.getAnnotation(FeatureFilter.class))
			    .map(FeatureFilter::value)
			    .<Constructor<? extends Predicate<IContext>>>map(f -> {
				    try {
					    return f.getConstructor();
				    } catch (NoSuchMethodException | SecurityException e) {
					    return null;
				    }
			    }).<Predicate<IContext>>map(c -> {
				    try {
					    return c.newInstance();
				    } catch (InstantiationException | IllegalAccessException
				        | IllegalArgumentException | InvocationTargetException e) {
					    return null;
				    }
			    }).orElse(c -> {
				    return true;
			    }).test(context);
		};
	}

	default List<Class<? extends T>> getFeatures() {
		return Collections.emptyList();
	}
}
