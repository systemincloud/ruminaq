/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.randomgenerator.impl.strategy;

import java.util.List;

import org.eclipse.emf.common.util.EMap;
import org.ruminaq.tasks.randomgenerator.impl.Port;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.data.Complex64I;
import org.ruminaq.tasks.randomgenerator.impl.RandomGeneratorI;
import org.slf4j.Logger;

public class Complex64Strategy extends RandomGeneratorComplexStrategy {

	private final Logger logger = RunnerLoggerFactory.getLogger(Complex64Strategy.class);

	public Complex64Strategy(RandomGeneratorI task, EMap<String, String> eMap) {
		super(task, eMap);
	}

	@Override public void generate(List<Integer> dims) {
		logger.trace("generating Complex64");
		int n = 1;
		for(Integer i : dims) n *= i;
		double[] real = new double[n];
		double[] imag = new double[n];

		for(int i = 0; i < n; i++) {
			double[] complex = super.getNextComplex();
			real[i] = complex[0];
			imag[i] = complex[1];
		}

		task.putData(Port.OUT, new Complex64I(real, imag, dims));
	}
}
