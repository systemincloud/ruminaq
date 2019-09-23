package org.ruminaq.tasks.randomgenerator.impl.strategy;

import java.util.List;

import org.eclipse.emf.common.util.EMap;
import org.ruminaq.runner.impl.data.DataI;
import org.ruminaq.tasks.randomgenerator.impl.Port;
import org.ruminaq.tasks.randomgenerator.impl.distributions.Distributon;
import org.ruminaq.tasks.randomgenerator.impl.distributions.NormalDistribution;
import org.ruminaq.tasks.randomgenerator.impl.distributions.UniformDistribution;
import org.ruminaq.tasks.randomgenerator.impl.strategy.RandomGeneratorStrategy;
import org.ruminaq.tasks.randomgenerator.impl.RandomGeneratorI;
import org.ruminaq.util.RandomUtil;

public abstract class RandomGeneratorNumericStrategy
    extends RandomGeneratorStrategy {

  public static final String NUMERIC_DISTRIBUTION = "NUMERIC_DISTRIBUTION";
  public static final String DEFAULT_DISTRIBUTION = "%u[0,1]";

  protected Distributon distribution;
  protected DataI value;
  protected String textDistribution;

  public RandomGeneratorNumericStrategy(RandomGeneratorI task,
      EMap<String, String> eMap, List<Integer> dims) {
    super(task);
    String textDistribution = eMap.get(NUMERIC_DISTRIBUTION);
    if (textDistribution == null)
      textDistribution = DEFAULT_DISTRIBUTION;
    if (isValue(textDistribution)) {
      if (dims == null)
        this.textDistribution = textDistribution;
      else
        this.value = getDataOfValue(textDistribution, dims);
    } else
      this.distribution = getNumericStrategy(textDistribution);
  }

  protected abstract boolean isValue(String textDistribution);

  protected abstract DataI getDataOfValue(String textDistribution,
      List<Integer> dims);

  public static Distributon getNumericStrategy(String textDistribution) {
    if (textDistribution.matches(RandomUtil.NORMAL))
      return new NormalDistribution(textDistribution);
    else if (textDistribution.matches(RandomUtil.UNIFORM))
      return new UniformDistribution(textDistribution);
    else
      return null;
  }

  @Override
  public void generate(List<Integer> dims) {
    if (value != null)
      task.putData(Port.OUT, value);
    else if (textDistribution != null)
      task.putData(Port.OUT, getDataOfValue(textDistribution, dims));
    else
      generateRandom(dims);
  }

  protected abstract void generateRandom(List<Integer> dims);
}
