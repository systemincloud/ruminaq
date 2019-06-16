/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.it.tests.api;

import org.apache.maven.model.Model;
import org.eclipse.core.resources.IProject;

public interface EclipseTestExtension {

  default void verifyProject(IProject project) {
  }

  default void verifyPom(Model model) {
  }

}
