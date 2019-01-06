package org.ruminaq.tasks.randomgenerator.impl;

import java.util.List;

public abstract class RandomGeneratorStrategy {
	protected RandomGeneratorI task;
	public RandomGeneratorStrategy(RandomGeneratorI task) {
		this.task = task;
	}
	public abstract void generate(List<Integer> dims);
}
