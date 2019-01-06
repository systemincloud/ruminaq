package org.ruminaq.tasks.randomgenerator.strategy;

import java.util.List;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.widgets.Composite;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.tasks.randomgenerator.Port;
import org.ruminaq.tasks.randomgenerator.PropertySpecificComposite;
import org.ruminaq.tasks.randomgenerator.ValueSaveListener;
import org.ruminaq.tasks.randomgenerator.impl.RandomGeneratorI;
import org.slf4j.Logger;
public class Complex32Strategy extends RandomGeneratorComplexStrategy {

	private final Logger logger = RunnerLoggerFactory.getLogger(Complex32Strategy.class);

	public Complex32Strategy(RandomGeneratorI task, EMap<String, String> eMap) {
		super(task, eMap);
	}

	@Override public void generate(List<Integer> dims) {
		logger.trace("generating Complex32");
		int n = 1;
		for(Integer i : dims) n *= i;
		float[] real = new float[n];
		float[] imag = new float[n];

		for(int i = 0; i < n; i++) {
			double[] complex = super.getNextComplex();
			real[i] = (float) complex[0];
			imag[i] = (float) complex[1];
		}

		task.putData(Port.OUT, new Complex32I(real, imag, dims));
	}

	public static PropertySpecificComposite createSpecificComposite(ValueSaveListener listener, Composite specificRoot, PictogramElement pe, TransactionalEditingDomain ed) {
		return new PropertySpecificComplexComposite(listener, specificRoot, pe, ed);
	}
}
