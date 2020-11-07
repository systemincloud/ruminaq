/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.model.ruminaq;

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

public enum DataTypeManager {
  INSTANCE;

  public DataType getDataTypeFromName(String name) {
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
//    for (Entry<Class<? extends DataType>, List<Pair<Class<? extends DataType>, Boolean>>> es : canCast
//        .entrySet())
//      if (es.getKey().isAssignableFrom(from))
//        for (Pair<Class<? extends DataType>, Boolean> it : es.getValue())
//          if (it.getValue0().isAssignableFrom(to))
//            return true;
    return false;
  }

  public boolean isLossyCastFromTo(Class<? extends DataType> from,
      Class<? extends DataType> to) {
//    for (Entry<Class<? extends DataType>, List<Pair<Class<? extends DataType>, Boolean>>> es : canCast
//        .entrySet())
//      if (es.getKey().isAssignableFrom(from))
//        for (Pair<Class<? extends DataType>, Boolean> it : es.getValue())
//          if (it.getValue0().isAssignableFrom(to)
//              && it.getValue1().booleanValue() == false)
//            return true;
    return false;
  }
}
