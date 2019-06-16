/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.javatask.it.tests;

import org.apache.maven.model.Model;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.eclipse.it.tests.api.EclipseTestExtension;

@Component
public class CreateRuminaqProjectTest implements EclipseTestExtension {

  void verifyPom(Model model) {

  }
}
