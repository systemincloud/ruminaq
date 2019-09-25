/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.diagram;

import java.util.Locale;
import java.util.stream.Stream;

/**
 * Cast Project Explorer element to enum.
 *
 * @author Marek Jagielski
 */
enum Selectable {
  PROJECT, FOLDER, FILE, PACKAGEFRAGMENT, OTHER;

  public static Selectable valueOf(Class<?> clazz) {
    String name = clazz.getSimpleName().toUpperCase(Locale.ENGLISH);
    return Stream.of(Selectable.values()).filter(s -> s.name().equals(name))
        .findFirst().orElse(OTHER);
  }
}