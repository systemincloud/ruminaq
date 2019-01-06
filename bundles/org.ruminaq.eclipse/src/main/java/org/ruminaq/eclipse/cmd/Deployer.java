/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.eclipse.cmd;

import java.io.InputStream;

import org.apache.maven.model.Model;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.m2e.core.MavenPlugin;

/**
 * 
 * @author Marek Jagielski
 */
public class Deployer {

	private IProject project;

	public Deployer(IProject project) {
		this.project = project;
	}

	public void deployToCloud() {
        IFolder targetFolder = project.getFolder("target");
		IFile pomFile = project.getFile("pom.xml");
        if(!targetFolder.exists()) return;
        if(!targetFolder.exists()) return;

        Model model;
		try {
			model = MavenPlugin.getMavenModelManager().readMavenModel(pomFile);
		} catch (CoreException e1) { return; }
        String fileName = model.getArtifactId() + "-" + model.getVersion() + "." + model.getPackaging();

        IFile jarfile = targetFolder.getFile(fileName);
        if(!jarfile.exists()) return;
        InputStream in;
        try {
			in = jarfile.getContents();
		} catch (CoreException e) {	return;	}


	}

}
