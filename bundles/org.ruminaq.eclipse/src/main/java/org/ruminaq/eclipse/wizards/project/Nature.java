/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.ruminaq.eclipse.Messages;
import org.ruminaq.eclipse.RuminaqException;

/**
 * Add eclipse project natures.
 *
 * @author Marek Jagielski
 */
public final class Nature {

  public static final String NATURE_ID =
      "org.ruminaq.eclipse.nature.RuminaqProjectNature"; //$NON-NLS-1$

  private Nature() {
  }

  /**
   * Add eclipse project natures.
   *
   * @param project Eclipse IProject reference
   */
  static void setNatureIds(IProject project) throws RuminaqException {
    IProjectDescription description;
    try {
      description = project.getDescription();
    } catch (CoreException e) {
      throw new RuminaqException(Messages.createProjectWizardFailed, e);
    }
    description.setNatureIds(new String[] {
        JavaCore.NATURE_ID,
        NATURE_ID,
        IMavenConstants.NATURE_ID });
    try {
      project.setDescription(description, null);
    } catch (CoreException e) {
      throw new RuminaqException(Messages.createProjectWizardFailed, e);
    }
  }
}
