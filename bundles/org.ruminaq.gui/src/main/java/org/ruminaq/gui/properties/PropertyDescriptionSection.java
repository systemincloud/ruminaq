/*
 * (C) Copyright 2018 Marek Jagielski.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ruminaq.gui.properties;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.ruminaq.model.util.ModelUtil;

public class PropertyDescriptionSection extends GFPropertySection
    implements ITabbedPropertyConstants {

	private Browser b;

	@Override
	public void createControls(Composite parent,
	    TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		((GridData) parent.getLayoutData()).verticalAlignment = SWT.FILL;
		((GridData) parent.getLayoutData()).grabExcessVerticalSpace = true;

		b = new Browser(parent, SWT.NONE);
		parent.setLayout(new GridLayout());
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = true;
		b.setLayoutData(gd);
		b.addListener(SWT.MenuDetect, new Listener() {
			@Override
			public void handleEvent(Event event) {
				event.doit = false;
			}
		});

	}

	@Override
	public void refresh() {
		PictogramElement pe = getSelectedPictogramElement();

		if (pe != null) {
			EObject bo = Graphiti.getLinkService()
			    .getBusinessObjectForLinkedPictogramElement(pe);
			if (bo == null)
				return;
			b.setText(getPage(bo));
		}
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
	}

	public Font getDescriptionFont() {
		Display display = Display.getCurrent();
		FontData data = display.getSystemFont().getFontData()[0];
		return new Font(display, data.getName(), data.getHeight() + 1, SWT.NONE);
	}

	private String getPage(EObject bo) {
		String fileName = ModelUtil.getName(bo.getClass(), false) + ".html";
		InputStream is = bo.getClass().getResourceAsStream("/html/" + fileName);
		if (is == null)
			return "";
		try {
			return IOUtils.toString(is, "UTF-8");
		} catch (IOException e) {
		}
		return "";
	}

//	private String getDescriptionUrl(EObject bo) {
//    	String fileName = ModelUtil.getName(bo.getClass(), false) + ".html";
//    	Bundle bundle = Platform.getBundle(FrameworkUtil.getBundle(bo.getClass()).getSymbolicName());
//    	URL fileURL = bundle.getEntry("html/" + fileName);
//    	URI uri = null;
//    	try {
//			uri = FileLocator.resolve(fileURL).toURI();
//		} catch (URISyntaxException | IOException e) {}
//    	return uri.toString();
//	}
//

//	private String getDescription(Object bo) {
//    	String fieldName = "UI_" + bo.getClass().getSimpleName().replaceAll("Impl", "") + "_long_description";
//
//    	Class<?> messages = Util.findClass(bo, "Messages");
//    	String text = "";
//    	if(messages == null) return text;
//		try {
//			Field field = messages.getField(fieldName);
//	    	text = (String)field.get(null);
//		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
//		}
//
//		return text;
//	}
//
//	private StyleRange[] getDescriptionStyleRanges(Object bo) {
//		List<StyleRange> ranges = new ArrayList<>();
//		return ranges.toArray(new StyleRange[ranges.size()]);
//	}
}
