/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.validation;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
//import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.emf.validation.model.EvaluationMode;
import org.eclipse.emf.validation.model.IConstraintStatus;
import org.eclipse.emf.validation.service.IValidationListener;
import org.eclipse.emf.validation.service.ValidationEvent;
import org.ruminaq.util.ErrorUtils;

public class ProblemsReporter implements IValidationListener {
  @Override
  public void validationOccurred(ValidationEvent event) {
    // Batch validation is done by the WST project validator during building.
    if (event.getEvaluationMode() == EvaluationMode.LIVE) {
      if (event.matches(IStatus.WARNING | IStatus.ERROR | IStatus.CANCEL)) {
        List<IConstraintStatus> results = event.getValidationResults();
//                new MultiStatus(SicPlugin.ECLIPSE_ID.s(), 1,
//                        (IStatus[]) results.toArray(new IStatus[results.size()]),
//                        "Validation errors found", null);
        for (IStatus s : results)
          ErrorUtils.showErrorMessage(s.getMessage());
      }
    }
  }
}
