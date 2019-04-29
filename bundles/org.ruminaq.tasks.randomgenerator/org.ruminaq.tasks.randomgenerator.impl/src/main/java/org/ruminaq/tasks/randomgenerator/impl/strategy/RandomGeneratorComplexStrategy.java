package org.ruminaq.tasks.randomgenerator.impl.strategy;

import org.eclipse.emf.common.util.EMap;
import org.ruminaq.tasks.randomgenerator.distributions.Distributon;
import org.ruminaq.tasks.randomgenerator.impl.RandomGeneratorI;
import org.ruminaq.tasks.randomgenerator.impl.RandomGeneratorStrategy;
import org.ruminaq.util.NumericUtil;

public abstract class RandomGeneratorComplexStrategy extends RandomGeneratorStrategy {

	private boolean rect = true;

	private Distributon aDistribution;
	private double      aValue;
	private Distributon bDistribution;
	private double      bValue;
	private Distributon mDistribution;
	private double      mValue;
	private Distributon gDistribution;
	private double      gValue;

	public RandomGeneratorComplexStrategy(RandomGeneratorI task, EMap<String, String> eMap) {
		super(task);
		String rep = eMap.get(PropertySpecificComplexComposite.COMPLEX_REPRESENTATION);
		this.rect = rep == null || rep.equals(PropertySpecificComplexComposite.RECTANGULAR_REPRESENTATION) ? true : false;
		if(rect) {
			String a = eMap.get(PropertySpecificComplexComposite.COMPLEX_A);
			if(a == null) a = PropertySpecificComplexComposite.COMPLEX_DEFAULT_A;
			if(isValue(a)) this.aValue = Double.parseDouble(a);
			else this.aDistribution = RandomGeneratorNumericStrategy.getNumericStrategy(a);
			String b = eMap.get(PropertySpecificComplexComposite.COMPLEX_B);
			if(b == null) b = PropertySpecificComplexComposite.COMPLEX_DEFAULT_B;
			if(isValue(b)) this.bValue = Double.parseDouble(b);
			else this.bDistribution = RandomGeneratorNumericStrategy.getNumericStrategy(b);
		} else {
			String m = eMap.get(PropertySpecificComplexComposite.COMPLEX_MAG);
			if(m == null) m = PropertySpecificComplexComposite.COMPLEX_DEFAULT_MAG;
			if(isValue(m)) this.mValue = Math.abs(Double.parseDouble(m));
			else this.mDistribution = RandomGeneratorNumericStrategy.getNumericStrategy(m);
			String g = eMap.get(PropertySpecificComplexComposite.COMPLEX_ARG);
			if(g == null) g = PropertySpecificComplexComposite.COMPLEX_DEFAULT_ARG;
			if(isValue(g)) this.gValue = Double.parseDouble(g);
			else this.gDistribution = RandomGeneratorNumericStrategy.getNumericStrategy(g);
		}
	}

	private boolean isValue(String value) {	return NumericUtil.isOneDimNumeric(value); }

	public double[] getNextComplex() {
		if(rect) return new double[] { aDistribution != null ? aDistribution.getNext() : aValue, bDistribution != null ? bDistribution.getNext() : bValue };
		else {
			double m = mDistribution != null ? getPositive(mDistribution) : mValue;
			double g = gDistribution != null ? gDistribution.getNext() : gValue;
			return new double[] { m*Math.cos(g), m*Math.sin(g) };
		}
	}

	private double getPositive(Distributon distribution) {
		double ret = 0;
		while((ret = distribution.getNext()) < 0);
		return ret;
	}
}
