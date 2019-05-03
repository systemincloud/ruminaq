package org.ruminaq.tasks.embeddedtask;

import java.util.Map;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.widgets.Composite;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.tasks.api.IPropertySection;
import org.ruminaq.tasks.api.ITaskUiApi;
import org.ruminaq.tasks.embeddedtask.ui.IEmbeddedTaskUiApi;
import org.ruminaq.tasks.userdefined.IUserDefinedUiApi;

@Component
public class TaskUiApi implements ITaskUiApi, IEmbeddedTaskUiApi, IUserDefinedUiApi {

  private String  symbolicName;
  private Version version;

    @Activate
    void activate(Map<String, Object> properties) {
        Bundle b = FrameworkUtil.getBundle(getClass());
        symbolicName = b.getSymbolicName();
        version = b.getVersion();
    }

    @Override
    public String getSymbolicName() {
        return symbolicName;
    }

    @Override
    public Version getVersion() {
        return version;
    }

    @Override
    public IPropertySection createPropertySection(Composite parent, PictogramElement pe, TransactionalEditingDomain ed,
            IDiagramTypeProvider dtp) {
        return new PropertySection(parent, pe, ed, dtp);
    }

    @Override
    public IPropertySection createParametersSection(
            Composite parent, PictogramElement pe,
            TransactionalEditingDomain ed,
            IDiagramTypeProvider dtp) {
        return new ParametersSection(parent, pe, ed, dtp);
    }

    @Override
    public String getModelerVersion() {
        return ""; //props.getProperty("modelerVersion").replace(Constants.SNAPSHOT, "");
    }
}
