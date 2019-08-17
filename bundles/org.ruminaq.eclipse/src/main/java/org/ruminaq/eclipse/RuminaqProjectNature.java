/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.ruminaq.eclipse.builder.RuminaqBuilder;

/**
 * Eclipse project nature configuration.
 *
 * @author Marek Jagielski
 */
public class RuminaqProjectNature implements IProjectNature {

  public static final String ID = RuminaqProjectNature.class.getCanonicalName();

  private IProject project = null;

  @Override
  public IProject getProject() {
    return project;
  }

  @Override
  public void setProject(IProject project) {
    this.project = project;
  }

  @Override
  public void configure() throws CoreException {
    IProjectDescription description = project.getDescription();
    List<ICommand> commands = Arrays.asList(description.getBuildSpec());

    if (commands.stream().map(ICommand::getBuilderName)
        .anyMatch(RuminaqBuilder.ID::equals)) {
      return;
    }

    ICommand command = description.newCommand();
    command.setBuilderName(RuminaqBuilder.ID);
    commands.add(command);

    description.setBuildSpec(commands.stream().toArray(ICommand[]::new));
    project.setDescription(description, null);
  }

  @Override
  public void deconfigure() throws CoreException {
    IProjectDescription description = getProject().getDescription();
    description.setBuildSpec(Stream.of(description.getBuildSpec())
        .filter(cmd -> !cmd.getBuilderName().equals(RuminaqBuilder.ID))
        .toArray(ICommand[]::new));
    project.setDescription(description, null);
  }
}
