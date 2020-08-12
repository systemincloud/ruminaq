/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tests.common.reddeer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.reddeer.gef.editor.GEFEditor;
import org.eclipse.reddeer.graphiti.api.GraphitiEditPart;
import org.ruminaq.gui.features.move.MoveInternalPortFeature;
import org.ruminaq.model.ruminaq.ModelUtil;

public class MoveInternalPortShape extends MoveShape {

  public MoveInternalPortShape(GEFEditor gefEditor, GraphitiEditPart ep,
      int deltaX, int deltaY) {
    super(gefEditor, ep, deltaX, deltaY);
  }

  @Override
  public void execute() {
    MoveInternalPortFeature f = new MoveInternalPortFeature(featureProvider);
    f.canMoveShape(context);
    ModelUtil.runModelChange(() -> {
      f.moveShape(context);
      try {
        Method postMoveShapeMethod = MoveInternalPortFeature.class
            .getDeclaredMethod("postMoveShape", IMoveShapeContext.class);
        postMoveShapeMethod.canAccess(f);
        postMoveShapeMethod.invoke(f, context);
      } catch (NoSuchMethodException | SecurityException
          | IllegalAccessException | IllegalArgumentException
          | InvocationTargetException e) {
        e.printStackTrace();
      }
    }, editDomain, "Move shape");
  }
}
