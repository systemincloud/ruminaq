/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.properties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.ruminaq.util.EclipseUtil;
import winterwell.markdown.pagemodel.MarkdownPage;

/**
 * Common methods for PropertyDescriptionExtension.
 *
 * @author Marek Jagielski
 */
public interface MarkdownDescription {

  default String getEntry(Class<?> bundleClass, String path) {
    if (!path.contains("://")) {
      String html = new MarkdownPage(
          EclipseUtil.resourceFromBundle(bundleClass, path)
              .map(is -> new BufferedReader(
                  new InputStreamReader(is, StandardCharsets.UTF_8)))
              .map(BufferedReader::lines).orElseGet(Stream::empty)
              .collect(Collectors.joining("\n"))).html();
      return replaceImagesWithBase64(bundleClass,
          path.substring(0, path.lastIndexOf('/')), html);
    } else {
      return "";
    }
  }

  default String replaceImagesWithBase64(Class<?> bundleClass, String basePath,
      String html) {
    Pattern pattern = Pattern.compile("<img src=\"([^[\"|:]]*)[^/]*/>");
    Matcher m = pattern.matcher(html);
    StringBuffer sb = new StringBuffer(html.length());
    while (m.find()) {
      EclipseUtil.resourceFromBundle(bundleClass, basePath + "/" + m.group(1))
          .ifPresent(is -> {
            try {
              byte[] bytes = new byte[is.available()];
              is.read(bytes);
              m.appendReplacement(sb,
                  Matcher.quoteReplacement("<img src=\"data:image/png;base64, "
                      + new String(Base64.getEncoder().encode(bytes))
                      + "\"/>"));
            } catch (IOException e) {
              e.printStackTrace();
            }
          });
    }
    m.appendTail(sb);
    return sb.toString();
  }

}
