package org.ruminaq.runner.impl.data;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.ruminaq.runner.thrift.RemoteData;

public abstract class DataI implements Serializable {

    private static final long serialVersionUID = 1L;

    protected List<Integer> dims;
    public    List<Integer> getDimensions()       { return dims; }

    protected int nElements = 1;
    public    int getNumberOfElements() { return nElements; }

    private boolean constant = false;
    public  boolean isConstant()                  { return this.constant; }
    public  void    setConstant(boolean constant) {        this.constant = constant; }

    private boolean constantRead = false;
    public  boolean isConstantRead()                      { return this.constantRead; }
    public  void    setConstantRead(boolean constantRead) {        this.constant = constantRead; }

    protected DataI(List<Integer> dims) {
        this.dims = dims;
        for(Integer i : dims) nElements *= i;
    }

    protected abstract DataI to(Class<? extends DataI> dataI);

    protected DataI from(Class<? extends DataI> type) {
        DataI ret = null;
        try {
            Method method = type.getMethod("from", DataI.class);
            ret = (DataI) method.invoke(null, this);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) { return null; }
        return ret;
    }

    @SuppressWarnings("unchecked")
    public <T extends DataI> T get(Class<T> type) {
        return (T) to(type);
    }

    @SuppressWarnings("unchecked")
    public static <T extends DataI> List<T> get(List<DataI> datas, Class<T> type) {
        LinkedList<T> ret = new LinkedList<>();
        for(DataI d : datas) ret.add((T) d.to(type));
        return ret;
    }

    public String toString()      { return this.getClass().getSimpleName(); }
    public String toShortString() { return this.getClass().getSimpleName(); }

    public abstract RemoteData getRemoteData();
}
