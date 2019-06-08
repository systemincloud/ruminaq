/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.ruminaq.eclipse.wizards.project;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.ruminaq.eclipse.Messages;
import org.ruminaq.eclipse.RuminaqException;
import org.ruminaq.eclipse.api.EclipseExtension;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.util.ServiceUtil;
import org.slf4j.Logger;

/**
 * Creates maven pom file.
 *
 * @author Marek Jagielski
 */
public final class PomFile {

  private static final Logger LOGGER = ModelerLoggerFactory
      .getLogger(PomFile.class);

  private static final String POM_FILE_PATH = "pom.xml"; //$NON-NLS-1$

  private static final String POM_TEMPLATE = "newProjectPom.xml"; //$NON-NLS-1$

  private static final String M2_REPO_ID = "Ruminaq"; //$NON-NLS-1$

  private static final String M2_REPO_URL = "https://s3.amazonaws.com/org-ruminaq-s3-m2/releases"; //$NON-NLS-1$

//  private Collection<EclipseExtension> extensions = ServiceUtil
//      .getServicesAtLatestVersion(PomFile.class,
//          EclipseExtension.class);

  /**
   * Creates maven pom file.
   *
   * @param project Eclipse IProject reference
   * @throws RuminaqException something went wrong
   */
  public void createPomFile(IProject project) throws RuminaqException {
    var reader = new MavenXpp3Reader();
    Model model;
    try {
      model = reader.read(PomFile.class.getResourceAsStream(POM_TEMPLATE));
    } catch (IOException | XmlPullParserException e) {
      LOGGER.error(Messages.createPomFileFailed, e);
      throw new RuminaqException(Messages.createPomFileFailed);
    }

//    model.getDependencies().addAll(
//        extensions
//          .stream()
//          .<List<Dependency>>map(EclipseExtension::getMavenDependencies)
//          .<Dependency>flatMap(List::stream)
//          .collect(Collectors.toList()));

    var repository = new Repository();
    repository.setId(M2_REPO_ID);
    repository.setUrl(M2_REPO_URL);
    model.addRepository(repository);

    var writer = new MavenXpp3Writer();
    var contentWriter = new StringWriter();
    try {
      writer.write(contentWriter, model);
    } catch (IOException e) {
      LOGGER.error(Messages.createPomFileFailed, e);
      throw new RuminaqException(Messages.createProjectWizardFailed);
    }

    var content = contentWriter.toString();

    IFile pomFile = project.getFile(POM_FILE_PATH);
    try {
      pomFile.create(new ByteArrayInputStream(content.getBytes()), true, new NullProgressMonitor());
    } catch (CoreException e) {
      LOGGER.error(Messages.createPomFileFailed, e);
      throw new RuminaqException(Messages.createProjectWizardFailed);
    }
  }
}
