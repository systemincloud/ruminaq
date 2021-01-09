/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.paste;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.ui.features.AbstractPasteFeature;
import org.ruminaq.gui.api.PasteElementFeatureExtension;
import org.ruminaq.gui.model.diagram.FlowSourceShape;
import org.ruminaq.gui.model.diagram.FlowTargetShape;
import org.ruminaq.gui.model.diagram.RuminaqDiagram;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.gui.model.diagram.SimpleConnectionPointShape;
import org.ruminaq.gui.model.diagram.SimpleConnectionShape;
import org.ruminaq.model.ruminaq.SimpleConnection;
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
    List<RuminaqShapePasteFeature<? extends RuminaqShape>> pasteFeatures = getPasteFeatures();
    pasteFeatures.stream().forEach(pf -> pf.paste(context));
    pasteSimpleConnections(pasteFeatures, getFeatureProvider());
  }

  private void pasteSimpleConnections(
      List<RuminaqShapePasteFeature<? extends RuminaqShape>> pfs,
      IFeatureProvider fp) {
    Map<Anchor, Anchor> anchors = pfs.stream()
        .filter(PasteAnchorTracker.class::isInstance)
        .map(PasteAnchorTracker.class::cast).map(PasteAnchorTracker::getAnchors)
        .flatMap(map -> map.entrySet().stream())
        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
    List<Anchor> oldFlowSources = anchors.keySet().stream()
        .filter(a -> Optional.of(a.getParent())
            .filter(FlowSourceShape.class::isInstance).isPresent())
        .collect(Collectors.toList());
    List<Anchor> oldFlowTargets = anchors.keySet().stream()
        .filter(a -> Optional.of(a.getParent())
            .filter(FlowTargetShape.class::isInstance).isPresent())
        .collect(Collectors.toList());

    Optional<RuminaqDiagram> oldDiagram = oldFlowSources.stream().findFirst()
        .map(Anchor::getParent).filter(RuminaqShape.class::isInstance)
        .map(RuminaqShape.class::cast).map(PasteElementFeature::getDiagram);

    List<SimpleConnectionShape> simpleConnectionsToCopy = oldDiagram
        .map(RuminaqDiagram::getConnections).map(EList::stream)
        .orElseGet(Stream::empty)
        .filter(SimpleConnectionShape.class::isInstance)
        .map(SimpleConnectionShape.class::cast).filter(scs -> {
          return false;
        }).collect(Collectors.toList());

    List<SimpleConnectionPointShape> simpleConnectionPointsToCopy = oldDiagram
        .map(RuminaqDiagram::getChildren).map(EList::stream)
        .orElseGet(Stream::empty)
        .filter(SimpleConnectionPointShape.class::isInstance)
        .map(SimpleConnectionPointShape.class::cast).filter(scs -> {
          return false;
        }).collect(Collectors.toList());
  }

  private static RuminaqDiagram getDiagram(ContainerShape shape) {
    return Optional.ofNullable(shape).filter(RuminaqDiagram.class::isInstance)
        .map(RuminaqDiagram.class::cast)
        .orElseGet(() -> getDiagram(
            Optional.ofNullable(shape).map(ContainerShape::eContainer)
                .filter(ContainerShape.class::isInstance)
                .map(ContainerShape.class::cast).orElse(null)));
  }
}
