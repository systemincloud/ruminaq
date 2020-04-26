package org.ruminaq.model.desc;

import java.lang.reflect.Field;

import org.ruminaq.model.ruminaq.PortInfo;

public class PortsDescrUtil {

  public static String getName(PortsDescr pd) {
    try {
      Field f = pd.getClass().getField(pd.toString());
      PortInfo in = f.getAnnotation(PortInfo.class);
      if (in != null)
        return in.id();
    } catch (NoSuchFieldException | SecurityException e) {
    }
    return "";
  }

  public static int getGroup(PortsDescr pd) {
    try {
      Field f = pd.getClass().getField(pd.toString());
      PortInfo in = f.getAnnotation(PortInfo.class);
      if (in != null)
        return in.group();
    } catch (NoSuchFieldException | SecurityException e) {
    }
    return -1;
  }
}
