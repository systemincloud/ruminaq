/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.api;

import java.util.List;

/**
 * Service api providing UserDefinedTaskExtension features.
 *
 * @author Marek Jagielski
 */
public interface UserDefinedTaskExtension {

  List<String> getSupportedData();
  
  String getCannonicalDataName(String dataName);

}
