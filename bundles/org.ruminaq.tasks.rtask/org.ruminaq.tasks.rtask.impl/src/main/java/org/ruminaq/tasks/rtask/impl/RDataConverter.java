/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.rtask.impl;

//import de.walware.rj.data.RObject;
//import de.walware.rj.servi.RServi;
//import de.walware.rj.services.FunctionCall;

public enum RDataConverter {
  INSTANCE;

//  public RObject toRData(RServi fRservi, DataI dataI) {
//    try {
//      StringBuilder sdims = new StringBuilder("dims <- c(");
//      dataI.getDimensions().forEach(i -> sdims.append(i.toString() + "L, "));
//      if (dataI.getDimensions().size() > 0)
//        sdims.delete(sdims.length() - 3, sdims.length());
//      sdims.append(")");
//      RObject dims = fRservi.evalData(sdims.toString(), null);
//
//      RObject data = RRunnerServiceManager.INSTANCE.toRData(dataI, dims);
//      if (data != null)
//        return data;
//
//      else if (dataI instanceof BoolI) {
//
//        	"array(dim=c(3,4,2))"
//        	RArray<RLogicalStore> rValues = RObjectFactoryImpl.INSTANCE.createLogiArray(dim);
//            for(boolean v : ((BoolI) dataI).getValues())
//                rValues.add(new PyBoolean(v));
//
//            RObject ret = fRservi.evalData("Bool$new()", null);
//            ret.set
//      } else if (dataI instanceof Complex32I) {
//            Complex32I c = (Complex32I) dataI;
//            float[] real = c.getRealValues();
//            float[] imag = c.getImagValues();
//            for(int i = 0; i < c.getNumberOfElements(); i++)
//                pyValues.add(new PyComplex(real[i], imag[i]));
//            return pi.get("Complex32").__call__(new PyObject[] { pyValues, pyDims });
//      } else if (dataI instanceof Complex64I) {
//            Complex64I c = (Complex64I) dataI;
//            double[] real = c.getRealValues();
//            double[] imag = c.getImagValues();
//            for(int i = 0; i < c.getNumberOfElements(); i++)
//                pyValues.add(new PyComplex(real[i], imag[i]));
//            return pi.get("Complex64").__call__(new PyObject[] { pyValues, pyDims });
//      }
//      if (dataI instanceof ControlI) {
//            return pi.get("Control").__call__();
//      } else if (dataI instanceof DecimalI) {
//            for(BigDecimal v : ((DecimalI) dataI).getValues())
//                pyValues.add(pi.get("D").__call__(new PyString(v.toString())));
//            return pi.get("Int32").__call__(new PyObject[] { pyValues, pyDims });
//      } else if (dataI instanceof Float32I) {
//            for(float v : ((Float32I) dataI).getValues())
//                pyValues.add(new PyFloat(v));
//            return pi.get("Float32").__call__(new PyObject[] { pyValues, pyDims });
//      } else if (dataI instanceof Float64I) {
//            for(double v : ((Float64I) dataI).getValues())
//                pyValues.add(new PyFloat(v));
//            return pi.get("Float64").__call__(new PyObject[] { pyValues, pyDims });
//      } else if (dataI instanceof Int32I) {
//        RObject values = fRservi.evalData("values <- c()", null);
//        for (int v : ((Int32I) dataI).getValues())
//          values = fRservi.evalData("values <- c(values, " + v + "L)", null);
//
//        FunctionCall fun = fRservi.createFunctionCall("array");
//        fun.add("data", values);
//        fun.add("dim", dims);
//        RObject ret = fun.evalData(null);
//
//        return ret;
//      } else if (dataI instanceof Int64I) {
//            for(long v : ((Int64I) dataI).getValues())
//                pyValues.add(new PyLong(v));
//            return pi.get("Int64").__call__(new PyObject[] { pyValues, pyDims });
//      } else if (dataI instanceof RawI) {
//            for(byte[] v : ((RawI) dataI).getValues())
//                pyValues.add(new PyByteArray(v));
//            return pi.get("Raw").__call__(new PyObject[] { pyValues, pyDims });
//      } else if (dataI instanceof TextI) {
//            for(String v : ((TextI) dataI).getValues())
//                pyValues.add(new PyString(v));
//            return pi.get("Text").__call__(new PyObject[] { pyValues, pyDims });
//      }
//    } catch (CoreException e) {
//      e.printStackTrace();
//    }
//    return null;
//  }

//  public DataI fromRData(RObject data, String datatype) {
//        PyObject pyDims = data.__getattr__("dims");
//    List<Integer> dims = new LinkedList<>();
//        for(PyObject d : ((PyList) pyDims).getArray())
//            dims.add(((PyInteger) d).asInt());
//
//        PyObject[] pyValues = ((PyList) data.__getattr__("values")).getArray();

//    DataI dataI = RRunnerServiceManager.INSTANCE.fromRData(data, null, dims);

//    if (dataI != null)
//      return dataI;

//        else if(pi.get("Bool").equals(data.fastGetClass())) {
//            boolean[] values = new boolean[pyValues.length];
//            int i = 0;
//            for(PyObject v : pyValues)
//                values[i++] = ((PyBoolean) v).getBooleanValue();
//            return new BoolI(values, dims);
//        } else if(pi.get("Complex32").equals(data.fastGetClass())) {
//            float[] real = new float[pyValues.length];
//            float[] imag = new float[pyValues.length];
//            int i = 0;
//            for(PyObject v : pyValues) {
//                real[i]   = (float) ((PyComplex) v).getReal().asDouble();
//                imag[i++] = (float) ((PyComplex) v).getImag().asDouble();
//            }
//            return new Complex32I(real, imag, dims);
//        } else if(pi.get("Complex64").equals(data.fastGetClass())) {
//            double[] real = new double[pyValues.length];
//            double[] imag = new double[pyValues.length];
//            int i = 0;
//            for(PyObject v : pyValues) {
//                real[i]   = ((PyComplex) v).getReal().asDouble();
//                imag[i++] = ((PyComplex) v).getImag().asDouble();
//            }
//            return new Complex64I(real, imag, dims);
//        } else if(pi.get("Control").equals(data.fastGetClass())) {
//            return new ControlI();
//        } else if(pi.get("Decimal").equals(data.fastGetClass())) {
//            BigDecimal[] bd = new BigDecimal[pyValues.length];
//            int i = 0;
//            for(PyObject v : pyValues)
//                bd[i++] = new BigDecimal(((PyString)v.__getattr__("__str__").__call__()).asString());
//            return new DecimalI(bd, dims);
//        } else if(pi.get("Float32").equals(data.fastGetClass())) {
//            float[] values = new float[pyValues.length];
//            int i = 0;
//            for(PyObject v : pyValues)
//                values[i++] = (float) ((PyFloat) v).asDouble();
//            return new Float32I(values, dims);
//        } else if(pi.get("Float64").equals(data.fastGetClass())) {
//            double[] values = new double[pyValues.length];
//            int i = 0;
//            for(PyObject v : pyValues)
//                values[i++] = ((PyFloat) v).asDouble();
//            return new Float64I(values, dims);
//        } else if(pi.get("Int32").equals(data.fastGetClass())) {
//            int[] values = new int[pyValues.length];
//            int i = 0;
//            for(PyObject v : pyValues) {
//                if(v instanceof PyLong)         values[i++] = ((PyLong) v).asInt();
//                else if(v instanceof PyInteger) values[i++] = ((PyInteger) v).asInt();
//            }
//            return new Int32I(values, dims);
//        } else if(pi.get("Int64").equals(data.fastGetClass())) {
//            long[] values = new long[pyValues.length];
//            int i = 0;
//            for(PyObject v : pyValues) {
//                if(v instanceof PyLong)         values[i++] = ((PyLong) v).asLong();
//                else if(v instanceof PyInteger) values[i++] = ((PyInteger) v).asLong();
//            }
//            return new Int64I(values, dims);
//        } else if(pi.get("Raw").equals(data.fastGetClass())) {
//            List<byte[]> values = new LinkedList<>();
//            for(PyObject v : pyValues) {
//                PyByteArray pba = (PyByteArray) v;
//                Object[] a = pba.toArray();
//                byte[] ba = new byte[a.length];
//                int i = 0;
//                for(Object b : a)
//                    ba[i++] = (byte) ((PyInteger) b).asInt();
//                values.add(ba);
//            }
//            return new RawI(values, dims);
//        } else if(pi.get("Text").equals(data.fastGetClass())) {
//            List<String> values = new LinkedList<>();
//            for(PyObject v : pyValues)
//                values.add(((PyString) v).asString());
//            return new TextI(values, dims);
//        }

//    return null;
//  }

}
