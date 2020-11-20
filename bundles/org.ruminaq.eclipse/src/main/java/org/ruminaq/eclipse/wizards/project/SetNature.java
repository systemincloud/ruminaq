/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.project;

import java.util.Objects;
import java.util.Optional;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.ruminaq.eclipse.Messages;
import org.ruminaq.eclipse.RuminaqException;
import org.ruminaq.eclipse.RuminaqProjectNature;
import org.ruminaq.util.Result;
import org.ruminaq.util.Try;

/**
 * Add eclipse project natures.
 *
 * @author Marek Jagielski
 */
public final class SetNature {

  private SetNature() {
    // just statics
  }

  /**
   * Add eclipse project natures.
   *
   * @param project Eclipse IProject reference
   */
  public static Try<RuminaqException> execute(IProject project) {
    Optional<IProjectDescription> description = Optional.of(project)
        .map(p -> Result.attempt(p::getDescription)).map(r -> r.orElse(null))
        .filter(Objects::nonNull);
    description.ifPresent(d -> d.setNatureIds(new String[] { JavaCore.NATURE_ID,
        RuminaqProjectNature.ID, IMavenConstants.NATURE_ID }));
    return description
        .map(d -> Try.check(() -> project.setDescription(d, null))
            .<RuminaqException>wrapError(e -> new RuminaqException(
                Messages.createProjectWizardFailedNature, e)))
        .orElseGet(Try::success);
  }
}
