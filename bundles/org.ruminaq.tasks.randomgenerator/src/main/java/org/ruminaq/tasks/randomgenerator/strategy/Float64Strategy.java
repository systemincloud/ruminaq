package org.ruminaq.tasks.randomgenerator.strategy;

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

public class Float64Strategy extends RandomGeneratorNumericStrategy {

	private final Logger logger = RunnerLoggerFactory.getLogger(Int32Strategy.class);

	public Float64Strategy(RandomGeneratorI task, EMap<String, String> eMap, List<Integer> dims) {
		super(task, eMap, dims);
	}

	@Override public void generateRandom(List<Integer> dims) {
		logger.trace("generating Float64");

		int n = 1;
		for(Integer i : dims) n *= i;
		double[] values = new double[n];

		for(int i = 0; i < n; i++)
			values[i] = distribution.getNext();

		task.putData(Port.OUT, new Float64I(values, dims));
	}

	public static PropertySpecificComposite createSpecificComposite(ValueSaveListener listener, Composite specificRoot, PictogramElement pe, TransactionalEditingDomain ed) {
		return new PropertySpecificNumericComposite(listener, specificRoot, pe, ed) {
			@Override protected boolean checkIfValue(String value) { return NumericUtil.isMultiDimsNumeric(value); }
		};
	}

	@Override protected boolean isValue(String value) { return NumericUtil.isMultiDimsNumeric(value); }

	@Override protected DataI getDataOfValue(String value, List<Integer> dims) {
		String[] vs = NumericUtil.getMutliDimsValues(value);
		int n = 1;
		for(Integer i : dims) n *= i;
		if(vs.length == 1) {
			double[] values = new double[n];
			for(int i = 0; i < n; i++)
				values[i] = Double.parseDouble(vs[0]);
			return new Float64I(values, dims);
		} else {
			double[] values = new double[vs.length];
			List<Integer> dims2 = NumericUtil.getMutliDimsNumericDimensions(value);
			for(int i = 0; i < vs.length; i++)
				values[i] = Double.parseDouble(vs[i]);
			return new Float64I(values, dims2);
		}
	}
}
