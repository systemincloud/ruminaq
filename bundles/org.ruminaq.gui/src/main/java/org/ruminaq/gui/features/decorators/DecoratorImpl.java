/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.decorators;

import java.util.Collection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.tb.IDecorator;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.DecoratorExtension;

/**
 * Service DecoratorExtension implementation.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=5" })
public class DecoratorImpl implements DecoratorExtension {

  @Override
  public Collection<IDecorator> getDecorators(PictogramElement pe) {
    return null;
  }
}
