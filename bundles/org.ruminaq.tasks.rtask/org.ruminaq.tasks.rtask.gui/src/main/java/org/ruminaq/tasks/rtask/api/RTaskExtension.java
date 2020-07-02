/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.rtask.api;

import java.util.Map;

/**
 * Service api providing RTask configuration.
 *
 * @author Marek Jagielski
 */
public interface RTaskExtension {

  Map<String, String> getDataTypes();

}
