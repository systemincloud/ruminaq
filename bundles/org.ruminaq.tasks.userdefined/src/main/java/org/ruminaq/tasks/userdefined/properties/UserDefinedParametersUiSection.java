package org.ruminaq.tasks.userdefined.properties;

import org.eclipse.swt.widgets.Composite;
import org.ruminaq.tasks.AbstractTaskPropertySection;
import org.ruminaq.tasks.userdefined.IUserDefinedUiApi;

public abstract class UserDefinedParametersUiSection
    extends AbstractTaskPropertySection {

  protected void create(Composite parent) {
    IUserDefinedUiApi uda = (IUserDefinedUiApi) getTaskUiApi();
    if (uda == null)
      return;
    propertySection = uda.createParametersSection(parent,
        getSelectedPictogramElement(),
        getDiagramContainer().getDiagramBehavior().getEditingDomain(),
        getDiagramTypeProvider());
  }
}
