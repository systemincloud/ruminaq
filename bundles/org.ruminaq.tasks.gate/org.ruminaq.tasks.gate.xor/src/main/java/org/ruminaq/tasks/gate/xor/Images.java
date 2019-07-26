package org.ruminaq.tasks.gate.xor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.ImageDescriptor;
import org.ruminaq.gui.api.ImagesExtension;

@Component
public class Images implements ImagesExtension {
	
	public enum Image implements ImageDescriptor {
		IMG_XOR_PALETTE("/icons/palette.xor.png"),
		IMG_XOR_DIAGRAM("/icons/diagram.xor.png")
		;

		private String path;

		Image(String path) {
			this.path = path;
		}

		@Override
		public String path() {
			return path;
		}

		@Override
		public Class<?> clazz() {
			return Image.class;
		}
	}
	
	public ImageDescriptor[] getImageDecriptors() {
		return Image.values();
	}
}
