/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.javatask.client.data;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Marek Jagielski
 */
public abstract class Data {

  protected List<Integer> dims;

  public List<Integer> getDimensions() {
    return dims;
  }

  protected int nElements = 1;

  public int getNumberOfElements() {
    return nElements;
  }

  public Data(Integer... dims) {
    this(Arrays.asList(dims));
  }

  public Data(List<Integer> dims) {
    this.dims = dims;
    nElements = dims.stream().reduce(1, (a, b) -> a * b);
  }

  public boolean equalDimensions(Data data) {
    if (data == null) {
      return false;
    }
    Iterator<Integer> it = data.getDimensions().iterator();
    for (Integer i : dims) {
      if (!it.hasNext() || !it.next().equals(i)) {
        return false;
      }
    }
    if (it.hasNext()) {
      return false;
    }
    return true;
  }
}
