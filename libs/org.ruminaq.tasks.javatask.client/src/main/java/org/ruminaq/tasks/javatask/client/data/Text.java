/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.javatask.client.data;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Marek Jagielski
 */
public class Text extends Data {

  private List<String> values = new LinkedList<>();

  public Text(List<Integer> dims, List<String> values) {
    super(dims);
    this.values.addAll(values);
  }

  public Text(String value) {
    super(1);
    this.values.add(value);
  }

  public List<String> getValues() {
    return values;
  }

  public String getValue() {
    return values.get(0);
  }

}
