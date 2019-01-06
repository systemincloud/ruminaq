package org.ruminaq.model.desc;

import java.lang.reflect.Field;

public class PortsDescrUtil {

	public static String getName(PortsDescr pd) {
		try {
			Field f = pd.getClass().getField(pd.toString());
			IN in = f.getAnnotation(IN.class);
			if(in != null) return in.name();
			OUT out = f.getAnnotation(OUT.class);
			if(out != null) return out.name();
		} catch (NoSuchFieldException | SecurityException e) { }
		return "";
	}

	public static int getGroup(PortsDescr pd) {
		try {
			Field f = pd.getClass().getField(pd.toString());
			IN in = f.getAnnotation(IN.class);
			if(in != null) return in.group();
		} catch (NoSuchFieldException | SecurityException e) { }
		return -1;
	}
}
