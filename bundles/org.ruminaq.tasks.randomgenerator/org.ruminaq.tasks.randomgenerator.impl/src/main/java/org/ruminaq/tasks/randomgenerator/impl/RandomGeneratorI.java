/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.randomgenerator.impl;

import java.util.List;

import org.eclipse.emf.common.util.EMap;
import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.model.ruminaq.dt.Bool;
import org.ruminaq.model.ruminaq.dt.Complex32;
import org.ruminaq.model.ruminaq.dt.Complex64;
import org.ruminaq.model.ruminaq.dt.Control;
import org.ruminaq.model.ruminaq.dt.Decimal;
import org.ruminaq.model.ruminaq.dt.Float32;
import org.ruminaq.model.ruminaq.dt.Float64;
import org.ruminaq.model.ruminaq.dt.Int32;
import org.ruminaq.model.ruminaq.dt.Int64;
import org.ruminaq.model.ruminaq.dt.Text;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.runner.impl.GeneratorI;
import org.ruminaq.runner.impl.PortMap;
import org.ruminaq.tasks.randomgenerator.impl.service.RandomGeneratorServiceManager;
import org.ruminaq.tasks.randomgenerator.impl.strategy.BoolStrategy;
import org.ruminaq.tasks.randomgenerator.impl.strategy.Complex32Strategy;
import org.ruminaq.tasks.randomgenerator.impl.strategy.Complex64Strategy;
import org.ruminaq.tasks.randomgenerator.impl.strategy.ControlStrategy;
import org.ruminaq.tasks.randomgenerator.impl.strategy.DecimalStrategy;
import org.ruminaq.tasks.randomgenerator.impl.strategy.Float32Strategy;
import org.ruminaq.tasks.randomgenerator.impl.strategy.Float64Strategy;
import org.ruminaq.tasks.randomgenerator.impl.strategy.Int32Strategy;
import org.ruminaq.tasks.randomgenerator.impl.strategy.Int64Strategy;
import org.ruminaq.tasks.randomgenerator.impl.strategy.RandomGeneratorStrategy;
import org.ruminaq.tasks.randomgenerator.impl.strategy.TextStrategy;
import org.ruminaq.tasks.randomgenerator.model.randomgenerator.RandomGenerator;
import org.ruminaq.util.GlobalUtil;
import org.ruminaq.util.RandomUtil;

import ch.qos.logback.classic.Logger;

/**
 *
 * @author Marek Jagielski
 */
public class RandomGeneratorI extends GeneratorI {

  private final Logger logger = RunnerLoggerFactory
      .getLogger(RandomGeneratorI.class);

  private RandomGenerator randomGenerator;

  private List<Integer> dims;
  private long interval = -2;

  private RandomGeneratorStrategy strategy;

  public RandomGeneratorI(EmbeddedTaskI parent, Task task) {
    super(parent, task);
    setGenerator(true);
    this.randomGenerator = (RandomGenerator) task;
    DataType dataType = randomGenerator.getDataType();

    if (!RandomUtil.containsRandom(
        parent.replaceVariables(randomGenerator.getDimensions()))) {
      this.dims = GlobalUtil.getDimensions(
          parent.replaceVariables(randomGenerator.getDimensions()));
      logger.trace("dimensions doesn't contain random. There are {} dimensions",
          this.dims.size());
    }

    if (!RandomUtil.containsRandom(
        parent.replaceVariables(randomGenerator.getInterval()))) {
      this.interval = GlobalUtil.getMilisecondsFromTime(
          parent.replaceVariables(randomGenerator.getInterval()));
      logger.trace("interval doesn't contain random. There are {} miliseconds",
          this.interval);
    }

    this.strategy = getStrategy(dataType, randomGenerator.getSpecific(),
        this.dims);
    logger.trace("chosen strategy is {}",
        this.strategy.getClass().getSimpleName());
  }

  private RandomGeneratorStrategy getStrategy(DataType dt,
      EMap<String, String> eMap, List<Integer> dims) {
    logger.trace("look for strategy for {}", dt);
    RandomGeneratorStrategy rgs = RandomGeneratorServiceManager.INSTANCE
        .getStrategy(dt, eMap);
    if (rgs != null)
      return rgs;

    if (dt instanceof Text)
      return new TextStrategy(this, eMap);
    if (dt instanceof Bool)
      return new BoolStrategy(this, eMap);
    if (dt instanceof Complex32)
      return new Complex32Strategy(this, eMap);
    if (dt instanceof Complex64)
      return new Complex64Strategy(this, eMap);
    if (dt instanceof Control)
      return new ControlStrategy(this, eMap);
    if (dt instanceof Int32)
      return new Int32Strategy(this, eMap, dims);
    if (dt instanceof Int64)
      return new Int64Strategy(this, eMap, dims);
    if (dt instanceof Float32)
      return new Float32Strategy(this, eMap, dims);
    if (dt instanceof Float64)
      return new Float64Strategy(this, eMap, dims);
    if (dt instanceof Decimal)
      return new DecimalStrategy(this, eMap, dims);

    return null;
  }

  @Override
  protected void generate() {
    String intervalString = randomGenerator.getInterval();
    logger.trace("intervalString {}", intervalString);
    long interval = this.interval != -2 ? this.interval
        : GlobalUtil.getMilisecondsFromTime(RandomUtil.replaceRandoms(
            parent.replaceVariables(intervalString), true, true));
    logger.trace("wait {} ms for next execution", interval);
    if (interval != -1)
      sleep(interval);

    List<Integer> dims = this.dims != null ? this.dims
        : GlobalUtil.getDimensions(RandomUtil.replaceRandoms(
            parent.replaceVariables(randomGenerator.getDimensions()), true,
            true));
    strategy.generate(dims);
  }

  @Override
  protected void execute(PortMap portIdData, int grp) {
  }
}
