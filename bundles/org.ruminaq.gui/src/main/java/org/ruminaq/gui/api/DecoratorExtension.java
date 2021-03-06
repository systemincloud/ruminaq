/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.api;

import java.util.Collection;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.tb.IDecorator;

/**
 * Service api providing Graphiti IDecorators.
 *
 * @author Marek Jagielski
 */
public interface DecoratorExtension {

  /**
   * Check is extension can contain any decorations.
   *
   * @param pe PictogramElement that is going to be decorated
   * @return flag
   */
  boolean forPictogramElement(PictogramElement pe);

  /**
   * Return all decorators that matches to given PictogramElement.
   *
   * @param pe PictogramElement that is going to be decorated
   * @param fp IFeatureProvider
   * @return collection of decorators
   */
  Collection<IDecorator> getDecorators(PictogramElement pe,
      IFeatureProvider fp);

}
