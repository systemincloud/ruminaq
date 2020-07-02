/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.randomgenerator.gui.api;

import java.util.List;

import org.ruminaq.model.ruminaq.DataType;

/**
 * Service api providing RandomGenerator extension.
 *
 * @author Marek Jagielski
 */
public interface RandomGeneratorExtension {

  List<Class<? extends DataType>> getDataTypes();

}
