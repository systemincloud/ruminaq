package org.ruminaq.tasks.gate;

import org.osgi.framework.Version;
import org.ruminaq.eclipse.api.EclipseExtension;
import org.ruminaq.tasks.api.ITaskApi;
import org.ruminaq.tasks.gate.model.gate.GatePackage;

public class TaskApi implements ITaskApi, EclipseExtension {

	private String  symbolicName;
	private Version version;

    @Override
    public String getSymbolicName() {
        return symbolicName;
    }

    @Override
    public Version getVersion() {
        return version;
    }

    @Override
    public void initEditor() {
        GatePackage.eINSTANCE.getClass();
    }

	public TaskApi(String symbolicName, Version version) {
		this.symbolicName = symbolicName;
		this.version      = version;
	}
}
