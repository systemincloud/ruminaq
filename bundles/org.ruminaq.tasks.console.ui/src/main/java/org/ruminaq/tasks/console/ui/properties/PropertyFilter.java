package org.ruminaq.tasks.console.ui.properties;

import org.osgi.framework.FrameworkUtil;
import org.ruminaq.tasks.AbstractTaskPropertyFilter;

public class PropertyFilter extends AbstractTaskPropertyFilter {

	@Override
	protected String getPrefix() {
		String symbolicName = FrameworkUtil.getBundle(getClass()).getSymbolicName();
		return symbolicName.substring(0, symbolicName.length() - ".ui".length());
	}
}
