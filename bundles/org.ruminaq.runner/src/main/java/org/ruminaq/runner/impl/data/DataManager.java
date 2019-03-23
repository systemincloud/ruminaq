package org.ruminaq.runner.impl.data;

import java.util.Optional;

import org.ruminaq.model.dt.Bool;
import org.ruminaq.model.dt.Complex32;
import org.ruminaq.model.dt.Complex64;
import org.ruminaq.model.dt.Control;
import org.ruminaq.model.dt.Decimal;
import org.ruminaq.model.dt.Float32;
import org.ruminaq.model.dt.Float64;
import org.ruminaq.model.dt.Int32;
import org.ruminaq.model.dt.Int64;
import org.ruminaq.model.dt.Raw;
import org.ruminaq.model.dt.Text;
import org.ruminaq.runner.service.RunnerServiceManager;

public enum DataManager {
    INSTANCE;

    public Class<? extends DataI> getDataFromName(String name) {
        if("Data".equals(name)) return DataI.class;

        Optional<Class<DataI>> data = RunnerServiceManager.INSTANCE.getDataFromName(name);
        if(data.isPresent()) return data.get();

        else if(Bool     .class.getSimpleName().equals(name)) return BoolI.class;
        else if(Complex32.class.getSimpleName().equals(name)) return Complex32I.class;
        else if(Complex64.class.getSimpleName().equals(name)) return Complex64I.class;
        else if(Control  .class.getSimpleName().equals(name)) return ControlI.class;
        else if(Decimal  .class.getSimpleName().equals(name)) return DecimalI.class;
        else if(Float32  .class.getSimpleName().equals(name)) return Float32I.class;
        else if(Float64  .class.getSimpleName().equals(name)) return Float64I.class;
        else if(Int32    .class.getSimpleName().equals(name)) return Int32I.class;
        else if(Int64    .class.getSimpleName().equals(name)) return Int64I.class;
        else if(Raw      .class.getSimpleName().equals(name)) return RawI.class;
        else if(Text     .class.getSimpleName().equals(name)) return TextI.class;
        else                                                  return null;
    }
}
