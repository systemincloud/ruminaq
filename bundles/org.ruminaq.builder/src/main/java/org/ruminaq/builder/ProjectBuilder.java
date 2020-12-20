/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.builder;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.maven.cli.MavenCli;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.ruminaq.logs.ModelerLoggerFactory;

import ch.qos.logback.classic.Logger;

public enum ProjectBuilder {
  INSTANCE;

  private final Logger logger = ModelerLoggerFactory
      .getLogger(ProjectBuilder.class);

  public void build(IProject project) throws CoreException {
    logger.trace("build");
    String path = project.getLocation().toOSString();
    MavenCli cli = new MavenCli();
    System.setProperty("maven.multiModuleProjectDirectory", "");
    int result = cli.doMain(new String[] { "package", "-T 4C", "-DskipTests" },
        path, null, null);
    if (result != 0)
      throw new CoreException(Status.CANCEL_STATUS);
  }

  public File getBuiltArtifact(IProject project) {
    File file = null;
    String basePath = project.getLocation().toOSString();
    MavenXpp3Reader reader = new MavenXpp3Reader();
    try {
      Model model = reader.read(new FileReader(basePath + "/pom.xml"));
      String path = project.getLocation().toOSString() + "/target/"
          + model.getArtifactId() + "-" + model.getVersion() + ".jar";
      file = new File(path);
    } catch (IOException | XmlPullParserException e) {
      /* */ }
    return file;
  }
}
