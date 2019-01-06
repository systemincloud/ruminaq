package org.ruminaq.tasks.randomgenerator.impl;

import java.util.List;

import org.eclipse.emf.common.util.EMap;
import org.ruminaq.model.dt.Bool;
import org.ruminaq.model.dt.Complex32;
import org.ruminaq.model.dt.Complex64;
import org.ruminaq.model.dt.Control;
import org.ruminaq.model.dt.Decimal;
import org.ruminaq.model.dt.Float32;
import org.ruminaq.model.dt.Float64;
import org.ruminaq.model.dt.Int32;
import org.ruminaq.model.dt.Int64;
import org.ruminaq.model.dt.Text;
import org.ruminaq.model.sic.DataType;
import org.ruminaq.model.sic.Task;
import org.ruminaq.tasks.randomgenerator.impl.service.RandomGeneratorServiceManager;
import org.ruminaq.tasks.randomgenerator.strategy.BoolStrategy;
import org.ruminaq.tasks.randomgenerator.strategy.Complex32Strategy;
import org.ruminaq.tasks.randomgenerator.strategy.Complex64Strategy;
import org.ruminaq.tasks.randomgenerator.strategy.ControlStrategy;
import org.ruminaq.tasks.randomgenerator.strategy.DecimalStrategy;
import org.ruminaq.tasks.randomgenerator.strategy.Float32Strategy;
import org.ruminaq.tasks.randomgenerator.strategy.Float64Strategy;
import org.ruminaq.tasks.randomgenerator.strategy.Int32Strategy;
import org.ruminaq.tasks.randomgenerator.strategy.Int64Strategy;
import org.ruminaq.tasks.randomgenerator.strategy.TextStrategy;
import org.ruminaq.util.GlobalUtil;
import org.ruminaq.util.RandomUtil;
import org.slf4j.Logger;

public class RandomGeneratorI extends GeneratorI {

	private final Logger logger = RunnerLoggerFactory.getLogger(RandomGeneratorI.class);

	private RandomGenerator randomGenerator;

	private List<Integer> dims;
	private long          interval = -2;

    private RandomGeneratorStrategy strategy;

	public RandomGeneratorI(EmbeddedTaskI parent, Task task) {
		super(parent, task);
		setGenerator(true);
		this.randomGenerator = (RandomGenerator) task;
		DataType dataType = randomGenerator.getDataType();

		if(!RandomUtil.containsRandom(parent.replaceVariables(randomGenerator.getDimensions()))) {
			this.dims = GlobalUtil.getDimensions(parent.replaceVariables(randomGenerator.getDimensions()));
			logger.trace("dimensions doesn't contain random. There are {} dimensions", this.dims.size());
		}

		if(!RandomUtil.containsRandom(parent.replaceVariables(randomGenerator.getInterval()))) {
			this.interval = GlobalUtil.getMilisecondsFromTime(parent.replaceVariables(randomGenerator.getInterval()));
			logger.trace("interval doesn't contain random. There are {} miliseconds", this.interval);
		}

		this.strategy = getStrategy(dataType, randomGenerator.getSpecific(), this.dims);
		logger.trace("chosen strategy is {}", this.strategy.getClass().getSimpleName());
	}

	private RandomGeneratorStrategy getStrategy(DataType dt, EMap<String, String> eMap, List<Integer> dims) {
		logger.trace("look for strategy for {}", dt);
		RandomGeneratorStrategy rgs = RandomGeneratorServiceManager.INSTANCE.getStrategy(dt, eMap);
		if(rgs != null) return rgs;

		if(dt instanceof Text)      return new TextStrategy     (this, eMap);
		if(dt instanceof Bool)      return new BoolStrategy     (this, eMap);
		if(dt instanceof Complex32) return new Complex32Strategy(this, eMap);
		if(dt instanceof Complex64) return new Complex64Strategy(this, eMap);
		if(dt instanceof Control)   return new ControlStrategy  (this, eMap);
		if(dt instanceof Int32)     return new Int32Strategy    (this, eMap, dims);
		if(dt instanceof Int64)     return new Int64Strategy    (this, eMap, dims);
		if(dt instanceof Float32)   return new Float32Strategy  (this, eMap, dims);
		if(dt instanceof Float64)   return new Float64Strategy  (this, eMap, dims);
		if(dt instanceof Decimal)   return new DecimalStrategy  (this, eMap, dims);

		return null;
	}

	@Override protected void generate() {
		String intervalString = randomGenerator.getInterval();
		logger.trace("intervalString {}", intervalString);
		long interval = this.interval != -2 ? this.interval : GlobalUtil.getMilisecondsFromTime(RandomUtil.replaceRandoms(parent.replaceVariables(intervalString), true, true));
		logger.trace("wait {} ms for next execution", interval);
		if(interval != -1) sleep(interval);

		List<Integer> dims = this.dims != null ? this.dims : GlobalUtil.getDimensions(RandomUtil.replaceRandoms(parent.replaceVariables(randomGenerator.getDimensions()), true, true));
		strategy.generate(dims);
	}

	@Override protected void execute(PortMap portIdData, int grp) { }
}
