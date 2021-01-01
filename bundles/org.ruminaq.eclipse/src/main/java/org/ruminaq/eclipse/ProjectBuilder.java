/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse;

import org.apache.maven.cli.MavenCli;
import org.eclipse.core.resources.IResource;

/**
 * Execute maven package.
 *
 * @author Marek Jagielski
 */
public enum ProjectBuilder {
  INSTANCE;

  /**
   * Eclipse project resource.
   *
   * @param project eclipse resource
   */
  public void build(IResource project) {
    String path = project.getLocation().toOSString();
    System.setProperty("maven.multiModuleProjectDirectory", "");
    new MavenCli().doMain(new String[] { "package", "-T 4C", "-DskipTests" },
        path, null, null);
  }
}
