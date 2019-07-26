/*
 * (C) Copyright 2018 Marek Jagielski.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ruminaq.gui.features.paste;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IPasteFeature;
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
import org.ruminaq.gui.features.paste.PasteAnchorTracker;
import org.ruminaq.gui.features.paste.PasteDefaultElementFeature;
import org.ruminaq.gui.features.paste.PasteInputPortFeature;
import org.ruminaq.gui.features.paste.PasteOutputPortFeature;
import org.ruminaq.gui.features.paste.PasteSimpleConnections;
import org.ruminaq.gui.features.paste.SicPasteFeature;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.FlowSource;
import org.ruminaq.model.ruminaq.FlowTarget;
import org.ruminaq.model.ruminaq.InputPort;
import org.ruminaq.model.ruminaq.OutputPort;
import org.ruminaq.model.ruminaq.SimpleConnection;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.util.ColorUtil;
import org.ruminaq.util.FontUtil;
import org.ruminaq.util.StyleUtil;

public class PasteElementFeature extends AbstractPasteFeature {

	public PasteElementFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canPaste(IPasteContext context) {
        List<SicPasteFeature> pfs = getPasteFeatures(context);
		if(pfs.size() == 0) return false;

        boolean ret = true;
        for(IPasteFeature pf : pfs)
        	if(!pf.canPaste(context)) ret = false;
        return ret;
	}

	private List<SicPasteFeature> getPasteFeatures(IPasteContext context) {
		List<SicPasteFeature> pfs = new LinkedList<>();

		Object[] fromClipboard = getFromClipboard();
        if(fromClipboard == null || fromClipboard.length == 0) return pfs;

        Object[] objects = getFromClipboard();

		int xMin = Integer.MAX_VALUE;
		int yMin = Integer.MAX_VALUE;

		for(Object o : objects) {
			PictogramElement pe = (PictogramElement) o;
			GraphicsAlgorithm ga = pe.getGraphicsAlgorithm();
			if(ga == null) continue;
			int xTmp = pe.getGraphicsAlgorithm().getX();
			int yTmp = pe.getGraphicsAlgorithm().getY();
			if(xTmp < xMin) { xMin = xTmp; yMin = yTmp; }
		}

        for(Object o1 : objects) {
        	PictogramElement oldPe = (PictogramElement) o1;
        	BaseElement      oldBo = null;

        	for(Object o2 : getAllBusinessObjectsForPictogramElement(oldPe))
        		if(o2 instanceof BaseElement) oldBo = (BaseElement) o2;

   	     	     if(oldBo instanceof InputPort)  pfs.add(new PasteInputPortFeature     (getFeatureProvider(), oldPe, xMin, yMin));
   	     	else if(oldBo instanceof OutputPort) pfs.add(new PasteOutputPortFeature    (getFeatureProvider(), oldPe, xMin, yMin));
   	     	else if(oldBo instanceof Task)       pfs.add(new PasteTaskFeature          (getFeatureProvider(), oldPe, xMin, yMin));
   	     	else                                 pfs.add(new PasteDefaultElementFeature(getFeatureProvider(), oldPe, xMin, yMin));
        }
		return pfs;
	}

	@Override
	public void paste(IPasteContext context) {
		List<SicPasteFeature> pfs = getPasteFeatures(context);

		for(SicPasteFeature pf : pfs) {
			pf.paste(context);
			cloneStylesAndFonts(pf.getNewPictogramElements());
		}
		pasteSimpleConnections(pfs, getFeatureProvider());
	}

	private void cloneStylesAndFonts(List<PictogramElement> pes) {
		for(PictogramElement p : pes) cloneStylesAndFonts(p);
	}

	private void cloneStylesAndFonts(PictogramElement p) {
		cloneStylesAndFonts(p.getGraphicsAlgorithm());

		if(p instanceof Connection) {
			Connection con = (Connection) p;
			for(ConnectionDecorator cd : con.getConnectionDecorators()) cloneStylesAndFonts(cd.getGraphicsAlgorithm());
		}

		if(p instanceof ContainerShape) {
			ContainerShape cs = (ContainerShape) p;
			for(Shape ch : cs.getChildren()) cloneStylesAndFonts(ch);
		}
	}

	private void cloneStylesAndFonts(GraphicsAlgorithm ga) {
		Style s = ga.getStyle();
		Style sTmp;
		if(s != null) {
			sTmp = StyleUtil.findStyle(getDiagram(), s.getId());
			if(sTmp != null) ga.setStyle(sTmp);
			else             ga.setStyle(cloneStylesAndFonts(s));
		}
		cloneStylesAndFonts((AbstractStyle) ga);
		if(ga instanceof AbstractText) {
			AbstractText txt = (AbstractText) ga;
			Font f = FontUtil.findFont(getDiagram(), txt.getFont().getName(), txt.getFont().getSize());
			if(f != null) txt.setFont(f);
			else {
				f = EcoreUtil.copy(txt.getFont());
				getDiagram().getFonts().add(f);
				txt.setFont(f);
			}
		}
		for(GraphicsAlgorithm gac : ga.getGraphicsAlgorithmChildren()) cloneStylesAndFonts(gac);
	}

	private void cloneStylesAndFonts(AbstractStyle as) {
		if(as.getForeground() != null) {
			Color c = ColorUtil.findColor(getDiagram(), as.getForeground().getRed(),
					                                    as.getForeground().getGreen(),
					                                    as.getForeground().getBlue());
			if(c == null) {
				c = EcoreUtil.copy(as.getForeground());
				getDiagram().getColors().add(c);
			}
			as.setForeground(c);
		}
		if(as.getBackground() != null) {
			Color c = ColorUtil.findColor(getDiagram(), as.getBackground().getRed(),
			                                            as.getBackground().getGreen(),
			                                            as.getBackground().getBlue());
			if(c == null) {
				c = EcoreUtil.copy(as.getBackground());
				getDiagram().getColors().add(c);
			}
			as.setBackground(c);
		}
	}

	private Style cloneStylesAndFonts(Style s) {
		Style sTmp = EcoreUtil.copy(s);
		getDiagram().getStyles().add(sTmp);
		if(sTmp.getFont() != null) {
			Font f = FontUtil.findFont(getDiagram(), sTmp.getFont().getName(), sTmp.getFont().getSize());
			if(f == null)  {
				f = EcoreUtil.copy(sTmp.getFont());
				getDiagram().getFonts().add(f);
			}
			sTmp.setFont(f);
		}
		cloneStylesAndFonts((AbstractStyle) sTmp);

		return sTmp;
	}

	private void pasteSimpleConnections(List<SicPasteFeature> pfs, IFeatureProvider fp) {
		Map<Anchor, Anchor>     anchors     = new HashMap<>();
		Map<FlowSource, Anchor> flowSources = new HashMap<>();
		Map<FlowTarget, Anchor> flowTargets = new HashMap<>();

		for(SicPasteFeature pf : pfs)
			if(pf instanceof PasteAnchorTracker) anchors.putAll(((PasteAnchorTracker) pf).getAnchors());
		for(Anchor a : anchors.keySet()) {
			for(Object o : getAllBusinessObjectsForPictogramElement(a.getParent())) {
				if(o instanceof FlowSource) flowSources.put((FlowSource) o, a);
				if(o instanceof FlowTarget) flowTargets.put((FlowTarget) o, a);
			}
		}
		if(anchors.size() == 0) return;
		Diagram oldDiagram = getDiagram(((Shape) anchors.entrySet().iterator().next().getKey().getParent()));
		Map<Connection, List<SimpleConnection>> peBos = new HashMap<>();
		Map<Connection, AnchorContainer> simpleConnectionPointAtTheEnd   = new HashMap<>();
		Map<Connection, AnchorContainer> simpleConnectionPointAtTheStart = new HashMap<>();
		for(Connection c : oldDiagram.getConnections()) {
			List<SimpleConnection> scsToCopy = new LinkedList<>();
			for(Object cn : getAllBusinessObjectsForPictogramElement(c)) {
				if(cn instanceof SimpleConnection) {
					SimpleConnection sc = (SimpleConnection) cn;
					if(flowSources.containsKey(sc.getSourceRef()) && flowTargets.containsKey(sc.getTargetRef())) scsToCopy.add(sc);
				}
			}
			if(scsToCopy.size() > 0) peBos.put(c, scsToCopy);
			Anchor sa = c.getStart();
			Anchor ea = c.getEnd();
			if(Graphiti.getPeService().getPropertyValue(ea.getParent(), Constants.SIMPLE_CONNECTION_POINT) != null)
				simpleConnectionPointAtTheEnd.put(c, ea.getParent());
			if(Graphiti.getPeService().getPropertyValue(sa.getParent(), Constants.SIMPLE_CONNECTION_POINT) != null)
				simpleConnectionPointAtTheStart.put(c, sa.getParent());
		}
		PasteSimpleConnections psc = new PasteSimpleConnections(flowSources,
				                                                flowTargets,
				                                                peBos,
				                                                anchors,
				                                                fp);
		psc.paste(null);
		cloneStylesAndFonts(psc.getNewPictogramElements());
	}

	private Diagram getDiagram(Shape shape) {
		if(shape instanceof Diagram) return (Diagram) shape;
		return getDiagram(shape.getContainer());
	}
}
