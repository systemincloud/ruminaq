package org.ruminaq.tasks.styles;

import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;
import org.ruminaq.util.StyleUtil;

public class InternalPortStyle {

	private static final IColorConstant GATE_FOREGROUND = new ColorConstant(0,   0,   0);
	private static final IColorConstant GATE_BACKROUND  = new ColorConstant(255, 255, 255);

	public static Style getStyle(Diagram diagram) {
		final String styleId = "INTERNAL_PORT_STYLE"; //$NON-NLS-1$

		Style style = StyleUtil.findStyle(diagram, styleId);
		if (style == null) {
			IGaService gaService = Graphiti.getGaService();
			style = gaService.createStyle(diagram, styleId);
			style.setForeground(gaService.manageColor(diagram, GATE_FOREGROUND));
			style.setBackground(gaService.manageColor(diagram, GATE_BACKROUND));
		}
		return style;
	}
	
}
