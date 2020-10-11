/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.validation.constraints;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.EMFEventType;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.ruminaq.model.ruminaq.Connection;
import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.model.ruminaq.DataTypeManager;
import org.ruminaq.model.ruminaq.InternalInputPort;
import org.ruminaq.model.ruminaq.InternalOutputPort;
import org.ruminaq.validation.StatusLocationDecorator;

public class DataCastImpossible extends AbstractModelConstraint {

  @Override
  public IStatus validate(IValidationContext ctx) {
    EObject eObj = ctx.getTarget();
    if (ctx.getEventType() == EMFEventType.NULL && eObj instanceof Connection)
      return validate(ctx, (Connection) eObj);
    return ctx.createSuccessStatus();
  }

  private IStatus validate(IValidationContext ctx, Connection connection) {
    if (!(connection.getTargetRef() instanceof InternalInputPort))
      return ctx.createSuccessStatus();

    InternalInputPort dst = (InternalInputPort) connection.getTargetRef();
    List<Class<? extends DataType>> shouldBe = new LinkedList<>();
    for (DataType dt : dst.getDataType())
      shouldBe.add(dt.getClass());

    if (shouldBe.size() == 0)
      return ctx.createSuccessStatus();

    if (!(connection.getSourceRef() instanceof InternalOutputPort))
      return ctx.createSuccessStatus();

    InternalOutputPort src = (InternalOutputPort) connection.getSourceRef();
    List<Class<? extends DataType>> came = new LinkedList<>();
    for (DataType dt : src.getDataType())
      came.add(dt.getClass());

    if (came.size() == 0)
      return ctx.createSuccessStatus();

    for (Class<? extends DataType> c : came)
      for (Class<? extends DataType> s : shouldBe)
        if (DataTypeManager.INSTANCE.canCastFromTo(c, s))
          return ctx.createSuccessStatus();

    return new StatusLocationDecorator(
        (ConstraintStatus) ctx.createFailureStatus(
            new Object[] { src.getTask().getId() + ":" + src.getId(),
                dst.getTask().getId() + ":" + dst.getId() }),
        dst.getTask().getId() + ":" + dst.getId());
  }
}
