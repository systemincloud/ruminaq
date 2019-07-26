package org.ruminaq.gui.features.paste;

import java.util.Map;

import org.eclipse.graphiti.mm.pictograms.Anchor;

public interface PasteAnchorTracker {
	Map<Anchor, Anchor> getAnchors();
}
