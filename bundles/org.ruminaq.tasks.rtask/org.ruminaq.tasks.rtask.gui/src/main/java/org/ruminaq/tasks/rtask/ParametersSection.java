package org.ruminaq.tasks.rtask;

import java.util.Map;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.swt.widgets.Composite;
import org.ruminaq.gui.properties.AbstractParametersSection;
import org.ruminaq.model.util.ModelUtil;
import org.ruminaq.tasks.api.IPropertySection;
import org.ruminaq.tasks.rtask.model.rtask.RTask;

public class ParametersSection extends AbstractParametersSection
    implements IPropertySection {

  private RTask bo;

  private TransactionalEditingDomain ed;

  public ParametersSection(Composite parent, PictogramElement pe,
      TransactionalEditingDomain ed, IDiagramTypeProvider dtp) {
    super.createControls(parent, null);
  }

  @Override
  protected boolean isDefault() {
    return true;
  }

  @Override
  public void refresh(PictogramElement pe, TransactionalEditingDomain ed) {
    if (pe != null) {
      Object bo = Graphiti.getLinkService()
          .getBusinessObjectForLinkedPictogramElement(pe);
      if (bo != null && bo instanceof RTask)
        this.bo = (RTask) bo;
    }
    this.ed = ed;
    super.refresh();
  }

  @Override
  protected Map<String, String> getActualParams() {
    return bo.getParameters();
  }

  @Override
  protected Map<String, String> getDefaultParams() {
    return bo.getDefaultParameters();
  }

  @Override
  protected void saveParameter(final String key, final String value) {
    ModelUtil.runModelChange(new Runnable() {
      public void run() {
        if (bo == null)
          return;
        bo.getParameters().put(key, value);
      }
    }, ed, "Change parameter");
  }
}
