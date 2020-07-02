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

import java.security.Key;

import javax.crypto.Cipher;

public class CryptoUtil {

  public static String decryptRSA(byte[] text, Key key) {
    byte[] dectyptedText = null;
    try {
      final Cipher cipher = Cipher.getInstance("RSA");
      cipher.init(Cipher.DECRYPT_MODE, key);
      dectyptedText = cipher.doFinal(text);

    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return new String(dectyptedText);
  }

  public static byte[] encryptRSA(String rand, Key key) {
    byte[] cipherText = null;
    try {
      final Cipher cipher = Cipher.getInstance("RSA");
      cipher.init(Cipher.ENCRYPT_MODE, key);
      cipherText = cipher.doFinal(rand.getBytes());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return cipherText;
  }
}
