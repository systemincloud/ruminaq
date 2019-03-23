package org.ruminaq.model;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.javatuples.Pair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.osgi.service.component.annotations.Reference;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.model.api.ModelExtensionHandler;
import org.ruminaq.model.dt.Bool;
import org.ruminaq.model.dt.Complex32;
import org.ruminaq.model.dt.Complex64;
import org.ruminaq.model.dt.Control;
import org.ruminaq.model.dt.DatatypeFactory;
import org.ruminaq.model.dt.Decimal;
import org.ruminaq.model.dt.Float32;
import org.ruminaq.model.dt.Float64;
import org.ruminaq.model.dt.Int32;
import org.ruminaq.model.dt.Int64;
import org.ruminaq.model.dt.Raw;
import org.ruminaq.model.dt.Text;
import org.ruminaq.model.ruminaq.DataType;
import org.slf4j.Logger;

@SuppressWarnings("unchecked")
public enum DataTypeManager {
	INSTANCE;

	private static final Logger logger = ModelerLoggerFactory.getLogger(DataTypeManager.class);

	@Reference
	private ModelExtensionHandler extensions;

	private JSONParser parser = new JSONParser();

	private Map<Class<? extends DataType>, List<Pair<Class<? extends DataType>, Boolean>>> canCast = new HashMap<>();
	{
		JSONObject listJson = null;

		try {
			listJson = (JSONObject) parser.parse(new InputStreamReader(DataTypeManager.class.getResourceAsStream("canCast.json")));

			String pkg = Bool.class.getPackage().getName();

			for(Object data : listJson.keySet()) {
				Class<? extends DataType> dataClass = (Class<? extends DataType>) Class.forName(pkg + "." + data);
				canCast.put(dataClass, new LinkedList<Pair<Class<? extends DataType>, Boolean>>());
				JSONObject casts = (JSONObject) listJson.get(data);
				for(Object castData : casts.keySet()) {
					Class<? extends DataType> castClass = (Class<? extends DataType>) Class.forName(pkg + "." + castData);
					boolean notLossy = casts.get(castData).equals("t");
					canCast.get(dataClass).add(new Pair<Class<? extends DataType>, Boolean>(castClass, notLossy));
				}
			}
		} catch(IOException | ParseException | ClassNotFoundException e) { e.printStackTrace(); }
	}

	public List<Class<? extends DataType>> getDataTypes() {
		List<Class<? extends DataType>> dts = new LinkedList<>();
		dts.add(Bool     .class);
		dts.add(Complex32.class);
		dts.add(Complex64.class);
		dts.add(Control  .class);
		dts.add(Decimal  .class);
		dts.add(Float32  .class);
		dts.add(Float64  .class);
		dts.add(Int32    .class);
		dts.add(Int64    .class);
		dts.add(Raw      .class);
		dts.add(Text     .class);
		dts.addAll(extensions.getDataTypes());
		return dts;
	}

	public DataType getDataTypeFromName(String name) {
		logger.trace("Look for: {} in Modeler Extensions", name);
		Optional<DataType> dataType = extensions.getDataTypeFromName(name);
		if(dataType.isPresent()) return dataType.get();
		logger.trace("Not found in Extensions");
		     if(Bool     .class.getSimpleName().equals(name)) return DatatypeFactory.eINSTANCE.createBool();
		else if(Complex32.class.getSimpleName().equals(name)) return DatatypeFactory.eINSTANCE.createComplex32();
		else if(Complex64.class.getSimpleName().equals(name)) return DatatypeFactory.eINSTANCE.createComplex64();
		else if(Control  .class.getSimpleName().equals(name)) return DatatypeFactory.eINSTANCE.createControl();
		else if(Decimal  .class.getSimpleName().equals(name)) return DatatypeFactory.eINSTANCE.createDecimal();
		else if(Float32  .class.getSimpleName().equals(name)) return DatatypeFactory.eINSTANCE.createFloat32();
		else if(Float64  .class.getSimpleName().equals(name)) return DatatypeFactory.eINSTANCE.createFloat64();
		else if(Int32    .class.getSimpleName().equals(name)) return DatatypeFactory.eINSTANCE.createInt32();
		else if(Int64    .class.getSimpleName().equals(name)) return DatatypeFactory.eINSTANCE.createInt64();
		else if(Raw      .class.getSimpleName().equals(name)) return DatatypeFactory.eINSTANCE.createRaw();
		else if(Text     .class.getSimpleName().equals(name)) return DatatypeFactory.eINSTANCE.createText();
		else                                                  return null;
	}

	public boolean canCastFromTo(Class<? extends DataType> from, Class<? extends DataType> to) {
		if (extensions.canCastFromTo(from, to)) return true;
		for (Entry<Class<? extends DataType>, List<Pair<Class<? extends DataType>, Boolean>>> es : canCast.entrySet())
			if(es.getKey().isAssignableFrom(from))
				for(Pair<Class<? extends DataType>, Boolean> it : es.getValue())
					if(it.getValue0().isAssignableFrom(to)) return true;
		return false;
	}

	public boolean isLossyCastFromTo(Class<? extends DataType> from, Class<? extends DataType> to) {
		if (extensions.isLossyCastFromTo(from, to)) return true;
		for (Entry<Class<? extends DataType>, List<Pair<Class<? extends DataType>, Boolean>>> es : canCast.entrySet())
			if(es.getKey().isAssignableFrom(from))
				for(Pair<Class<? extends DataType>, Boolean> it : es.getValue())
					if(it.getValue0().isAssignableFrom(to) && it.getValue1().booleanValue() == false) return true;
		return false;
	}
}
