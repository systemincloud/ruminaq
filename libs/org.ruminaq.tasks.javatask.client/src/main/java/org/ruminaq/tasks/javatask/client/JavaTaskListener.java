/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.javatask.client;

import org.slf4j.Logger;

/**
 *
 * @author Marek Jagielski
 */
public interface JavaTaskListener
    extends InputPortListener, OutputPortListener {

  void externalData(int i);

  void sleep(long l);

  void generatorPause();

  boolean generatorIsPaused();

  void generatorResume();

  void generatorEnd();

  void exitRunner();

  String getParameter(String key);

  Object runExpression(String expression);

  Logger log();
}
