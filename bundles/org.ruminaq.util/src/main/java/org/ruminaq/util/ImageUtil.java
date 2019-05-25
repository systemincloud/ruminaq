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
