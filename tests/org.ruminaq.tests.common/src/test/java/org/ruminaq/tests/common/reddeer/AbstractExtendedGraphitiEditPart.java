/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tests.common.reddeer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.gef.EditPart;
import org.eclipse.graphiti.features.context.ISingleClickContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.internal.features.context.impl.base.SingleClickContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.reddeer.common.util.Display;
import org.eclipse.reddeer.common.util.ResultRunnable;
import org.eclipse.reddeer.graphiti.GraphitiLayerException;
import org.eclipse.reddeer.graphiti.impl.graphitieditpart.AbstractGraphitiEditPart;
import org.eclipse.reddeer.graphiti.lookup.DiagramEditorLookup;
import org.hamcrest.Matcher;

public abstract class AbstractExtendedGraphitiEditPart
    extends AbstractGraphitiEditPart {

  public AbstractExtendedGraphitiEditPart(EditPart editPart) {
    super(editPart);
  }

  public AbstractExtendedGraphitiEditPart(Matcher<EditPart> matcher,
      int index) {
    super(matcher, index);
  }

  public void singleClick() {
    DiagramEditor diagramEditor = DiagramEditorLookup.getInstance()
        .findDiagramEditor();

    PictogramElement pe = null;
    try {
      Method method = editPart.getClass().getMethod("getPictogramElement");
      Object obj = method.invoke(editPart);
      pe = (PictogramElement) obj;
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException
        | IllegalArgumentException | InvocationTargetException e) {
      e.printStackTrace();
    }

    ISingleClickContext ctx = new SingleClickContext(pe, null, null);

    List<ICustomFeature> features = Display
        .syncExec((ResultRunnable<List<ICustomFeature>>) () -> Stream
            .of(diagramEditor.getDiagramTypeProvider()
                .getAvailableToolBehaviorProviders())
            .map(t -> new SimpleEntry<>(t, ctx))
            .map(se -> se.getKey().getSingleClickFeature(se.getValue()))
            .filter(Objects::nonNull).collect(Collectors.toList()));

    features.stream().forEach(feature -> Display.getDisplay().asyncExec(() -> {
      feature.execute(ctx);
    }));
    Display.getDisplay().syncExec(() -> {
    });
  }

}
