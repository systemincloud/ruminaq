/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.validation;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.model.ConstraintStatus;

public class StatusLocationDecorator extends ConstraintStatus {

  private IStatus status;
  private String location;

  public StatusLocationDecorator(ConstraintStatus status, String location) {
    super(status.getConstraint(), status.getTarget(), status.getMessage(),
        status.getResultLocus());
    this.status = status;
    this.location = location;
  }

  public String getLocation() {
    return location;
  }

  @Override
  public IStatus[] getChildren() {
    return status.getChildren();
  }

  @Override
  public int getCode() {
    return status.getCode();
  }

  @Override
  public Throwable getException() {
    return status.getException();
  }

  @Override
  public String getMessage() {
    return status.getMessage();
  }

  @Override
  public String getPlugin() {
    return status.getPlugin();
  }

  @Override
  public int getSeverity() {
    return status.getSeverity();
  }

  @Override
  public boolean isMultiStatus() {
    return status.isMultiStatus();
  }

  @Override
  public boolean isOK() {
    return status.isOK();
  }

  @Override
  public boolean matches(int arg) {
    return status.matches(arg);
  }
}
