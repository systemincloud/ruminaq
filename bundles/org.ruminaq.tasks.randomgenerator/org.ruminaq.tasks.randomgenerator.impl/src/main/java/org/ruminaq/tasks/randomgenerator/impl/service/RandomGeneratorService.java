/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.randomgenerator.impl.service;

import org.eclipse.emf.common.util.EMap;
import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.tasks.randomgenerator.impl.strategy.RandomGeneratorStrategy;

/**
 *
 * @author Marek Jagielski
 */
public interface RandomGeneratorService {
	RandomGeneratorStrategy getStrategy(DataType dt, EMap<String, String> eMap);
}
