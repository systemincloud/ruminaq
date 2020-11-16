/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.project;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.ruminaq.eclipse.Messages;
import org.ruminaq.eclipse.RuminaqException;
import org.ruminaq.util.EclipseUtil;

/**
 * Add eclipse project builders.
 *
 * @author Marek Jagielski
 */
public final class Builders {

  private static final String EXTERNALTOOLBUILDERS = ".externalToolBuilders";

  private static final String BUILDER_CONFIG_MVN = IMavenConstants.BUILDER_ID
      + ".launch";

  private Builders() {
  }

  /**
   * Configure builders of eclipse project.
   *
   * @param project Eclipse IProject reference
   */
  static void configureBuilders(IProject project) {
    EclipseUtil.createFolderWithParents(project, EXTERNALTOOLBUILDERS);

    try (InputStream confFile = Builders.class
        .getResourceAsStream(BUILDER_CONFIG_MVN)) {
      IFile outputFile = project.getFolder(EXTERNALTOOLBUILDERS)
          .getFile(BUILDER_CONFIG_MVN);
      outputFile.create(confFile, true, null);
      ICommand command = Arrays.stream(project.getDescription().getBuildSpec())
          .filter(
              cmd -> IMavenConstants.BUILDER_ID.equals(cmd.getBuilderName()))
          .findFirst().orElseThrow(IOException::new);
      command
          .setBuilderName("org.eclipse.ui.externaltools.ExternalToolBuilder");
      command.getArguments().put("LaunchConfigHandle",
          "<project>/.externalToolBuilders/org.eclipse.m2e.core.maven2Builder.launch");
    } catch (CoreException | IOException e) {
      throw new RuminaqException(
          Messages.createProjectWizardFailedConfigureBuilders, e);
    }
  }
}
