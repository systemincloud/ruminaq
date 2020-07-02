/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.randomgenerator.impl.strategy;

import org.eclipse.emf.common.util.EMap;
import org.ruminaq.tasks.randomgenerator.impl.RandomGeneratorI;
import org.ruminaq.tasks.randomgenerator.impl.distributions.Distributon;
import org.ruminaq.util.NumericUtil;

public abstract class RandomGeneratorComplexStrategy
    extends RandomGeneratorStrategy {

  public static final String COMPLEX_REPRESENTATION = "COMPLEX_DISTRIBUTION";
  public static final String RECTANGULAR_REPRESENTATION = "RECTANGULAR";
  public static final String POLAR_REPRESENTATION = "POLAR";
  public static final String DEFAULT_REPRESENTATION = RECTANGULAR_REPRESENTATION;

  public static final String COMPLEX_A = "COMPLEX_A";
  public static final String COMPLEX_B = "COMPLEX_B";
  public static final String COMPLEX_MAG = "COMPLEX_MAG";
  public static final String COMPLEX_ARG = "COMPLEX_ARG";

  public static final String COMPLEX_DEFAULT_A = "%u[0,1]";
  public static final String COMPLEX_DEFAULT_B = "%u[0,1]";
  public static final String COMPLEX_DEFAULT_MAG = "%u[0,1]";
  public static final String COMPLEX_DEFAULT_ARG = "%u[0,6.28]";

  private boolean rect = true;

  private Distributon aDistribution;
  private double aValue;
  private Distributon bDistribution;
  private double bValue;
  private Distributon mDistribution;
  private double mValue;
  private Distributon gDistribution;
  private double gValue;

  public RandomGeneratorComplexStrategy(RandomGeneratorI task,
      EMap<String, String> eMap) {
    super(task);
    String rep = eMap.get(COMPLEX_REPRESENTATION);
    this.rect = rep == null || rep.equals(RECTANGULAR_REPRESENTATION) ? true
        : false;
    if (rect) {
      String a = eMap.get(COMPLEX_A);
      if (a == null)
        a = COMPLEX_DEFAULT_A;
      if (isValue(a))
        this.aValue = Double.parseDouble(a);
      else
        this.aDistribution = RandomGeneratorNumericStrategy
            .getNumericStrategy(a);
      String b = eMap.get(COMPLEX_B);
      if (b == null)
        b = COMPLEX_DEFAULT_B;
      if (isValue(b))
        this.bValue = Double.parseDouble(b);
      else
        this.bDistribution = RandomGeneratorNumericStrategy
            .getNumericStrategy(b);
    } else {
      String m = eMap.get(COMPLEX_MAG);
      if (m == null)
        m = COMPLEX_DEFAULT_MAG;
      if (isValue(m))
        this.mValue = Math.abs(Double.parseDouble(m));
      else
        this.mDistribution = RandomGeneratorNumericStrategy
            .getNumericStrategy(m);
      String g = eMap.get(COMPLEX_ARG);
      if (g == null)
        g = COMPLEX_DEFAULT_ARG;
      if (isValue(g))
        this.gValue = Double.parseDouble(g);
      else
        this.gDistribution = RandomGeneratorNumericStrategy
            .getNumericStrategy(g);
    }
  }

  private boolean isValue(String value) {
    return NumericUtil.isOneDimNumeric(value);
  }

  public double[] getNextComplex() {
    if (rect)
      return new double[] {
          aDistribution != null ? aDistribution.getNext() : aValue,
          bDistribution != null ? bDistribution.getNext() : bValue };
    else {
      double m = mDistribution != null ? getPositive(mDistribution) : mValue;
      double g = gDistribution != null ? gDistribution.getNext() : gValue;
      return new double[] { m * Math.cos(g), m * Math.sin(g) };
    }
  }

  private double getPositive(Distributon distribution) {
    double ret = 0;
    while ((ret = distribution.getNext()) < 0)
      ;
    return ret;
  }
}
