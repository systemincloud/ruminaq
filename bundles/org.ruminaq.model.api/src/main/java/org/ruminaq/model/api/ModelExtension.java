/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.model.api;

import java.util.List;
import java.util.Optional;

import org.ruminaq.model.model.ruminaq.DataType;

/**
 *
 * @author Marek Jagielski
 */
public interface ModelExtension {

	List<Class<? extends DataType>> getDataTypes();

	Optional<DataType> getDataTypeFromName(String name);

	boolean canCastFromTo(Class<? extends DataType> from, Class<? extends DataType> to);

	boolean isLossyCastFromTo(Class<? extends DataType> from, Class<? extends DataType> to);
}
