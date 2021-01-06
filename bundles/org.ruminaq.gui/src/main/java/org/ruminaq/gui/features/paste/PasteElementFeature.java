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
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.ui.features.AbstractPasteFeature;
import org.ruminaq.gui.api.PasteElementFeatureExtension;
import org.ruminaq.gui.model.diagram.FlowSourceShape;
import org.ruminaq.gui.model.diagram.FlowTargetShape;
import org.ruminaq.gui.model.diagram.RuminaqDiagram;
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

//    Optional<RuminaqDiagram> oldDiagram = anchors.entrySet().stream().findFirst().map(Map.Entry::getKey)
//        .map(Anchor::getParent).filter(RuminaqShape.class::isInstance)
//        .map(RuminaqShape.class::cast).map(PasteElementFeature::getDiagram);
//
//    Map<Connection, AnchorContainer> simpleConnectionPointAtTheEnd = new HashMap<>();
//    Map<Connection, AnchorContainer> simpleConnectionPointAtTheStart = new HashMap<>();
//    for (Connection c : oldDiagram.get().getConnections()) {
//          if (oldFlowSources.contains(c.getStart())
//              && oldFlowTargets.contains(c.getEnd())) {
//            
//          }
//      Optional<Anchor> sa = Optional.of(c).map(Connection::getStart);
//      Optional<Anchor> ea = Optional.of(c).map(Connection::getEnd);
//      if (ea.map(Anchor::getParent)
//          .filter(SimpleConnectionPointShape.class::isInstance).isPresent()) {
//        simpleConnectionPointAtTheEnd.put(c, ea.get().getParent());
//      }
//      if (sa.map(Anchor::getParent)
//          .filter(SimpleConnectionPointShape.class::isInstance).isPresent()) {
//        simpleConnectionPointAtTheStart.put(c, sa.get().getParent());
//      }
//    }
//    PasteSimpleConnections psc = new PasteSimpleConnections(flowSources,
//        flowTargets, peBos, anchors, fp);
//    psc.paste(null);
  }

  private static RuminaqDiagram getDiagram(RuminaqShape shape) {
    return Optional.ofNullable(shape).filter(RuminaqDiagram.class::isInstance)
        .map(RuminaqDiagram.class::cast).orElseGet(() -> getDiagram(Optional
            .ofNullable(shape).map(RuminaqShape::getParent).orElse(null)));
  }
}
