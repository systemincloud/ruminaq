package org.ruminaq.tasks.styles;

import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;
import org.ruminaq.util.StyleUtil;

public class TaskStyle {

  private static final IColorConstant TASK_FOREGROUND = new ColorConstant(0, 0,
      0);
  private static final IColorConstant TASK_BACKROUND = new ColorConstant(255,
      255, 255);
  private static final IColorConstant ONLY_LOCAL_TASK_BACKROUND = new ColorConstant(
      220, 220, 220);

  public static Style getStyle(Diagram diagram) {
    final String styleId = "TASK"; //$NON-NLS-1$

    Style style = StyleUtil.findStyle(diagram, styleId);
    if (style == null) {
      IGaService gaService = Graphiti.getGaService();
      style = gaService.createStyle(diagram, styleId);
      style.setForeground(gaService.manageColor(diagram, TASK_FOREGROUND));
      style.setBackground(gaService.manageColor(diagram, TASK_BACKROUND));
    }
    return style;
  }

  public static Style getOnlyLocalStyle(Diagram diagram, boolean create) {
    final String styleId = "ONLY_LOCAL_TASK"; //$NON-NLS-1$

    Style style = StyleUtil.findStyle(diagram, styleId);
    if (style == null && create) {
      IGaService gaService = Graphiti.getGaService();
      style = gaService.createStyle(diagram, styleId);
      style.setForeground(gaService.manageColor(diagram, TASK_FOREGROUND));
      style.setBackground(
          gaService.manageColor(diagram, ONLY_LOCAL_TASK_BACKROUND));

    }
    return style;
  }

  public static Style getOnlyLocalStyle(Diagram diagram) {
    return getOnlyLocalStyle(diagram, true);
  }

}
