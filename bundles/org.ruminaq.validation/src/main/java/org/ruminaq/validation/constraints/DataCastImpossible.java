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
package org.ruminaq.validation.constraints;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.EMFEventType;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.ruminaq.model.DataTypeManager;
import org.ruminaq.model.ruminaq.Connection;
import org.ruminaq.model.ruminaq.DataType;
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
            new Object[] { src.getParent().getId() + ":" + src.getId(),
                dst.getParent().getId() + ":" + dst.getId() }),
        dst.getParent().getId() + ":" + dst.getId());
  }
}
