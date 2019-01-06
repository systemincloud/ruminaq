package org.ruminaq.tasks.gate.xor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.FrameworkUtil;

public class Images {

	public enum K {
		IMG_XOR_PALETTE("/icons/palette.xor.png"),
		IMG_XOR_DIAGRAM("/icons/diagram.xor.png")
		;
		
		public String path;
		K(String path) { this.path =  path; }
	}
	
	static Map<String, String> images = new HashMap<String, String>() {	private static final long serialVersionUID = 1L; {
		for (final K v : K.values())
			put(v.name(), FileLocator.find(FrameworkUtil.getBundle(this.getClass()), new Path(v.path), null).toString());
	}};
	
	public static Map<String, String> getImageKeyPath() { return images; }
}
