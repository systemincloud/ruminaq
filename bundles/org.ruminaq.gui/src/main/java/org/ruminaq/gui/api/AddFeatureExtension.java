/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.api;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;

/**
 * Service api providing graphiti AddFeature.
 *
 * @author Marek Jagielski
 */
public interface AddFeatureExtension {

	default IAddFeature getAddFeature(IAddContext cxt, IFeatureProvider fp) {
		return Optional.ofNullable(getAddFeatures().stream().filter(f -> {
			return Optional.ofNullable(f.getAnnotation(RuminaqFeature.class))
			    .map(RuminaqFeature::value)
			    .map(c -> c.isAssignableFrom(cxt.getNewObject().getClass()))
			    .orElse(false);
		}).findFirst().orElse(null))
		    .map(f -> {
		    	try {
						return f.getConstructor(IFeatureProvider.class);
					} catch (NoSuchMethodException | SecurityException e) {
						return null;
					}
		    })
		    .filter(Objects::nonNull)
		    .map(c -> {
					try {
						return c.newInstance(fp);
					} catch (InstantiationException | IllegalAccessException
					    | IllegalArgumentException | InvocationTargetException e) {
						return null;
					}
				})
		    .filter(Objects::nonNull)
		    .orElse(null);
	}

	default List<Class<? extends IAddFeature>> getAddFeatures() {
		return Collections.emptyList();
	}
}
