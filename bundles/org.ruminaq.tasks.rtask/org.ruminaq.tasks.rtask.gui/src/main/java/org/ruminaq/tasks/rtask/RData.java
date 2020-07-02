/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.rtask;

import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;

public enum RData {
  INSTANCE;

  public List<Pair<String, String>> getRTaskDatas() {
    List<Pair<String, String>> ret = new LinkedList<>();
    ret.add(new Pair<>("sicrtask", "Bool"));
    ret.add(new Pair<>("sicrtask", "Complex32"));
    ret.add(new Pair<>("sicrtask", "Complex64"));
    ret.add(new Pair<>("sicrtask", "Control"));
    ret.add(new Pair<>("sicrtask", "Decimal"));
    ret.add(new Pair<>("sicrtask", "Int32"));
    ret.add(new Pair<>("sicrtask", "Int64"));
    ret.add(new Pair<>("sicrtask", "Float32"));
    ret.add(new Pair<>("sicrtask", "Float64"));
    ret.add(new Pair<>("sicrtask", "Raw"));
    ret.add(new Pair<>("sicrtask", "Text"));
    // TODO:ret.addAll(RTaskExtensionManager.INSTANCE.getPythonTaskDatas());
    return ret;
  }
}
