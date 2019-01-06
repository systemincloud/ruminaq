package org.ruminaq.tasks.javatask.impl;

import java.util.LinkedList;
import java.util.List;

import org.ruminaq.tasks.javatask.client.data.Bool;
import org.ruminaq.tasks.javatask.client.data.Complex32;
import org.ruminaq.tasks.javatask.client.data.Complex64;
import org.ruminaq.tasks.javatask.client.data.Control;
import org.ruminaq.tasks.javatask.client.data.Data;
import org.ruminaq.tasks.javatask.client.data.Decimal;
import org.ruminaq.tasks.javatask.client.data.Float32;
import org.ruminaq.tasks.javatask.client.data.Float64;
import org.ruminaq.tasks.javatask.client.data.Int32;
import org.ruminaq.tasks.javatask.client.data.Int64;
import org.ruminaq.tasks.javatask.client.data.Raw;
import org.ruminaq.tasks.javatask.client.data.Text;
import org.ruminaq.tasks.javatask.extension.JavaTaskExtensionManager;
import org.ruminaq.tasks.javatask.impl.service.JavaTaskServiceManager;

public enum JavaTaskDataConverter {
    INSTANCE;

    public List<Class<? extends Data>> getJavaTaskDatas() {
        List<Class<? extends Data>> ret = new LinkedList<>();
        ret.add(Bool.class);
        ret.add(Complex32.class);
        ret.add(Complex64.class);
        ret.add(Control.class);
        ret.add(Decimal.class);
        ret.add(Int32.class);
        ret.add(Int64.class);
        ret.add(Float32.class);
        ret.add(Float64.class);
        ret.add(Raw.class);
        ret.add(Text.class);
        ret.addAll(JavaTaskExtensionManager.INSTANCE.getJavaTaskDatas());
        return ret;
    }

    public Data toJavaTaskData(DataI dataI, Class<? extends Data> to) {
        Data data = JavaTaskServiceManager.INSTANCE.toJavaTaskData(dataI, to);
             if(data != null)               return data;
        else if(to.equals(Bool.class))      return new Bool     (dataI.getDimensions(), dataI.get(BoolI.class)   .getValues());
        else if(to.equals(Complex32.class)) { Complex32I d = dataI.get(Complex32I.class); return new Complex32(dataI.getDimensions(), d.getRealValues(), d.getImagValues()); }
        else if(to.equals(Complex64.class)) { Complex64I d = dataI.get(Complex64I.class); return new Complex64(dataI.getDimensions(), d.getRealValues(), d.getImagValues()); }
        else if(to.equals(Control.class))   return new Control  ();
        else if(to.equals(Decimal.class))   return new Decimal  (dataI.getDimensions(), dataI.get(DecimalI.class).getValues());
        else if(to.equals(Float32.class))   return new Float32  (dataI.getDimensions(), dataI.get(Float32I.class).getValues());
        else if(to.equals(Float64.class))   return new Float64  (dataI.getDimensions(), dataI.get(Float64I.class).getValues());
        else if(to.equals(Int32.class))     return new Int32    (dataI.getDimensions(), dataI.get(Int32I.class)  .getValues());
        else if(to.equals(Int64.class))     return new Int64    (dataI.getDimensions(), dataI.get(Int64I.class)  .getValues());
        else if(to.equals(Raw.class))       return new Raw      (dataI.getDimensions(), dataI.get(RawI.class)    .getValues());
        else if(to.equals(Text.class))      return new Text     (dataI.getDimensions(), dataI.get(TextI.class)   .getValues());
        else return null;
    }

    public Data toJavaTaskData(DataI dataI) {
            Data data = JavaTaskServiceManager.INSTANCE.toJavaTaskData(dataI);
             if(data != null)                return data;
        else if(dataI instanceof BoolI)      return new Bool     (dataI.getDimensions(), dataI.get(BoolI.class)   .getValues());
        else if(dataI instanceof Complex32I) { Complex32I d = dataI.get(Complex32I.class); return new Complex32(dataI.getDimensions(), d.getRealValues(), d.getImagValues()); }
        else if(dataI instanceof Complex64I) { Complex64I d = dataI.get(Complex64I.class); return new Complex64(dataI.getDimensions(), d.getRealValues(), d.getImagValues()); }
        else if(dataI instanceof ControlI)   return new Control  ();
        else if(dataI instanceof DecimalI)   return new Decimal  (dataI.getDimensions(), dataI.get(DecimalI.class).getValues());
        else if(dataI instanceof Float32I)   return new Float32  (dataI.getDimensions(), dataI.get(Float32I.class).getValues());
        else if(dataI instanceof Float64I)   return new Float64  (dataI.getDimensions(), dataI.get(Float64I.class).getValues());
        else if(dataI instanceof Int32I)     return new Int32    (dataI.getDimensions(), dataI.get(Int32I.class)  .getValues());
        else if(dataI instanceof Int64I)     return new Int64    (dataI.getDimensions(), dataI.get(Int64I.class)  .getValues());
        else if(dataI instanceof RawI)       return new Raw      (dataI.getDimensions(), dataI.get(RawI.class)    .getValues());
        else if(dataI instanceof TextI)      return new Text     (dataI.getDimensions(), dataI.get(TextI.class)   .getValues());
        else return null;
    }

    public DataI fromJavaTaskData(Data data, boolean copy) {
        DataI dataI = JavaTaskServiceManager.INSTANCE.fromJavaTaskData(data, copy);
             if(dataI != null)             return dataI;
        else if(data instanceof Bool)      return new BoolI(((Bool) data).getValues(), data.getDimensions(), copy);
        else if(data instanceof Complex32) return new Complex32I(((Complex32) data).getRealValues(), ((Complex32) data).getImagValues(), data.getDimensions(), copy);
        else if(data instanceof Complex64) return new Complex64I(((Complex64) data).getRealValues(), ((Complex64) data).getImagValues(), data.getDimensions(), copy);
        else if(data instanceof Control)   return new ControlI();
        else if(data instanceof Decimal)   return new DecimalI(((Decimal) data).getValues(), data.getDimensions());
        else if(data instanceof Float32)   return new Float32I(((Float32) data).getValues(), data.getDimensions(), copy);
        else if(data instanceof Float64)   return new Float64I(((Float64) data).getValues(), data.getDimensions(), copy);
        else if(data instanceof Int32)     return new Int32I  (((Int32)   data).getValues(), data.getDimensions(), copy);
        else if(data instanceof Int64)     return new Int64I  (((Int64)   data).getValues(), data.getDimensions(), copy);
        else if(data instanceof Raw)       return new RawI    (((Raw)     data).getValues(), data.getDimensions(), copy);
        else if(data instanceof Text)      return new TextI   (((Text)    data).getValues(), data.getDimensions());
        else return null;
    }
}
