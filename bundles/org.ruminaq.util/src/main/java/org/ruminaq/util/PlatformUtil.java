/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.util;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;

/**
 * Eclipse platform utils.
 *
 * @author Marek Jagielski
 */
public final class PlatformUtil {

  private PlatformUtil() {
  }

  private static Bundle getBundle(Class<?> clazz) {
    return FrameworkUtil.getBundle(clazz);
  }

  public static Version getBundleVersion(Class<?> clazz) {
    return getBundle(clazz).getVersion();
  }

  public static String getBundleSymbolicName(Class<?> clazz) {
    return getBundle(clazz).getSymbolicName();
  }

}
