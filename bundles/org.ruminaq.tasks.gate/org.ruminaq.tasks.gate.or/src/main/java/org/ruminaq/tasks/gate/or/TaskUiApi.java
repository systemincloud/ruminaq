package org.ruminaq.tasks.gate.or;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.widgets.Composite;
import org.osgi.framework.Version;
import org.ruminaq.tasks.api.IPropertySection;
import org.ruminaq.tasks.api.ITaskUiApi;

public class TaskUiApi implements ITaskUiApi {

  private String symbolicName;
  private Version version;

  @Override
  public String getSymbolicName() {
    return symbolicName;
  }

  @Override
  public Version getVersion() {
    return version;
  }

  public TaskUiApi(String symbolicName, Version version) {
    this.symbolicName = symbolicName;
    this.version = version;
  }

  @Override
  public IPropertySection createPropertySection(Composite parent,
      PictogramElement pe, TransactionalEditingDomain ed,
      IDiagramTypeProvider dtp) {
    return new PropertySection(parent, pe, ed, dtp);
  }
}
