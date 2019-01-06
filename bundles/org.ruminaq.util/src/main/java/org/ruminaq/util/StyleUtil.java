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
package org.ruminaq.util;

import java.util.Collection;

import org.eclipse.graphiti.mm.StyleContainer;
import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.util.IColorConstant;

public class StyleUtil {

	public static Style findStyle(StyleContainer styleContainer, String id) {
		Collection<Style> styles = styleContainer.getStyles();
		if(styles != null)
			for(Style style : styles)
				if(id.equals(style.getId())) return style;
		return null;
	}

	public static Style getStyleForPolygon(Diagram diagram) {
		final String styleId = "POLYGON-ARROW"; //$NON-NLS-1$

		Style style = findStyle(diagram, styleId);

		if(style == null) {
			IGaService gaService = Graphiti.getGaService();
			style = gaService.createStyle(diagram, styleId);
			style.setForeground(gaService.manageColor(diagram, IColorConstant.BLACK));
			style.setBackground(gaService.manageColor(diagram, IColorConstant.BLACK));
			style.setLineWidth(1);
		}
		return style;
	}
}
