/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.eclipse.wizards.project;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.osgi.service.component.annotations.Reference;
import org.ruminaq.eclipse.api.EclipseExtensionHandler;

/**
 * Creates maven pom file.
 *
 * @author Marek Jagielski
 */
public final class PomFile {

    private static final String POM_FILE_PATH = "pom.xml";

    private static final String POM_TEMPLATE = "newProjectPom.xml";

    private static final String XMLNS = "xmlns";

    private static final String DEPENDENCIES = "dependencies";

    private static final String DEPENDENCY = "dependency";

    private static final String GROUP_ID = "groupId";

    private static final String ARTIFACT_ID = "artifactId";

    private static final String VERSION = "version";

    private static final String REPOSITORIES = "repositories";

    private static final String REPOSITORY = "repository";

    private static final String ID = "id";

    private static final String URL = "url";

    private static final String M2_REPO_URL = "https://s3.amazonaws.com/org-ruminaq-s3-m2/releases";

	@Reference
	private EclipseExtensionHandler extensions;

    /**
     * Creates maven pom file.
     *
     * @param project Eclipse IProject reference
     */
    public void createPOMFile(IProject project) throws JDOMException, IOException, CoreException {
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(CreateProjectWizard.class.getResourceAsStream(POM_TEMPLATE));
        Element root = document.getRootElement();
        Namespace ns = root.getNamespace(XMLNS);
        Element dependencies = root.getChild(DEPENDENCIES, ns);

        extensions.getMavenDependencies().forEach(dep -> {
            Element dependency = new Element(DEPENDENCY, dependencies.getNamespace());
            dependency.addContent(new Element(GROUP_ID, dependency.getNamespace()).setText(dep.getValue0()));
            dependency.addContent(new Element(ARTIFACT_ID, dependency.getNamespace()).setText(dep.getValue1()));
            dependency.addContent(new Element(VERSION, dependency.getNamespace()).setText(dep.getValue2()));
            dependencies.addContent(dependency);
        });

        Element repositories = root.getChild(REPOSITORIES, ns);
        Element repository = new Element(REPOSITORY, repositories.getNamespace());
        repository.addContent(new Element(ID, repository.getNamespace()).setText("System in Cloud"));
        repository.addContent(new Element(URL, repository.getNamespace()).setText(M2_REPO_URL));
        repositories.addContent(repository);

        IFile pomFile = project.getFile(POM_FILE_PATH);
        pomFile.create(
                new ByteArrayInputStream(
                        new XMLOutputter(Format.getPrettyFormat()
                                .setOmitDeclaration(true)
                                .setLineSeparator(System.lineSeparator()))
                        .outputString(document).getBytes(StandardCharsets.UTF_8.name())),
                       true, null);
    }
}
