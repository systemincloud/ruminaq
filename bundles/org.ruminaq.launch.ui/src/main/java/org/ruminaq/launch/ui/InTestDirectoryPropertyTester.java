package org.ruminaq.launch.ui;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.ruminaq.eclipse.ConstantsUtil;

public class InTestDirectoryPropertyTester extends PropertyTester {

	private static final String PROPERTY_NAME = "inTestDir";

	@Override
	public boolean test(Object receiver, String property, Object[] arg2, Object expectedValue) {
	    if(property.equals(PROPERTY_NAME) && receiver instanceof IFile)
	    	return ConstantsUtil.isTest(((IFile) receiver));
	    return false;
	}
}
