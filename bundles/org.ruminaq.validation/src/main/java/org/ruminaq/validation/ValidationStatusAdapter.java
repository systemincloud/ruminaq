/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.validation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.impl.AdapterImpl;

public class ValidationStatusAdapter extends AdapterImpl {

  public ValidationStatusAdapter() {
    super();
  }

  private List<IStatus> validationStatus = new ArrayList<>();

  @Override
  public boolean isAdapterForType(Object type) {
    return type instanceof Class
        && getClass().isAssignableFrom((Class<?>) type);
  }

  public IStatus getValidationStatus() {
    switch (validationStatus.size()) {
      case 0:
        return Status.OK_STATUS;
      case 1:
        return validationStatus.get(0);
    }
    return new MultiStatusWithMessage(
        validationStatus.toArray(new IStatus[validationStatus.size()]));
  }

  public void clearValidationStatus() {
    validationStatus.clear();
  }

  public void addValidationStatus(IStatus status) {
    validationStatus.add(status);
  }

  private static class MultiStatusWithMessage extends MultiStatus {

    private String message;

    public MultiStatusWithMessage(IStatus[] newChildren) {
      super("org.ruminaq.eclipse", 0, newChildren, "", null);
    }

    @Override
    public String getMessage() {
      if (message != null)
        return message;
      if (getChildren().length == 0)
        return super.getMessage();

      StringBuffer sb = new StringBuffer();
      for (IStatus status : getChildren()) {
        if (status.isOK())
          continue;
        sb.append(" - ").append(status.getMessage()).append('\n');
      }
      if (sb.length() > 0)
        sb.deleteCharAt(sb.length() - 1);
      message = sb.toString();
      return message;
    }

  }
}
