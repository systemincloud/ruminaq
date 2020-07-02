/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
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

import java.util.LinkedList;
import java.util.List;

import com.google.common.base.CharMatcher;

public class GlobalUtil {

  public static final String GV = "\\$\\{[^\\},]+\\}";

  public static boolean isGlobalVariable(String value) {
    return value.matches(GV);
  }

  /*
   * Example: '4, %n[5,6] , 3, 4,5'
   */
  public static boolean isDimensionsAlsoGVandRand(String value) {
    String tmp = value.replaceAll(GV, "1");
    tmp = RandomUtil.replaceAllRandomsWith(tmp, "1");
    return isDimensions(tmp);
  }

  /*
   * Example: '4, ${xxx} , 3, 4,5'
   */
  public static boolean isDimensionsAlsoGV(String value) {
    String tmp = value.replaceAll(GV, "1");
    return isDimensions(tmp);
  }

  /*
   * Dimensions are integers separated by ',' Example: '4, 4 , 3, 4,5'
   */
  public static boolean isDimensions(String value) {
    if (value.matches(".*[0-9]\\s+[0-9].*"))
      return false;
    String tmp = value.replaceAll("\\s+", "");
    if (tmp.matches(".*,,.*"))
      return false;
    if (tmp.matches(",.*"))
      return false;
    if (tmp.matches("[0-9,]+"))
      return true;
    else
      return false;
  }

  public static boolean isIntegerAlsoGVandRand(String value) {
    String tmp = value.replaceAll(GV, "1");
    tmp = RandomUtil.replaceAllRandomsWith(tmp, "1");
    return isInteger(tmp);
  }

  public static boolean isIntegerAlsoGV(String value) {
    String tmp = value.replaceAll(GV, "1");
    return isInteger(tmp);
  }

  public static boolean isInteger(String value) {
    return value.matches("\\d+");
  }

  public static boolean isTimeAlsoGVandRand(String time) {
    if (GlobalUtil.isGlobalVariable(time))
      return true;
    String tmp = time.replaceAll(GV, "1");
    tmp = RandomUtil.replaceAllRandomsWith(tmp, "1");
    return isTime(tmp);
  }

  public static boolean isTimeAlsoGV(String time) {
    String tmp = time.replaceAll(GV, "1");
    return isTime(tmp);
  }

  public static boolean isTime(String time) {
    return time
        .matches("^((\\d+([.,]\\d+)?\\s*(ms|s|m|h|d)\\s*)+)|(\\s*inf\\s*)$");
  }

  /**
   * Examples of argument : '5ms', '6 ms', '7 s', '7.5s', '1h1s', '1 d 20h'.
   * 
   * @param time
   * @return
   */
  public static long getMilisecondsFromTime(String time) {
    if (!GlobalUtil.isTime(time))
      throw new ParseError("Can't understand written internval.");

    if (time.matches("\\s*inf\\s*"))
      return -1;

    Tokenizer tokenizer = new Tokenizer();
    tokenizer.add("\\d+([.,]\\d+)?\\s*ms\\s*", 1);
    tokenizer.add("\\d+([.,]\\d+)?\\s*s\\s*", 2);
    tokenizer.add("\\d+([.,]\\d+)?\\s*m\\s*", 3);
    tokenizer.add("\\d+([.,]\\d+)?\\s*h\\s*", 4);
    tokenizer.add("\\d+([.,]\\d+)?\\s*d\\s*", 5);

    long ret = 0;
    tokenizer.tokenize(time);
    for (Tokenizer.Token tok : tokenizer.getTokens()) {
      String tmp = tok.sequence.replaceFirst(",", ".");
      int digit = tmp.indexOf(".");
      tmp = CharMatcher.digit().retainFrom(tmp);
      if (digit > 0)
        tmp = new StringBuilder(tmp).insert(digit, ".").toString();
      float f = Float.valueOf(tmp);
      switch (tok.token) {
        case 1:
          ret += f;
          break;
        case 2:
          ret += f * 1000;
          break;
        case 3:
          ret += f * 60 * 1000;
          break;
        case 4:
          ret += f * 60 * 60 * 1000;
          break;
        case 5:
          ret += f * 24 * 60 * 60 * 1000;
          break;
      }
    }
    return ret;
  }

  public static List<Integer> getDimensions(String string) {
    String tmp = string.replaceAll("\\s+", "");
    String[] dims = tmp.split(",");
    List<Integer> ret = new LinkedList<>();
    for (String d : dims)
      if (!"".equals(d))
        ret.add(Integer.parseInt(d));
    return ret;
  }
}
