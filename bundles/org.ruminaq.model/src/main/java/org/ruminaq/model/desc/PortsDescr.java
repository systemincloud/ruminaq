/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.model.desc;

import java.util.Optional;

import org.ruminaq.model.ruminaq.PortInfo;
import org.ruminaq.util.Result;

public interface PortsDescr {
  String name();

  default String getId() {
    return Optional.of(this.name())
        .map(name -> Result.attempt(() -> this.getClass().getField(name)))
        .flatMap(r -> Optional.ofNullable(r.orElse(null)))
        .map(f -> f.getAnnotation(PortInfo.class)).map(PortInfo::id)
        .orElse(null);
  }

  default int getGroup() {
    return Optional.of(this.name())
        .map(name -> Result.attempt(() -> this.getClass().getField(name)))
        .flatMap(r -> Optional.ofNullable(r.orElse(null)))
        .map(f -> f.getAnnotation(PortInfo.class)).map(PortInfo::group)
        .orElse(-1);
  }
}
