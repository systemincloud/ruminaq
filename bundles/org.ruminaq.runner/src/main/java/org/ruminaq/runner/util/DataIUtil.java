package org.ruminaq.runner.util;

import java.nio.ByteBuffer;

import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.runner.impl.data.DataI;

public class DataIUtil {

  @SuppressWarnings("unchecked")
  public static Class<? extends DataI> getDataClassForDataType(
      DataType dataType, Class<?> clazz) {
    String name = dataType.getClass().getSimpleName();
    if (name.endsWith("Impl"))
      name = name.substring(0, name.length() - 4);
    Class<? extends DataI> dataClazz;
    try {
      dataClazz = (Class<? extends DataI>) Class
          .forName(clazz.getPackage().getName() + "." + name + "I");
    } catch (ClassNotFoundException e) {
      return null;
    }
    return dataClazz;
  }

  static void print(ByteBuffer bb) {
    ByteBuffer bb_2 = bb.duplicate();
    bb_2.rewind();
    for (int x = 0, xx = bb_2.limit(); x < xx; ++x) {
      System.out.print((bb_2.get() & 0xFF) + " ");
      if ((x + 1) % 4 == 0)
        System.out.print(System.lineSeparator());
    }
  }
}
