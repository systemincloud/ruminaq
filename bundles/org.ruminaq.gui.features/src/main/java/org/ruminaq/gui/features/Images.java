package org.ruminaq.gui.features;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;

public class Images {

	public enum K {
		IMG_CONTEXT_SIMPLE_CONNECTION ("/icons/context.simpleconnection.gif"),
		IMG_PALETTE_INPUTPORT         ("/icons/inputport.palette.png"),
		IMG_PALETTE_OUTPUTPORT        ("/icons/outputport.palette.png");
		
		public String path;
		K(String path) { this.path =  path; }
	}
	
	static Map<String, String> images = new HashMap<String, String>();
	
	public static void init(Bundle bundle) {
		for (final K v : K.values())
			images.put(v.name(), FileLocator.find(bundle, new Path(v.path), null).toString());
	}
	
	public static Map<String, String> getImageKeyPath() { return images; }
}
