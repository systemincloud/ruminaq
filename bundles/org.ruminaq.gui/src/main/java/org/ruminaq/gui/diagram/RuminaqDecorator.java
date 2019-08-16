/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.diagram;

import org.eclipse.draw2d.IFigure;
import org.eclipse.graphiti.tb.IDecorator;

public interface RuminaqDecorator extends IDecorator {
	IFigure decorateFigure(IFigure figure, IDecorator decorator);
}
