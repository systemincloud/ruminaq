/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.properties;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.osgi.framework.FrameworkUtil;
import org.ruminaq.util.Result;

public abstract class AbstractDescription {

  protected String getEntry(Class<?> bundleClass, String path) {
    return Optional.of(FrameworkUtil.getBundle(bundleClass))
        .map(b -> b.getEntry(path))
        .map(url -> Result.attempt(() -> url.openConnection()))
        .map(r -> r.orElse(null)).filter(Objects::nonNull)
        .map(urlConn -> Result.attempt(() -> urlConn.getInputStream()))
        .map(r -> r.orElse(null)).filter(Objects::nonNull)
        .map(is -> new BufferedReader(
            new InputStreamReader(is, StandardCharsets.UTF_8)))
        .map(BufferedReader::lines).orElseGet(Stream::empty)
        .collect(Collectors.joining("\n"));
  }

}
