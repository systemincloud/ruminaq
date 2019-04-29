/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.randomgenerator.impl.strategy;

import java.util.List;

import org.eclipse.emf.common.util.EMap;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.data.Complex32I;
import org.ruminaq.tasks.randomgenerator.impl.Port;
import org.ruminaq.tasks.randomgenerator.impl.RandomGeneratorI;
import org.slf4j.Logger;

/**
 *
 * @author Marek Jagielski
 */

public class Complex32Strategy extends RandomGeneratorComplexStrategy {

	private final Logger logger = RunnerLoggerFactory.getLogger(Complex32Strategy.class);

	public Complex32Strategy(RandomGeneratorI task, EMap<String, String> eMap) {
		super(task, eMap);
	}

	@Override
	public void generate(List<Integer> dims) {
		logger.trace("generating Complex32");
		int n = dims.stream().reduce(1, (a, b) -> a * b);
		float[] real = new float[n];
		float[] imag = new float[n];

		for (int i = 0; i < n; i++) {
			double[] complex = super.getNextComplex();
			real[i] = (float) complex[0];
			imag[i] = (float) complex[1];
		}

		task.putData(Port.OUT, new Complex32I(real, imag, dims));
	}
}
