/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.api;

import java.util.Collection;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.tb.IContextButtonEntry;

/**
 * Service api providing Graphiti DomainContextButtonPadData.
 *
 * @author Marek Jagielski
 */
public interface DomainContextButtonPadDataExtension {

  /**
   * Return all DomainContextButtonPadData.
   *
   * @param fp      IFeatureProvider of Graphiti
   * @param context IPictogramElementContext of Graphiti
   * @return collection with DomainContextButtonPadData
   */
  Collection<IContextButtonEntry> getContextButtonPad(IFeatureProvider fp,
      IPictogramElementContext context);

}
