/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.paste;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.mm.algorithms.AbstractText;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.styles.AbstractStyle;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.Font;
import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.features.AbstractPasteFeature;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.api.PasteElementFeatureExtension;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.FlowSource;
import org.ruminaq.model.ruminaq.FlowTarget;
import org.ruminaq.model.ruminaq.SimpleConnection;
import org.ruminaq.util.ColorUtil;
import org.ruminaq.util.FontUtil;
import org.ruminaq.util.ServiceUtil;
import org.ruminaq.util.StyleUtil;

public class PasteElementFeature extends AbstractPasteFeature {

	public PasteElementFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canPaste(IPasteContext context) {
		return getPasteFeatures(context).stream()
		    .allMatch(pf -> pf.canPaste(context));
	}

	private List<RuminaqPasteFeature> getPasteFeatures(IPasteContext context) {
		List<RuminaqPasteFeature> pfs = new LinkedList<>();

		List<PictogramElement> objects = Stream.of(getFromClipboard())
		    .filter(o -> o instanceof PictogramElement)
		    .map(o -> (PictogramElement) o).collect(Collectors.toList());

		if (objects.isEmpty()) {
			return pfs;
		}

		int xMin = Stream.of(objects).filter(o -> o instanceof PictogramElement)
		    .map(o -> (PictogramElement) o)
		    .map(PictogramElement::getGraphicsAlgorithm).filter(Objects::nonNull)
		    .mapToInt(GraphicsAlgorithm::getX).min()
		    .orElseThrow(NoSuchElementException::new);

		int yMin = Stream.of(objects).filter(o -> o instanceof PictogramElement)
		    .map(o -> (PictogramElement) o)
		    .map(PictogramElement::getGraphicsAlgorithm).filter(Objects::nonNull)
		    .mapToInt(GraphicsAlgorithm::getY).min()
		    .orElseThrow(NoSuchElementException::new);

		return objects.stream().<RuminaqPasteFeature>map(o -> {
			PictogramElement oldPe = o;
			BaseElement oldBo = Stream
			    .of(getAllBusinessObjectsForPictogramElement(oldPe))
			    .filter(bo -> bo instanceof BaseElement).map(bo -> (BaseElement) bo)
			    .findFirst().orElse(null);

			return ServiceUtil
			    .getServicesAtLatestVersion(
			        PasteElementFeature.class, PasteElementFeatureExtension.class)
			    .stream()
			    .map(ext -> ext.getFeature(getFeatureProvider(), oldBo, oldPe, xMin,
			        yMin))
			    .filter(Objects::nonNull).findFirst()
			    .orElse(new PasteDefaultElementFeature(getFeatureProvider(), oldPe,
			        xMin, yMin));

		}).collect(Collectors.toList());
	}

	@Override
	public void paste(IPasteContext context) {
		List<RuminaqPasteFeature> pfs = getPasteFeatures(context);

		for (RuminaqPasteFeature pf : pfs) {
			pf.paste(context);
			cloneStylesAndFonts(pf.getNewPictogramElements());
		}
		pasteSimpleConnections(pfs, getFeatureProvider());
	}

	private void cloneStylesAndFonts(List<PictogramElement> pes) {
		for (PictogramElement p : pes)
			cloneStylesAndFonts(p);
	}

	private void cloneStylesAndFonts(PictogramElement p) {
		cloneStylesAndFonts(p.getGraphicsAlgorithm());

		if (p instanceof Connection) {
			Connection con = (Connection) p;
			for (ConnectionDecorator cd : con.getConnectionDecorators())
				cloneStylesAndFonts(cd.getGraphicsAlgorithm());
		}

		if (p instanceof ContainerShape) {
			ContainerShape cs = (ContainerShape) p;
			for (Shape ch : cs.getChildren())
				cloneStylesAndFonts(ch);
		}
	}

	private void cloneStylesAndFonts(GraphicsAlgorithm ga) {
		Style s = ga.getStyle();
		Style sTmp;
		if (s != null) {
			sTmp = StyleUtil.findStyle(getDiagram(), s.getId());
			if (sTmp != null)
				ga.setStyle(sTmp);
			else
				ga.setStyle(cloneStylesAndFonts(s));
		}
		cloneStylesAndFonts((AbstractStyle) ga);
		if (ga instanceof AbstractText) {
			AbstractText txt = (AbstractText) ga;
			Font f = FontUtil.findFont(getDiagram(), txt.getFont().getName(),
			    txt.getFont().getSize());
			if (f != null)
				txt.setFont(f);
			else {
				f = EcoreUtil.copy(txt.getFont());
				getDiagram().getFonts().add(f);
				txt.setFont(f);
			}
		}
		for (GraphicsAlgorithm gac : ga.getGraphicsAlgorithmChildren())
			cloneStylesAndFonts(gac);
	}

	private void cloneStylesAndFonts(AbstractStyle as) {
		if (as.getForeground() != null) {
			Color c = ColorUtil.findColor(getDiagram(), as.getForeground().getRed(),
			    as.getForeground().getGreen(), as.getForeground().getBlue());
			if (c == null) {
				c = EcoreUtil.copy(as.getForeground());
				getDiagram().getColors().add(c);
			}
			as.setForeground(c);
		}
		if (as.getBackground() != null) {
			Color c = ColorUtil.findColor(getDiagram(), as.getBackground().getRed(),
			    as.getBackground().getGreen(), as.getBackground().getBlue());
			if (c == null) {
				c = EcoreUtil.copy(as.getBackground());
				getDiagram().getColors().add(c);
			}
			as.setBackground(c);
		}
	}

	private Style cloneStylesAndFonts(Style s) {
		Style sTmp = EcoreUtil.copy(s);
		getDiagram().getStyles().add(sTmp);
		if (sTmp.getFont() != null) {
			Font f = FontUtil.findFont(getDiagram(), sTmp.getFont().getName(),
			    sTmp.getFont().getSize());
			if (f == null) {
				f = EcoreUtil.copy(sTmp.getFont());
				getDiagram().getFonts().add(f);
			}
			sTmp.setFont(f);
		}
		cloneStylesAndFonts((AbstractStyle) sTmp);

		return sTmp;
	}

	private void pasteSimpleConnections(List<RuminaqPasteFeature> pfs,
	    IFeatureProvider fp) {
		Map<Anchor, Anchor> anchors = new HashMap<>();
		Map<FlowSource, Anchor> flowSources = new HashMap<>();
		Map<FlowTarget, Anchor> flowTargets = new HashMap<>();

		for (RuminaqPasteFeature pf : pfs)
			if (pf instanceof PasteAnchorTracker)
				anchors.putAll(((PasteAnchorTracker) pf).getAnchors());
		for (Anchor a : anchors.keySet()) {
			for (Object o : getAllBusinessObjectsForPictogramElement(a.getParent())) {
				if (o instanceof FlowSource)
					flowSources.put((FlowSource) o, a);
				if (o instanceof FlowTarget)
					flowTargets.put((FlowTarget) o, a);
			}
		}
		if (anchors.size() == 0)
			return;
		Diagram oldDiagram = getDiagram(
		    ((Shape) anchors.entrySet().iterator().next().getKey().getParent()));
		Map<Connection, List<SimpleConnection>> peBos = new HashMap<>();
		Map<Connection, AnchorContainer> simpleConnectionPointAtTheEnd = new HashMap<>();
		Map<Connection, AnchorContainer> simpleConnectionPointAtTheStart = new HashMap<>();
		for (Connection c : oldDiagram.getConnections()) {
			List<SimpleConnection> scsToCopy = new LinkedList<>();
			for (Object cn : getAllBusinessObjectsForPictogramElement(c)) {
				if (cn instanceof SimpleConnection) {
					SimpleConnection sc = (SimpleConnection) cn;
					if (flowSources.containsKey(sc.getSourceRef())
					    && flowTargets.containsKey(sc.getTargetRef()))
						scsToCopy.add(sc);
				}
			}
			if (scsToCopy.size() > 0)
				peBos.put(c, scsToCopy);
			Anchor sa = c.getStart();
			Anchor ea = c.getEnd();
			if (Graphiti.getPeService().getPropertyValue(ea.getParent(),
			    Constants.SIMPLE_CONNECTION_POINT) != null)
				simpleConnectionPointAtTheEnd.put(c, ea.getParent());
			if (Graphiti.getPeService().getPropertyValue(sa.getParent(),
			    Constants.SIMPLE_CONNECTION_POINT) != null)
				simpleConnectionPointAtTheStart.put(c, sa.getParent());
		}
		PasteSimpleConnections psc = new PasteSimpleConnections(flowSources,
		    flowTargets, peBos, anchors, fp);
		psc.paste(null);
		cloneStylesAndFonts(psc.getNewPictogramElements());
	}

	private Diagram getDiagram(Shape shape) {
		if (shape instanceof Diagram)
			return (Diagram) shape;
		return getDiagram(shape.getContainer());
	}
}
