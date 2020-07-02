/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.util;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.FrameworkUtil;

public final class ImageUtil {

  private ImageUtil() {
  }

  public static ImageDescriptor getImageDescriptor(IImage img) {
    String pluginId = FrameworkUtil.getBundle(img.getClass()).getSymbolicName();
    return AbstractUIPlugin.imageDescriptorFromPlugin(pluginId, img.getPath());
  }

}
