/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.randomgenerator.impl.strategy;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EMap;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.data.TextI;
import org.ruminaq.tasks.randomgenerator.impl.RandomGeneratorI;
import org.ruminaq.tasks.randomgenerator.model.Port;
import org.ruminaq.util.RandomUtil;

import ch.qos.logback.classic.Logger;

public class TextStrategy extends RandomGeneratorStrategy {

  private final Logger logger = RunnerLoggerFactory
      .getLogger(TextStrategy.class);

  public enum TextType {
    ALPHANUMERIC, ALPHABETIC, NUMERIC;
  }

  public enum TextCase {
    ANY, LOWER, UPPER;
  }

  public static final String TEXT_TYPE = "TEXT_TYPE";
  public static final String TEXT_CASE = "TEXT_CASE";
  public static final String TEXT_LENGTH = "TEXT_LENGTH";
  public static final int DEFAULT_TEXT_LENGTH = 5;

  private String textType;
  private String textCase;
  private String textLength;
  private int length = -1;

  public TextStrategy(RandomGeneratorI task, EMap<String, String> eMap) {
    super(task);
    textType = eMap.get(TEXT_TYPE);
    textCase = eMap.get(TEXT_CASE);
    textLength = eMap.get(TEXT_LENGTH);
    if (!RandomUtil
        .containsRandom(task.getParent().replaceVariables(textLength)))
      length = Integer.parseInt(task.getParent().replaceVariables(textLength));
  }

  @Override
  public void generate(List<Integer> dims) {
    logger.trace("generating Text");
    int length = this.length != -1 ? this.length
        : Integer.parseInt(RandomUtil.replaceRandoms(
            task.getParent().replaceVariables(textLength), true, true));

    int n = 1;
    for (Integer i : dims)
      n *= i;
    List<String> values = new ArrayList<>(n);

    if (TextType.ALPHANUMERIC.toString().equals(textType))
      for (int i = 0; i < n; i++)
        values.add(TextStrategyUtil.generateRandomString(length,
            TextStrategyUtil.Mode.ALPHANUMERIC, textCase));
    else if (TextType.ALPHABETIC.toString().equals(textType))
      for (int i = 0; i < n; i++)
        values.add(TextStrategyUtil.generateRandomString(length,
            TextStrategyUtil.Mode.ALPHA, textCase));
    else if (TextType.NUMERIC.toString().equals(textType))
      for (int i = 0; i < n; i++)
        values.add(TextStrategyUtil.generateRandomString(length,
            TextStrategyUtil.Mode.NUMERIC, textCase));

    task.putData(Port.OUT, new TextI(values, dims));
  }
}
