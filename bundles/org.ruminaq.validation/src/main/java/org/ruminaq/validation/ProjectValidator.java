package org.ruminaq.validation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.Validator;

public class ProjectValidator {

	public static void validateOnSave(Resource resource, IProgressMonitor monitor) {
		String pathString = resource.getURI().toPlatformString(true);
		IPath path = Path.fromOSString(pathString);
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		validate(file, monitor);
	}

	private static void validate(IFile file, IProgressMonitor monitor) {
		Validator[] validators = ValidationFramework.getDefault().getValidatorsFor(file);
		for(Validator v : validators) v.validate(file, IResourceDelta.CHANGED, null, monitor);
	}

    public static void validate(IResource resource, IProgressMonitor monitor) {
		Validator[] validators = ValidationFramework.getDefault().getValidatorsFor(resource);
		for(Validator v : validators) 	v.validate(resource, IResourceDelta.CHANGED, null, monitor);
    }

    public static void validate(IResourceDelta delta, IProgressMonitor monitor) {
		IResource resource = delta.getResource();
		Validator[] validators = ValidationFramework.getDefault().getValidatorsFor(resource);
		for(Validator v : validators) v.validate(resource, delta.getKind(), null, monitor);
    }
}
