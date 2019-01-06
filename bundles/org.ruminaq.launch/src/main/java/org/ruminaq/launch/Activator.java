/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.launch;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {

	private static Activator plugin;

	public Activator() { }

	public static Activator getDefault() {
		return plugin;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public Bundle[] getBundles(String bundleName, String version) {
		Bundle[] bundles = Platform.getBundles(bundleName, version);
		if (bundles != null) return bundles;
		else return null;
	}

	public Bundle getBundle(String bundleName) {
		Bundle[] bundles = getBundles(bundleName, null);
		if (bundles != null && bundles.length > 0) return bundles[0];
		else return null;
	}
}

