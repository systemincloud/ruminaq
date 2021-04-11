/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.model.ruminaq;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.ruminaq.util.GlobalUtil;

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
    return value.matches("^\\s*\\[\\s*" + numericExp + "\\s*\\]\\s*$");
  }

  public static boolean isOneRowTableNumeric(String value) {
    return value.matches("^\\s*\\[" + numericRow + "\\]\\s*$");
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
    return value.matches("^" + integer + "\\s*$");
  }

  public static boolean isOneDimPositiveIntegerAlsoGV(String value) {
    return Optional.of(value)
        .filter(Predicate.not(GlobalUtil::isGlobalVariable))
        .map(NumericUtil::isOneDimPositiveInteger).orElse(Boolean.TRUE);
  }

  public static boolean isOneDimPositiveInteger(String value) {
    return value.matches("^" + intPositive + "\\s*$");
  }

  public static boolean isMultiDimsIntegerAlsoGV(String value) {
    return Optional.of(value)
        .filter(Predicate.not(GlobalUtil::isGlobalVariable))
        .map(NumericUtil::isMultiDimsInteger).orElse(Boolean.TRUE);
  }

  public static boolean isOneElementTableInteger(String value) {
    return value.matches("^\\s*\\[\\s*" + integer + "\\s*\\]\\s*$");
  }

  public static boolean isOneRowTableInteger(String value) {
    return value.matches("^\\s*\\[" + integerRow + "\\]\\s*$");
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
    return value.matches("^" + complex + "\\s*$");
  }

  public static boolean isMultiDimsComplexAlsoGV(String value) {
    if (GlobalUtil.isGlobalVariable(value))
      return true;
    return isMultiDimsComplex(value);
  }

  public static boolean isOneElementTableComplex(String value) {
    return value.matches("^\\s*\\[\\s*" + complex + "\\s*\\]\\s*$");
  }

  public static boolean isOneRowTableComplex(String value) {
    return value.matches("^\\s*\\[" + complexRow + "\\]\\s*$");
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
    return Optional.of(value)
        .filter(Predicate.not(GlobalUtil::isGlobalVariable))
        .map(NumericUtil::isMultiDimsBool).orElse(Boolean.TRUE);
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
    return value.matches("^\\s*\\[" + boolRow + "\\]\\s*$");
  }

  private static boolean isOneElementTableBool(String value) {
    return value.matches("^\\s*\\[\\s*" + bool + "\\s*\\]\\s*$");
  }

  private static boolean isOneDimBool(String value) {
    return value.matches("^" + bool + "\\s*$");
  }

  private static boolean checkTableSize(String value) {
//    String tmp = value.replace(",", "");
//    tmp = tmp.replace("[", "").replace("]", "");
//    tmp = CharMatcher.whitespace().trimAndCollapseFrom(tmp, ' ');
//    tmp = tmp.replace(" ;", ";").replace("; ", ";");
//    int semicolonIdx = 0;
//    int newSemicolonIdx = 0;
//    int cols = -1;
//    do {
//      newSemicolonIdx = tmp.indexOf(";", semicolonIdx);
//      if (newSemicolonIdx == -1)
//        newSemicolonIdx = tmp.length() - 1;
//      int n = CharMatcher.whitespace()
//          .countIn(tmp.substring(semicolonIdx, newSemicolonIdx));
//      if (cols == -1)
//        cols = n;
//      else if (n != cols)
//        return false;
//      semicolonIdx = newSemicolonIdx + 1;
//    } while (newSemicolonIdx < tmp.length() - 1);
    return true;
  }
}
