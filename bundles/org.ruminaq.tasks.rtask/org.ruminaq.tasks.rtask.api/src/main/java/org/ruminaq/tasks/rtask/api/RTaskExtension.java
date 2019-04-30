/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.rtask.api;

import java.util.List;

import org.javatuples.Pair;

/**
 *
 * @author Marek Jagielski
 */
public interface RTaskExtension {
    List<Pair<String, String>> getPythonTaskDatas();
}
