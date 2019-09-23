package org.ruminaq.tasks.pythontask;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.FrameworkUtil;

public class Images {

  public enum K {
    IMG_PYTHONTASK_PALETTE("/icons/palette.pythontask.png"),
    IMG_PYTHONTASK_DIAGRAM("/icons/diagram.pythontask.png");

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
