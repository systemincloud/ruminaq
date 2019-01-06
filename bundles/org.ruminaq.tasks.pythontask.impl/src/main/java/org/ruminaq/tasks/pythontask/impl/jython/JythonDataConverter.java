package org.ruminaq.tasks.pythontask.impl.jython;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import org.python.core.PyBoolean;
import org.python.core.PyByteArray;
import org.python.core.PyComplex;
import org.python.core.PyFloat;
import org.python.core.PyInteger;
import org.python.core.PyList;
import org.python.core.PyLong;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import org.ruminaq.runner.impl.data.BoolI;
import org.ruminaq.runner.impl.data.Complex32I;
import org.ruminaq.runner.impl.data.Complex64I;
import org.ruminaq.runner.impl.data.ControlI;
import org.ruminaq.runner.impl.data.DataI;
import org.ruminaq.runner.impl.data.DecimalI;
import org.ruminaq.runner.impl.data.Float32I;
import org.ruminaq.runner.impl.data.Float64I;
import org.ruminaq.runner.impl.data.Int32I;
import org.ruminaq.runner.impl.data.Int64I;
import org.ruminaq.runner.impl.data.RawI;
import org.ruminaq.runner.impl.data.TextI;
import org.ruminaq.tasks.pythontask.service.PythonRunnerServiceManager;

public enum JythonDataConverter {
    INSTANCE;

    public void importDatas(PythonInterpreter pi) {
        PythonRunnerServiceManager.INSTANCE.importDatas(pi);
        pi.exec("from ruminaq.data.Bool import Bool");
        pi.exec("from ruminaq.data.Complex32 import Complex32");
        pi.exec("from ruminaq.data.Complex64 import Complex64");
        pi.exec("from ruminaq.data.Control import Control");
        pi.exec("from decimal import Decimal as D");
        pi.exec("from ruminaq.data.Decimal import Decimal");
        pi.exec("from ruminaq.data.Float32 import Float32");
        pi.exec("from ruminaq.data.Float64 import Float64");
        pi.exec("from ruminaq.data.Int32 import Int32");
        pi.exec("from ruminaq.data.Int64 import Int64");
        pi.exec("from ruminaq.data.Raw import Raw");
        pi.exec("from ruminaq.data.Text import Text");
    }

    public PyObject toJythonData(PythonInterpreter pi, DataI dataI) {
        PyList pyDims = new PyList();
        for(Integer i : dataI.getDimensions())
            pyDims.add(new PyInteger(i));

        PyList pyValues = new PyList();

        PyObject data = PythonRunnerServiceManager.INSTANCE.toJythonData(pi, dataI, pyDims);
        if(data != null) return data;

        else if(dataI instanceof BoolI) {
            for(boolean v : ((BoolI) dataI).getValues())
                pyValues.add(new PyBoolean(v));
            return pi.get("Bool").__call__(new PyObject[] { pyValues, pyDims });
        } else if(dataI instanceof Complex32I) {
            Complex32I c = (Complex32I) dataI;
            float[] real = c.getRealValues();
            float[] imag = c.getImagValues();
            for(int i = 0; i < c.getNumberOfElements(); i++)
                pyValues.add(new PyComplex(real[i], imag[i]));
            return pi.get("Complex32").__call__(new PyObject[] { pyValues, pyDims });
        } else if(dataI instanceof Complex64I) {
            Complex64I c = (Complex64I) dataI;
            double[] real = c.getRealValues();
            double[] imag = c.getImagValues();
            for(int i = 0; i < c.getNumberOfElements(); i++)
                pyValues.add(new PyComplex(real[i], imag[i]));
            return pi.get("Complex64").__call__(new PyObject[] { pyValues, pyDims });
        } if(dataI instanceof ControlI) {
            return pi.get("Control").__call__();
        } else if(dataI instanceof DecimalI) {
            for(BigDecimal v : ((DecimalI) dataI).getValues())
                pyValues.add(pi.get("D").__call__(new PyString(v.toString())));
            return pi.get("Int32").__call__(new PyObject[] { pyValues, pyDims });
        } else if(dataI instanceof Float32I) {
            for(float v : ((Float32I) dataI).getValues())
                pyValues.add(new PyFloat(v));
            return pi.get("Float32").__call__(new PyObject[] { pyValues, pyDims });
        } else if(dataI instanceof Float64I) {
            for(double v : ((Float64I) dataI).getValues())
                pyValues.add(new PyFloat(v));
            return pi.get("Float64").__call__(new PyObject[] { pyValues, pyDims });
        } else if(dataI instanceof Int32I) {
            for(int v : ((Int32I) dataI).getValues())
                pyValues.add(new PyInteger(v));
            return pi.get("Int32").__call__(new PyObject[] { pyValues, pyDims });
        } else if(dataI instanceof Int64I) {
            for(long v : ((Int64I) dataI).getValues())
                pyValues.add(new PyLong(v));
            return pi.get("Int64").__call__(new PyObject[] { pyValues, pyDims });
        } else if(dataI instanceof RawI) {
            for (byte[] v : ((RawI) dataI).getValues()) {
                Constructor<PyByteArray> constructor;
                try {
                    constructor = PyByteArray.class.getDeclaredConstructor(byte[].class);
                    constructor.setAccessible(true);
                    PyByteArray pyArray = constructor.newInstance(v);
                    pyValues.add(pyArray);
                } catch (NoSuchMethodException |
                        SecurityException |
                        InstantiationException |
                        IllegalAccessException |
                        IllegalArgumentException |
                        InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            return pi.get("Raw").__call__(new PyObject[] { pyValues, pyDims });
        } else if(dataI instanceof TextI) {
            for(String v : ((TextI) dataI).getValues())
                pyValues.add(new PyString(v));
            return pi.get("Text").__call__(new PyObject[] { pyValues, pyDims });
        }

        return null;
    }

    public DataI fromJythonData(PythonInterpreter pi, PyObject data) {
        PyObject pyDims = data.__getattr__("dims");
        List<Integer> dims = new LinkedList<>();
        for(PyObject d : ((PyList) pyDims).getArray())
            dims.add(((PyInteger) d).asInt());

        PyObject[] pyValues = ((PyList) data.__getattr__("values")).getArray();

        DataI dataI = PythonRunnerServiceManager.INSTANCE.fromJythonData(pi, data, pyValues, dims);

        if(dataI != null) return dataI;

        else if(pi.get("Bool").equals(data.fastGetClass())) {
            boolean[] values = new boolean[pyValues.length];
            int i = 0;
            for(PyObject v : pyValues)
                values[i++] = ((PyBoolean) v).getBooleanValue();
            return new BoolI(values, dims);
        } else if(pi.get("Complex32").equals(data.fastGetClass())) {
            float[] real = new float[pyValues.length];
            float[] imag = new float[pyValues.length];
            int i = 0;
            for(PyObject v : pyValues) {
                real[i]   = (float) ((PyComplex) v).getReal().asDouble();
                imag[i++] = (float) ((PyComplex) v).getImag().asDouble();
            }
            return new Complex32I(real, imag, dims);
        } else if(pi.get("Complex64").equals(data.fastGetClass())) {
            double[] real = new double[pyValues.length];
            double[] imag = new double[pyValues.length];
            int i = 0;
            for(PyObject v : pyValues) {
                real[i]   = ((PyComplex) v).getReal().asDouble();
                imag[i++] = ((PyComplex) v).getImag().asDouble();
            }
            return new Complex64I(real, imag, dims);
        } else if(pi.get("Control").equals(data.fastGetClass())) {
            return new ControlI();
        } else if(pi.get("Decimal").equals(data.fastGetClass())) {
            BigDecimal[] bd = new BigDecimal[pyValues.length];
            int i = 0;
            for(PyObject v : pyValues)
                bd[i++] = new BigDecimal(((PyString)v.__getattr__("__str__").__call__()).asString());
            return new DecimalI(bd, dims);
        } else if(pi.get("Float32").equals(data.fastGetClass())) {
            float[] values = new float[pyValues.length];
            int i = 0;
            for(PyObject v : pyValues)
                values[i++] = (float) ((PyFloat) v).asDouble();
            return new Float32I(values, dims);
        } else if(pi.get("Float64").equals(data.fastGetClass())) {
            double[] values = new double[pyValues.length];
            int i = 0;
            for(PyObject v : pyValues)
                values[i++] = ((PyFloat) v).asDouble();
            return new Float64I(values, dims);
        } else if(pi.get("Int32").equals(data.fastGetClass())) {
            int[] values = new int[pyValues.length];
            int i = 0;
            for(PyObject v : pyValues) {
                if(v instanceof PyLong)         values[i++] = ((PyLong) v).asInt();
                else if(v instanceof PyInteger) values[i++] = ((PyInteger) v).asInt();
            }
            return new Int32I(values, dims);
        } else if(pi.get("Int64").equals(data.fastGetClass())) {
            long[] values = new long[pyValues.length];
            int i = 0;
            for(PyObject v : pyValues) {
                if(v instanceof PyLong)         values[i++] = ((PyLong) v).asLong();
                else if(v instanceof PyInteger) values[i++] = ((PyInteger) v).asLong();
            }
            return new Int64I(values, dims);
        } else if(pi.get("Raw").equals(data.fastGetClass())) {
            List<byte[]> values = new LinkedList<>();
            for(PyObject v : pyValues) {
                PyByteArray pba = (PyByteArray) v;
                Object[] a = pba.toArray();
                byte[] ba = new byte[a.length];
                int i = 0;
                for(Object b : a)
                    ba[i++] = (byte) ((PyInteger) b).asInt();
                values.add(ba);
            }
            return new RawI(values, dims);
        } else if(pi.get("Text").equals(data.fastGetClass())) {
            List<String> values = new LinkedList<>();
            for(PyObject v : pyValues)
                values.add(((PyString) v).asString());
            return new TextI(values, dims);
        }

        return null;
    }

}
