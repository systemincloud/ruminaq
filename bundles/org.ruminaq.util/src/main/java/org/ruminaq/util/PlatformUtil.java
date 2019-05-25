package org.ruminaq.util;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;

public final class PlatformUtil {

  private PlatformUtil() {
  }

  public static Bundle getBundle(Class<?> clazz) {
    return FrameworkUtil.getBundle(clazz);
  }

  public static Version getBundleVersion(Class<?> clazz) {
    return getBundle(clazz).getVersion();
  }

}
