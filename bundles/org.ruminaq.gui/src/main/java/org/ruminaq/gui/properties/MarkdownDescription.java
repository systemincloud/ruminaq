/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.properties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.util.EclipseUtil;
import org.slf4j.Logger;
import winterwell.markdown.pagemodel.MarkdownPage;

/**
 * Common methods for PropertyDescriptionExtension.
 *
 * @author Marek Jagielski
 */
public interface MarkdownDescription {

  static final Logger LOGGER = ModelerLoggerFactory
      .getLogger(MarkdownDescription.class);

  static final Pattern IMG_PATTERN = Pattern
      .compile("<img src=\"([^[\"|:]]*)[^/]*/>");

  /**
   * Util method that reads bundle resource in markdown format and converts it
   * to html.
   *
   * @param bundleClass class in bundle
   * @param path        resource path in bundle
   * @return html
   */
  default String getEntry(Class<?> bundleClass, String path) {
    if (path.contains("://")) {
      return "";
    }
    String html = new MarkdownPage(
        EclipseUtil.resourceFromBundle(bundleClass, path)
            .map(is -> new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8)))
            .map(BufferedReader::lines).orElseGet(Stream::empty)
            .collect(Collectors.joining("\n"))).html();
    return replaceImagesWithBase64(bundleClass,
        path.substring(0, path.lastIndexOf('/')), html);
  }

  /**
   * Util method that replace from html references to resource images with
   * Base64 encoded inline image.
   *
   * @param bundleClass class in bundle
   * @param basePath    reference path
   * @param html        document with images
   * @return html with embedded images
   */
  default String replaceImagesWithBase64(Class<?> bundleClass, String basePath,
      String html) {
    Matcher m = IMG_PATTERN.matcher(html);
    StringBuffer sb = new StringBuffer(html.length());
    while (m.find()) {
      EclipseUtil.resourceFromBundle(bundleClass, basePath + "/" + m.group(1))
          .ifPresent((InputStream is) -> {
            try {
              byte[] bytes = new byte[is.available()];
              is.read(bytes);
              m.appendReplacement(sb,
                  Matcher.quoteReplacement("<img src=\"data:image/png;base64, "
                      + Base64.getEncoder().encodeToString(bytes) + "\"/>"));
            } catch (IOException e) {
              LOGGER.error("Can't read image {}", m.group(1), e);
            }
          });
    }
    m.appendTail(sb);
    return sb.toString();
  }

}
