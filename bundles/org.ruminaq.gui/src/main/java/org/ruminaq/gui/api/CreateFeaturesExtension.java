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
import java.util.stream.Collectors;

import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;

/**
 * Service api providing graphiti AddFeature.
 *
 * @author Marek Jagielski
 */
public interface CreateFeaturesExtension {

	default List<ICreateFeature> getCreateFeatures(IFeatureProvider fp) {
		return Optional.ofNullable(getCreateFeatures())
		    .orElse(Collections.emptyList()).stream()
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
		  .collect(Collectors.toList());
	}

	default List<Class<? extends ICreateFeature>> getCreateFeatures() {
		return Collections.emptyList();
	}
}
