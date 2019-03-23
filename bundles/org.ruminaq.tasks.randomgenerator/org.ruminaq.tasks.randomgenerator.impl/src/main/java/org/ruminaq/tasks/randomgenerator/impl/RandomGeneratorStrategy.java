/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.randomgenerator.impl;

import java.util.List;

/**
 *
 * @author Marek Jagielski
 */
public abstract class RandomGeneratorStrategy {
	protected RandomGeneratorI task;

	public RandomGeneratorStrategy(RandomGeneratorI task) {
		this.task = task;
	}

	public abstract void generate(List<Integer> dims);
}
