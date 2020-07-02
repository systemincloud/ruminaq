/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.javatask.impl.service;

import org.ruminaq.runner.impl.data.DataI;
import org.ruminaq.tasks.javatask.client.data.Data;

public interface JavaTaskService {
  Data toJavaTaskData(DataI dataI, Class<? extends Data> to);

  Data toJavaTaskData(DataI dataI);

  DataI fromJavaTaskData(Data data, boolean copy);
}
