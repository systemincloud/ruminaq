package org.ruminaq.tasks;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;
import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Reference;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.LabelUtil;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.tasks.api.ITaskUiApi;
import org.ruminaq.tasks.api.TasksUiManagerHandler;

public abstract class AbstractTaskPropertyFilter
    extends AbstractPropertySectionFilter {

  @Reference
  private TasksUiManagerHandler tasks;

  @Override
  protected boolean accept(PictogramElement pictogramElement) {
    if (Graphiti.getPeService().getPropertyValue(pictogramElement,
        LabelUtil.LABEL_PROPERTY) != null)
      return false;
    EObject bo = Graphiti.getLinkService()
        .getBusinessObjectForLinkedPictogramElement(pictogramElement);
    if (bo instanceof Task) {
      Task task = (Task) bo;
      for (ITaskUiApi t : tasks.getTasks(getPrefix())) {
        Version v = Version.parseVersion(
            task.getVersion().replace(Constants.SNAPSHOT, Constants.QUALIFIER));
        if (t.getSymbolicName().equals(task.getBundleName())
            && t.getVersion().getMajor() == v.getMajor()
            && t.getVersion().getMinor() == v.getMinor()
            && t.getVersion().getMicro() == v.getMicro()
            && t.checkPropertyFilter(task))
          return true;
      }
    }
    return false;
  }

  protected abstract String getPrefix();
}
