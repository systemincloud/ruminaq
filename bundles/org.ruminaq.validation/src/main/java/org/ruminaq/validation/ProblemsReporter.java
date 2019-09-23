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