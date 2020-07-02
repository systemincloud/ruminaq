/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.randomgenerator.impl.strategy;

import java.util.Locale;

import org.ruminaq.tasks.randomgenerator.impl.strategy.TextStrategy.TextCase;

public class TextStrategyUtil {

  public static enum Mode {
    ALPHA, ALPHANUMERIC, NUMERIC
  }

  public static String generateRandomString(int length, Mode mode,
      String textCase) {

    StringBuffer buffer = new StringBuffer();
    String characters = "";

    switch (mode) {
      case ALPHA:
        characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        break;
      case ALPHANUMERIC:
        characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        break;
      case NUMERIC:
        characters = "1234567890";
        break;
    }

    int charactersLength = characters.length();

    for (int i = 0; i < length; i++) {
      double index = Math.random() * charactersLength;
      buffer.append(characters.charAt((int) index));
    }
    String ret = buffer.toString();

    if (TextCase.LOWER.toString().equals(textCase))
      ret = ret.toLowerCase(Locale.US);
    if (TextCase.UPPER.toString().equals(textCase))
      ret = ret.toUpperCase(Locale.US);

    return ret;
  }
}
