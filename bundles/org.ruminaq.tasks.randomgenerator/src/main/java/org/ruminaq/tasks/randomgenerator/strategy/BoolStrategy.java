package org.ruminaq.tasks.randomgenerator.strategy;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.eclipse.emf.common.util.EMap;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.data.BoolI;
import org.ruminaq.tasks.randomgenerator.Port;
import org.ruminaq.tasks.randomgenerator.impl.RandomGeneratorI;
import org.ruminaq.tasks.randomgenerator.impl.RandomGeneratorStrategy;
import org.slf4j.Logger;

public class BoolStrategy extends RandomGeneratorStrategy {

	private final Logger logger = RunnerLoggerFactory.getLogger(BoolStrategy.class);

	public BoolStrategy(RandomGeneratorI task, EMap<String, String> eMap) {
		super(task);
	}

	@Override public void generate(List<Integer> dims) {
		logger.trace("generating Bool");

		int n = 1;
		for(Integer i : dims) n *= i;
		boolean[] values = new boolean[n];

		for(int i = 0; i < n; i++)
			values[i] = ThreadLocalRandom.current().nextBoolean();

		task.putData(Port.OUT, new BoolI(values, dims));
	}
}
