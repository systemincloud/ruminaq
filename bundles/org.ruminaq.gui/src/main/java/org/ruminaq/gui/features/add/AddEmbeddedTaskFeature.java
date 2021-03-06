/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.add;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.image.Images;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.EmbeddedTask;

/**
 * IAddFeature for EmbeddedTask.
 *
 * @author Marek Jagielski
 */
@FeatureFilter(AddEmbeddedTaskFeature.Filter.class)
public class AddEmbeddedTaskFeature extends AbstractAddTaskFeature {

  private static class Filter extends AbstractAddFeatureFilter {
    @Override
    public Class<? extends BaseElement> forBusinessObject() {
      return EmbeddedTask.class;
    }
  }

  public static final String NOT_CHOSEN = "???";

  public AddEmbeddedTaskFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  protected String getInsideIconId() {
    return Images.IMG_EMBEDDEDTASK_DIAGRAM_MAIN;
  }

  @Override
  protected String getInsideIconDesc() {
    return NOT_CHOSEN;
  }
}
