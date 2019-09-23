package org.ruminaq.tasks.embeddedtask;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.FrameworkUtil;

public class Images {

  public enum K {
    IMG_EMBEDDEDTASK_PALETTE("/icons/palette.embeddedtask_main.png"),
    IMG_EMBEDDEDTASK_DIAGRAM_MAIN("/icons/diagram.embeddedtask_main.png"),
    IMG_EMBEDDEDTASK_DIAGRAM_TEST("/icons/diagram.embeddedtask_test.png");

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
