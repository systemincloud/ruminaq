/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.paste;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.ui.features.AbstractPasteFeature;
import org.ruminaq.gui.api.PasteElementFeatureExtension;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.util.ServiceUtil;

/**
 * Paste group of RuminaqShapes.
 *
 * @author Marek Jagielski
 */
public class PasteElementFeature extends AbstractPasteFeature {

  public PasteElementFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canPaste(IPasteContext context) {
    List<RuminaqShapePasteFeature<? extends RuminaqShape>> features = getPasteFeatures();
    return !features.isEmpty()
        && features.stream().allMatch(pf -> pf.canPaste(context));
  }

  private List<RuminaqShapePasteFeature<? extends RuminaqShape>> getPasteFeatures() {
    List<RuminaqShape> objects = Stream.of(getFromClipboard())
        .filter(RuminaqShape.class::isInstance).map(RuminaqShape.class::cast)
        .collect(Collectors.toList());

    if (objects.isEmpty()) {
      return Collections.emptyList();
    }

    int xMin = objects.stream().mapToInt(RuminaqShape::getX).min()
        .orElseThrow(NoSuchElementException::new);

    int yMin = objects.stream().mapToInt(RuminaqShape::getY).min()
        .orElseThrow(NoSuchElementException::new);

    return objects.stream()
        .<RuminaqShapePasteFeature<? extends RuminaqShape>>map(rs -> ServiceUtil
            .getServicesAtLatestVersion(
                PasteElementFeature.class, PasteElementFeatureExtension.class)
            .stream()
            .map(ext -> ext.getFeature(getFeatureProvider(),
                rs.getModelObject(), rs, xMin, yMin))
            .filter(Objects::nonNull).findFirst().orElse(null))
        .filter(Objects::nonNull).collect(Collectors.toList());
  }

  @Override
  public void paste(IPasteContext context) {
    getPasteFeatures().stream().forEach(pf -> pf.paste(context));
    
  }
}
