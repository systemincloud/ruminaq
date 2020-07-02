/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.FrameworkUtil;

public class Images {

  public enum K {
    IMG_TOGGLE_BREAKPOINT("/icons/toggle_breakpoint.gif"),
    IMG_TOGGLE_BREAKPOINT_S("/icons/toggle_breakpoint_s.gif"),
    IMG_TOGGLE_BREAKPOINT_D("/icons/toggle_breakpoint_d.gif");

    public String path;

    K(String path) {
      this.path = path;
    }
  }

  static Map<String, String> images = new HashMap<String, String>() {
    private static final long serialVersionUID = 1L;
    {
      for (final K v : K.values())
        put(v.name(), FileLocator.find(FrameworkUtil.getBundle(this.getClass()),
            new Path(v.path), null).toString());
    }
  };

  public static Map<String, String> getImageKeyPath() {
    return images;
  }
}
