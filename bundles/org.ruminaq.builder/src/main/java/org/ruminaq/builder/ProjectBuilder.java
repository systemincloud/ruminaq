/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.builder;

import org.apache.maven.cli.MavenCli;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;

public enum ProjectBuilder {
  INSTANCE;

  public void build(IProject project) throws CoreException {
    String path = project.getLocation().toOSString();
    MavenCli cli = new MavenCli();
    System.setProperty("maven.multiModuleProjectDirectory", "");
    int result = cli.doMain(new String[] { "package", "-T 4C", "-DskipTests" },
        path, null, null);
    if (result != 0)
      throw new CoreException(Status.CANCEL_STATUS);
  }
}
