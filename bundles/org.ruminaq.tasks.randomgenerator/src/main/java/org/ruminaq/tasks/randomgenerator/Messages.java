package org.ruminaq.tasks.randomgenerator;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.ruminaq.tasks.randomgenerator.messages"; //$NON-NLS-1$
	public static String Properties_RandomGenerator_label_type;
	public static String Properties_RandomGenerator_label_interval;

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() { }
}
