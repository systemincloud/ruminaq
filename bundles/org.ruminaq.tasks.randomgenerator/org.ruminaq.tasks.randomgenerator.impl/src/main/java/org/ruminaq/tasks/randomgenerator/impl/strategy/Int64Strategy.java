/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.randomgenerator.impl.strategy;

import java.util.List;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.widgets.Composite;
import org.ruminaq.tasks.randomgenerator.Port;
import org.ruminaq.tasks.randomgenerator.PropertySpecificComposite;
import org.ruminaq.tasks.randomgenerator.ValueSaveListener;
import org.ruminaq.tasks.randomgenerator.impl.RandomGeneratorI;
import org.ruminaq.util.NumericUtil;
import org.slf4j.Logger;

public class Int64Strategy extends RandomGeneratorNumericStrategy {

	private final Logger logger = RunnerLoggerFactory.getLogger(Int64Strategy.class);

	public Int64Strategy(RandomGeneratorI task, EMap<String, String> eMap, List<Integer> dims) {
		super(task, eMap, dims);
	}

	@Override public void generateRandom(List<Integer> dims) {
		logger.trace("generating Int64");

		int n = 1;
		for(Integer i : dims) n *= i;
		long[] values = new long[n];

		for(int i = 0; i < n; i++)
			values[i] = Math.round(distribution.getNext());

		task.putData(Port.OUT, new Int64I(values, dims));
	}

	public static PropertySpecificComposite createSpecificComposite(ValueSaveListener listener, Composite specificRoot, PictogramElement pe, TransactionalEditingDomain ed) {
		return new PropertySpecificNumericComposite(listener, specificRoot, pe, ed) {
			@Override protected boolean checkIfValue(String value) { return NumericUtil.isMultiDimsInteger(value); }
		};
	}

	@Override protected boolean isValue(String value) { return NumericUtil.isMultiDimsInteger(value); }

	@Override protected DataI getDataOfValue(String value, List<Integer> dims) {
		String[] vs = NumericUtil.getMutliDimsValues(value);
		int n = 1;
		for(Integer i : dims) n *= i;
		if(vs.length == 1) {
			long[] values = new long[n];
			for(int i = 0; i < n; i++)
				values[i] = Long.parseLong(vs[0]);
			return new Int64I(values, dims);
		} else {
			long[] values = new long[vs.length];
			List<Integer> dims2 = NumericUtil.getMutliDimsNumericDimensions(value);
			for(int i = 0; i < vs.length; i++)
				values[i] = Long.parseLong(vs[i]);
			return new Int64I(values, dims2);
		}
	}
}
