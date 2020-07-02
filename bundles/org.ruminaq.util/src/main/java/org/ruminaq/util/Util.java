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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class Util {

  public static <T> T[] concat(T[] first, T second) {
    T[] result = Arrays.copyOf(first, first.length + 1);
    result[result.length - 1] = second;
    return result;
  }

  public static <T> T[] concat(T[] first, T[] second) {
    T[] result = Arrays.copyOf(first, first.length + second.length);
    System.arraycopy(second, 0, result, first.length, second.length);
    return result;
  }

  public static byte[] objectToBytes(Object object) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream os;
    try {
      os = new ObjectOutputStream(baos);
      os.writeObject(object);
    } catch (IOException e) {
    }

    return baos.toByteArray();
  }

  public static int findFreeLocalPort(int startPort) {
    int port = 0;
    int lastPort = startPort;
    while (port == 0) {
      lastPort++;
      ServerSocket socket = null;
      try {
        socket = new ServerSocket(lastPort);
      } catch (IOException e) {
        continue;
      } finally {
        if (socket != null)
          try {
            socket.close();
          } catch (IOException e) {
            /* e.printStackTrace(); */ }
      }
      port = lastPort;
    }
    return port;
  }

  private static TransformerFactory factory = TransformerFactory.newInstance();

  public static String transform(String xml, String xsl, final String key,
      final String value) throws TransformerException {
    return transform(xml, xsl, new HashMap<String, String>() {
      private static final long serialVersionUID = 1L;
      {
        put(key, value);
      }
    });
  }

  public static String transform(String xml, String xsl,
      Map<String, String> params) throws TransformerException {
    String resultXml = xml;
    StringWriter writer = new StringWriter();
    Source xmlDoc = new StreamSource(
        new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    Source xslDoc = new StreamSource(
        new ByteArrayInputStream(xsl.getBytes(StandardCharsets.UTF_8)));
    Result result = new StreamResult(writer);
    Transformer trans = factory.newTransformer(xslDoc);
    trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    if (params != null) {
      params.forEach((k, v) -> {
        trans.setParameter(k, v);
      });
    }
    trans.transform(xmlDoc, result);
    resultXml = writer.toString();
    return resultXml;
  }
}
