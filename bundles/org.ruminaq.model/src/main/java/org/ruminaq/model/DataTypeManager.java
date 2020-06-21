package org.ruminaq.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.javatuples.Pair;
import org.osgi.service.component.annotations.Reference;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.model.api.ModelExtensionHandler;
import org.ruminaq.model.ruminaq.dt.Bool;
import org.ruminaq.model.ruminaq.dt.Complex32;
import org.ruminaq.model.ruminaq.dt.Complex64;
import org.ruminaq.model.ruminaq.dt.Control;
import org.ruminaq.model.ruminaq.dt.DatatypeFactory;
import org.ruminaq.model.ruminaq.dt.Decimal;
import org.ruminaq.model.ruminaq.dt.Float32;
import org.ruminaq.model.ruminaq.dt.Float64;
import org.ruminaq.model.ruminaq.dt.Int32;
import org.ruminaq.model.ruminaq.dt.Int64;
import org.ruminaq.model.ruminaq.dt.Raw;
import org.ruminaq.model.ruminaq.dt.Text;
import org.ruminaq.model.ruminaq.DataType;

import ch.qos.logback.classic.Logger;

public enum DataTypeManager {
  INSTANCE;

  private static final Logger logger = ModelerLoggerFactory
      .getLogger(DataTypeManager.class);

  @Reference
  private ModelExtensionHandler extensions;

  private Map<Class<? extends DataType>, List<Pair<Class<? extends DataType>, Boolean>>> canCast = new HashMap<>();

  public List<Class<? extends DataType>> getDataTypes() {
    List<Class<? extends DataType>> dts = new LinkedList<>();
    dts.add(Bool.class);
    dts.add(Complex32.class);
    dts.add(Complex64.class);
    dts.add(Control.class);
    dts.add(Decimal.class);
    dts.add(Float32.class);
    dts.add(Float64.class);
    dts.add(Int32.class);
    dts.add(Int64.class);
    dts.add(Raw.class);
    dts.add(Text.class);
    dts.addAll(extensions.getDataTypes());
    return dts;
  }

  public DataType getDataTypeFromName(String name) {
    logger.trace("Look for: {} in Modeler Extensions", name);
    Optional<DataType> dataType = extensions.getDataTypeFromName(name);
    if (dataType.isPresent())
      return dataType.get();
    logger.trace("Not found in Extensions");
    if (Bool.class.getSimpleName().equals(name))
      return DatatypeFactory.eINSTANCE.createBool();
    else if (Complex32.class.getSimpleName().equals(name))
      return DatatypeFactory.eINSTANCE.createComplex32();
    else if (Complex64.class.getSimpleName().equals(name))
      return DatatypeFactory.eINSTANCE.createComplex64();
    else if (Control.class.getSimpleName().equals(name))
      return DatatypeFactory.eINSTANCE.createControl();
    else if (Decimal.class.getSimpleName().equals(name))
      return DatatypeFactory.eINSTANCE.createDecimal();
    else if (Float32.class.getSimpleName().equals(name))
      return DatatypeFactory.eINSTANCE.createFloat32();
    else if (Float64.class.getSimpleName().equals(name))
      return DatatypeFactory.eINSTANCE.createFloat64();
    else if (Int32.class.getSimpleName().equals(name))
      return DatatypeFactory.eINSTANCE.createInt32();
    else if (Int64.class.getSimpleName().equals(name))
      return DatatypeFactory.eINSTANCE.createInt64();
    else if (Raw.class.getSimpleName().equals(name))
      return DatatypeFactory.eINSTANCE.createRaw();
    else if (Text.class.getSimpleName().equals(name))
      return DatatypeFactory.eINSTANCE.createText();
    else
      return null;
  }

  public boolean canCastFromTo(Class<? extends DataType> from,
      Class<? extends DataType> to) {
    if (extensions.canCastFromTo(from, to))
      return true;
    for (Entry<Class<? extends DataType>, List<Pair<Class<? extends DataType>, Boolean>>> es : canCast
        .entrySet())
      if (es.getKey().isAssignableFrom(from))
        for (Pair<Class<? extends DataType>, Boolean> it : es.getValue())
          if (it.getValue0().isAssignableFrom(to))
            return true;
    return false;
  }

  public boolean isLossyCastFromTo(Class<? extends DataType> from,
      Class<? extends DataType> to) {
    if (extensions.isLossyCastFromTo(from, to))
      return true;
    for (Entry<Class<? extends DataType>, List<Pair<Class<? extends DataType>, Boolean>>> es : canCast
        .entrySet())
      if (es.getKey().isAssignableFrom(from))
        for (Pair<Class<? extends DataType>, Boolean> it : es.getValue())
          if (it.getValue0().isAssignableFrom(to)
              && it.getValue1().booleanValue() == false)
            return true;
    return false;
  }
}
