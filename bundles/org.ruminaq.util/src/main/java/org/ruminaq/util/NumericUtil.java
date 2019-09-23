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

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.CharMatcher;

public class NumericUtil {

  public static final String numPositive = "\\d+([.]\\d+)?";
  public static final String numeric = "[+-]?" + numPositive;
  public static final String numPosExp = numPositive + "(e[+-]?\\d+)?";
  public static final String numericExp = "[+-]?" + numPositive;
  public static final String numericRow = "(\\s*" + numericExp
      + "\\s*(,)?\\s*)*(" + numericExp + "\\s*)";
  public static final String numericRowCol = "(" + numericRow + "(;)?\\s*)*("
      + numericRow + ")";

  public static final String integer = "[+-]?\\d+";
  public static final String intPositive = "\\d+";
  public static final String integerRow = "(\\s*" + integer + "\\s*(,)?\\s*)*("
      + integer + "\\s*)";
  public static final String integerRowCol = "(" + integerRow + "(;)?\\s*)*("
      + integerRow + ")";

  public static final String imagPos = numPositive + "i";
  public static final String imag = "[+-]?" + imagPos;
  public static final String complex = "(" + numericExp + "|" + imag + "|" + "("
      + numericExp + "\\s*[+-]\\s*" + imagPos + "))";
  public static final String complexRow = "(\\s*" + complex + "\\s*(,)?\\s*)*("
      + complex + "\\s*)";
  public static final String complexRowCol = "(" + complexRow + "(;)?\\s*)*("
      + complexRow + ")";

  public static final String bool = "(true|false)";
  public static final String boolRow = "(\\s*" + bool + "\\s*(,)?\\s*)*(" + bool
      + "\\s*)";
  public static final String boolRowCol = "(" + boolRow + "(;)?\\s*)*("
      + boolRow + ")";

  public static boolean isOneDimNumeric(String value) {
    if (value.matches("^" + numericExp + "\\s*$"))
      return true;
    else
      return false;
  }

  public static boolean isOneDimNumericAlsoGV(String value) {
    if (GlobalUtil.isGlobalVariable(value))
      return true;
    return isOneDimNumeric(value);
  }

  public static boolean isOneElementTableNumeric(String value) {
    if (value.matches("^\\s*\\[\\s*" + numericExp + "\\s*\\]\\s*$"))
      return true;
    else
      return false;
  }

  public static boolean isOneRowTableNumeric(String value) {
    if (value.matches("^\\s*\\[" + numericRow + "\\]\\s*$"))
      return true;
    else
      return false;
  }

  public static boolean isMutliDimsNumericAlsoGV(String text) {
    if (GlobalUtil.isGlobalVariable(text))
      return true;
    return isMultiDimsNumeric(text);
  }

  public static boolean isMultiDimsNumeric(String value) {
    if (isOneDimNumeric(value))
      return true;
    if (isOneElementTableNumeric(value))
      return true;
    if (isOneRowTableNumeric(value))
      return true;
    if (value.matches("^\\s*\\[" + numericRowCol + "\\]\\s*$"))
      if (checkTableSize(value))
        return true;
    return false;
  }

  public static List<Integer> getMutliDimsNumericDimensions(String value) {
    List<Integer> dims = new LinkedList<>();
    if (isOneDimNumeric(value) || isOneElementTableNumeric(value))
      dims.add(1);
    else if (isOneRowTableNumeric(value))
      dims.add(StringUtils.countMatches(value, ",") + 1);
    else if (isMultiDimsNumeric(value)) {
      dims.add(StringUtils.countMatches(value, ";") + 1);
      dims.add(
          StringUtils.countMatches(value.substring(0, value.indexOf(";")), ",")
              + 1);
    }
    return dims;
  }

  public static List<Integer> getMultiDimsIntegerDimensions(String value) {
    List<Integer> dims = new LinkedList<>();
    if (isOneDimInteger(value) || isOneElementTableInteger(value))
      dims.add(1);
    else if (isOneRowTableInteger(value))
      dims.add(StringUtils.countMatches(value, ",") + 1);
    else if (isMultiDimsInteger(value)) {
      dims.add(StringUtils.countMatches(value, ";") + 1);
      dims.add(
          StringUtils.countMatches(value.substring(0, value.indexOf(";")), ",")
              + 1);
    }
    return dims;
  }

  public static List<Integer> getMultiDimsComplexDimensions(String value) {
    List<Integer> dims = new LinkedList<>();
    if (isOneDimComplex(value) || isOneElementTableComplex(value))
      dims.add(1);
    else if (isOneRowTableComplex(value))
      dims.add(StringUtils.countMatches(value, ",") + 1);
    else if (isMultiDimsComplex(value)) {
      dims.add(StringUtils.countMatches(value, ";") + 1);
      dims.add(
          StringUtils.countMatches(value.substring(0, value.indexOf(";")), ",")
              + 1);
    }
    return dims;
  }

  public static List<Integer> getMultiDimsBoolDimensions(String value) {
    List<Integer> dims = new LinkedList<>();
    if (isOneDimBool(value) || isOneElementTableBool(value))
      dims.add(1);
    else if (isOneRowTableBool(value))
      dims.add(StringUtils.countMatches(value, ",") + 1);
    else if (isMultiDimsBool(value)) {
      dims.add(StringUtils.countMatches(value, ";") + 1);
      dims.add(
          StringUtils.countMatches(value.substring(0, value.indexOf(";")), ",")
              + 1);
    }
    return dims;
  }

  public static String[] getMutliDimsValues(String value) {
    String tmp = value.trim().replace("[", "").replace("]", "")
        .replace(";", ",").replace(" ", "");
    return tmp.split(",");
  }

  public static boolean isOneDimInteger(String value) {
    if (value.matches("^" + integer + "\\s*$"))
      return true;
    else
      return false;
  }

  public static boolean isOneDimPositiveIntegerAlsoGV(String value) {
    if (GlobalUtil.isGlobalVariable(value))
      return true;
    return isOneDimPositiveInteger(value);
  }

  public static boolean isOneDimPositiveInteger(String value) {
    if (value.matches("^" + intPositive + "\\s*$"))
      return true;
    else
      return false;
  }

  public static boolean isMultiDimsIntegerAlsoGV(String value) {
    if (GlobalUtil.isGlobalVariable(value))
      return true;
    return isMultiDimsInteger(value);
  }

  public static boolean isOneElementTableInteger(String value) {
    if (value.matches("^\\s*\\[\\s*" + integer + "\\s*\\]\\s*$"))
      return true;
    else
      return false;
  }

  public static boolean isOneRowTableInteger(String value) {
    if (value.matches("^\\s*\\[" + integerRow + "\\]\\s*$"))
      return true;
    else
      return false;
  }

  public static boolean isMultiDimsInteger(String value) {
    if (isOneDimInteger(value))
      return true;
    if (isOneElementTableInteger(value))
      return true;
    if (isOneRowTableInteger(value))
      return true;
    if (value.matches("^\\s*\\[" + integerRowCol + "\\]\\s*$"))
      if (checkTableSize(value))
        return true;
    return false;
  }

  public static boolean isOneDimComplex(String value) {
    if (value.matches("^" + complex + "\\s*$"))
      return true;
    else
      return false;
  }

  public static boolean isMultiDimsComplexAlsoGV(String value) {
    if (GlobalUtil.isGlobalVariable(value))
      return true;
    return isMultiDimsComplex(value);
  }

  public static boolean isOneElementTableComplex(String value) {
    if (value.matches("^\\s*\\[\\s*" + complex + "\\s*\\]\\s*$"))
      return true;
    else
      return false;
  }

  public static boolean isOneRowTableComplex(String value) {
    if (value.matches("^\\s*\\[" + complexRow + "\\]\\s*$"))
      return true;
    else
      return false;
  }

  private static boolean isMultiDimsComplex(String value) {
    if (isOneDimComplex(value))
      return true;
    if (isOneElementTableComplex(value))
      return true;
    if (isOneRowTableComplex(value))
      return true;
    if (value.matches("^\\s*\\[" + complexRowCol + "\\]\\s*$"))
      if (checkTableSize(value))
        return true;
    return false;
  }

  public static boolean isMultiDimsBoolAlsoGV(String value) {
    if (GlobalUtil.isGlobalVariable(value))
      return true;
    return isMultiDimsBool(value);
  }

  private static boolean isMultiDimsBool(String value) {
    if (isOneDimBool(value))
      return true;
    if (isOneElementTableBool(value))
      return true;
    if (isOneRowTableBool(value))
      return true;
    if (value.matches("^\\s*\\[" + boolRowCol + "\\]\\s*$"))
      if (checkTableSize(value))
        return true;
    return false;
  }

  private static boolean isOneRowTableBool(String value) {
    if (value.matches("^\\s*\\[" + boolRow + "\\]\\s*$"))
      return true;
    else
      return false;
  }

  private static boolean isOneElementTableBool(String value) {
    if (value.matches("^\\s*\\[\\s*" + bool + "\\s*\\]\\s*$"))
      return true;
    else
      return false;
  }

  private static boolean isOneDimBool(String value) {
    if (value.matches("^" + bool + "\\s*$"))
      return true;
    else
      return false;
  }

  private static boolean checkTableSize(String value) {
    String tmp = value.replace(",", "");
    tmp = tmp.replace("[", "").replace("]", "");
    tmp = CharMatcher.whitespace().trimAndCollapseFrom(tmp, ' ');
    tmp = tmp.replace(" ;", ";").replace("; ", ";");
    int semicolonIdx = 0;
    int newSemicolonIdx = 0;
    int cols = -1;
    do {
      newSemicolonIdx = tmp.indexOf(";", semicolonIdx);
      if (newSemicolonIdx == -1)
        newSemicolonIdx = tmp.length() - 1;
      int n = CharMatcher.whitespace()
          .countIn(tmp.substring(semicolonIdx, newSemicolonIdx));
      if (cols == -1)
        cols = n;
      else if (n != cols)
        return false;
      semicolonIdx = newSemicolonIdx + 1;
    } while (newSemicolonIdx < tmp.length() - 1);
    return true;
  }
}
