package org.ruminaq.tasks.console;

import org.osgi.service.component.annotations.Component;
import org.ruminaq.eclipse.api.EclipseExtension;
import org.ruminaq.tasks.console.model.console.ConsolePackage;

@Component
public class EclipseExtensionImpl implements EclipseExtension {

    @Override
    public void initEditor() {
        ConsolePackage.eINSTANCE.getClass();
    }
}
