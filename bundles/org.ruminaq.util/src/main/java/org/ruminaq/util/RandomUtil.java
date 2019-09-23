/*
 * (C) Copyright 2018 Marek Jagielski.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ruminaq.util;

import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RandomUtil {

  public static final String NORMAL = "\\%n\\[-?\\d+(\\.\\d+)?\\,\\d+(\\.\\d+)?]";
  public static final String UNIFORM = "\\%u\\[-?\\d+(\\.\\d+)?\\,-?\\d+(\\.\\d+)?]";

  public static boolean isRandomAlsoGV(String text) {
    if (GlobalUtil.isGlobalVariable(text))
      return true;
    return isRandom(text);
  }

  public static boolean isRandom(String text) {
    if (text.matches(NORMAL))
      return true;
    if (text.matches(UNIFORM))
      return true;
    return false;
  }

  public static boolean containsRandom(String string) {
    if (Pattern.compile(NORMAL).matcher(string).find())
      return true;
    if (Pattern.compile(UNIFORM).matcher(string).find())
      return true;
    return false;
  }

  public static String replaceRandoms(String string, boolean onlyInt,
      boolean onlyPositive) {
    String ret = string;
    String tmp;
    //
    // NORMAL
    //
    StringBuffer sb = new StringBuffer();
    Matcher matcher = Pattern.compile(NORMAL).matcher(ret);
    int lastIdx = 0;
    while (matcher.find()) {
      int start = matcher.start();
      if (start != lastIdx)
        sb.append(ret.substring(lastIdx, start));
      lastIdx = matcher.end();
      String expr = matcher.group();
      int coma = expr.indexOf(",");
      float mean = Float.parseFloat(expr.substring(3, coma));
      float stdDev = Float
          .parseFloat(expr.substring(coma + 1, expr.length() - 1));

      double value = 0;
      do {
        value = ThreadLocalRandom.current().nextGaussian() * stdDev + mean;
      } while (onlyPositive && value < 0);

      if (onlyInt)
        sb.append(Math.round(value));
      else
        sb.append(value);
    }
    sb.append(ret.substring(lastIdx));
    // -------------------------------

    tmp = sb.toString();
    if (!"".equals(tmp))
      ret = tmp;

    //
    // UNIFORM
    //
    sb = new StringBuffer();
    matcher = Pattern.compile(UNIFORM).matcher(ret);
    lastIdx = 0;
    while (matcher.find()) {
      int start = matcher.start();
      if (start != lastIdx)
        sb.append(ret.substring(lastIdx, start));
      lastIdx = matcher.end();
      String expr = matcher.group();
      int coma = expr.indexOf(",");
      float low = Float.parseFloat(expr.substring(3, coma));
      float heigh = Float
          .parseFloat(expr.substring(coma + 1, expr.length() - 1));
      if (low < 0)
        low = 0;
      if (Float.compare(heigh, low) > 0) {
        double value = ThreadLocalRandom.current().nextDouble() * (heigh - low)
            + low;
        if (onlyInt)
          sb.append(Math.round(value));
        else
          sb.append(value);
      } else
        throw new RuntimeException("Incorrect bounderies");
    }
    sb.append(ret.substring(lastIdx));
    // -------------------------------

    tmp = sb.toString();
    if (!"".equals(tmp))
      ret = tmp;
    return ret;
  }

  public static String replaceAllRandomsWith(String string, String rep) {
    String tmp = string.replaceAll(NORMAL, rep);
    tmp = tmp.replaceAll(UNIFORM, rep);
    return tmp;
  }

}
