package org.ruminaq.prefs;

import java.util.Hashtable;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;

public class ProjectProps extends Props {

	public final static String PROJECT_PROPS = "org.ruminaq.project";

	public final static String MODELER_VERSION = "modeler.version";

	private static Hashtable<IProject, Props> instances = new Hashtable<>();

	public static Props getInstance(IProject project) {
		if (!instances.containsKey(project)) {
			instances.put(project, new ProjectProps(project));
		}
		return instances.get(project);
	}

	private ProjectProps(IProject project) {
		super(project, PROJECT_PROPS, false);
	}

	@Override
	public void preferenceChange(PreferenceChangeEvent arg0) {
	}
}
