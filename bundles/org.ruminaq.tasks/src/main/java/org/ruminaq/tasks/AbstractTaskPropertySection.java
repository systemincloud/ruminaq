package org.ruminaq.tasks;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Reference;
import org.ruminaq.launch.LaunchListener;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.tasks.api.IPropertySection;
import org.ruminaq.tasks.api.ITaskUiApi;
import org.ruminaq.tasks.api.TasksUiManagerHandler;
import org.ruminaq.util.EclipseUtil;

public abstract class AbstractTaskPropertySection extends GFPropertySection
    implements ITabbedPropertyConstants, LaunchListener {

  @Reference
  private TasksUiManagerHandler tasks;

  protected IPropertySection propertySection;

  private String created = null;
  private Composite parent;

  private List<Control> notChanged;

  protected ITaskUiApi getTaskUiApi() {
    PictogramElement pe = getSelectedPictogramElement();
    if (pe == null)
      return null;
    EObject bo = Graphiti.getLinkService()
        .getBusinessObjectForLinkedPictogramElement(pe);
    if (bo instanceof Task) {
      Task task = (Task) bo;
      for (ITaskUiApi t : tasks.getTasks(getPrefix())) {
//        if (t.getSymbolicName().equals(task.getBundleName()) && TaskProvider
//            .compare(t.getVersion(), Version.parseVersion(task.getVersion())))
          return t;
      }
    }
    return null;
  }

  protected abstract String getPrefix();

  @Override
  public void createControls(Composite parent,
      TabbedPropertySheetPage tabbedPropertySheetPage) {
    super.createControls(parent, tabbedPropertySheetPage);
    this.parent = parent;
    initLaunchListener();
  }

  protected abstract boolean isRunning();

  protected abstract void initLaunchListener();

  protected void create(Composite parent) {
    ITaskUiApi tua = getTaskUiApi();
    if (tua == null)
      return;
    propertySection = tua.createPropertySection(parent,
        getSelectedPictogramElement(),
        getDiagramContainer().getDiagramBehavior().getEditingDomain(),
        getDiagramTypeProvider());
    this.notChanged = new LinkedList<>();
    EclipseUtil.setEnabledRecursive(parent, !isRunning(), notChanged);
  }

  @Override
  public void refresh() {
    PictogramElement pe = getSelectedPictogramElement();
    if (pe == null)
      return;
    if (!getId(pe).equals(created)) {
      for (Control control : parent.getChildren())
        control.dispose();
      create(parent);
      parent.layout();
      this.created = getId(getSelectedPictogramElement());
    }

    super.refresh();
    if (propertySection != null)
      propertySection.refresh(getSelectedPictogramElement(),
          getDiagramContainer().getDiagramBehavior().getEditingDomain());
  }

  private String getId(PictogramElement pe) {
    EObject bo = Graphiti.getLinkService()
        .getBusinessObjectForLinkedPictogramElement(pe);
    if (bo instanceof Task) {
      Task task = (Task) bo;
      return task.getId();
    }
    return null;
  }

  @Override
  public void launched() {
    Display.getDefault().asyncExec(new Runnable() {
      public void run() {
        notChanged = new LinkedList<>();
        if (!parent.isDisposed())
          EclipseUtil.setEnabledRecursive(parent, false, notChanged);
      }
    });
  }

  @Override
  public void stopped() {
    Display.getDefault().asyncExec(new Runnable() {
      public void run() {
        if (!parent.isDisposed()) {
          EclipseUtil.setEnabledRecursive(parent, true,
              new LinkedList<Control>());
          for (Control c : notChanged)
            c.setEnabled(false);
        }
      }
    });
  }

  @Override
  public void dirmiStarted() {
  }
}
