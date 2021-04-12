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

  private static final String numPositive = "\\d+([.]\\d+)?";
  private static final String numeric = "[+-]?" + numPositive;
  private static final String numPosExp = numPositive + "(e[+-]?\\d+)?";
  private static final String numericExp = "[+-]?" + numPositive;
  private static final String numericRow = "(\\s*" + numericExp
      + "\\s*(,)?\\s*)*(" + numericExp + "\\s*)";
  private static final String numericRowCol = "(" + numericRow + "(;)?\\s*)*("
      + numericRow + ")";

  private static final String integer = "[+-]?\\d+";
  private static final String intPositive = "\\d+";
  private static final String integerRow = "(\\s*" + integer + "\\s*(,)?\\s*)*("
      + integer + "\\s*)";
  private static final String integerRowCol = "(" + integerRow + "(;)?\\s*)*("
      + integerRow + ")";

  private static final String imagPos = numPositive + "i";
  private static final String imag = "[+-]?" + imagPos;
  private static final String complex = "(" + numericExp + "|" + imag + "|"
      + "(" + numericExp + "\\s*[+-]\\s*" + imagPos + "))";
  private static final String complexRow = "(\\s*" + complex + "\\s*(,)?\\s*)*("
      + complex + "\\s*)";
  private static final String complexRowCol = "(" + complexRow + "(;)?\\s*)*("
      + complexRow + ")";

  private static final String bool = "(true|false)";
  private static final String boolRow = "(\\s*" + bool + "\\s*(,)?\\s*)*("
      + bool + "\\s*)";
  private static final String boolRowCol = "(" + boolRow + "(;)?\\s*)*("
      + boolRow + ")";

  public static boolean isOneDimNumeric(String value) {
    return value.matches("^" + numericExp + "\\s*$");
  }

  public static boolean isOneDimNumericAlsoGV(String value) {
    return Optional.of(value)
        .filter(Predicate.not(GlobalUtil::isGlobalVariable))
        .map(NumericUtil::isOneDimNumeric).orElse(Boolean.TRUE);
  }

  public static boolean isOneElementTableNumeric(String value) {
    return value.matches("^\\s*\\[\\s*" + numericExp + "\\s*\\]\\s*$");
  }

  public static boolean isOneRowTableNumeric(String value) {
    return value.matches("^\\s*\\[" + numericRow + "\\]\\s*$");
  }

  public static boolean isMutliDimsNumericAlsoGV(String value) {
    return Optional.of(value)
        .filter(Predicate.not(GlobalUtil::isGlobalVariable))
        .map(NumericUtil::isMultiDimsNumeric).orElse(Boolean.TRUE);
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
    return value.trim().replace("[", "").replace("]", "").replace(";", ",")
        .replace(" ", "").split(",");
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
    return Optional.of(value)
        .filter(Predicate.not(GlobalUtil::isGlobalVariable))
        .map(NumericUtil::isMultiDimsComplex).orElse(Boolean.TRUE);
  }

  public static boolean isOneElementTableComplex(String value) {
    return value.matches("^\\s*\\[\\s*" + complex + "\\s*\\]\\s*$");
  }

  public static boolean isOneRowTableComplex(String value) {
    return value.matches("^\\s*\\[" + complexRow + "\\]\\s*$");
  }

  private static boolean isMultiDimsComplex(String value) {
    return Optional.of(value)
        .filter(Predicate.not(NumericUtil::isOneDimComplex))
        .filter(Predicate.not(NumericUtil::isOneElementTableComplex))
        .filter(Predicate.not(NumericUtil::isOneRowTableComplex))
        .filter(Predicate
            .not(v -> v.matches("^\\s*\\[" + complexRowCol + "\\]\\s*$")
                && checkTableSize(v)))
        .map(v -> Boolean.FALSE).orElse(Boolean.TRUE);
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
