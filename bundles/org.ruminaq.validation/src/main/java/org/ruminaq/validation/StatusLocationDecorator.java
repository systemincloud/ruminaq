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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.model.ConstraintStatus;

public class StatusLocationDecorator extends ConstraintStatus {

	private IStatus status;
	private String  location;

	public StatusLocationDecorator(ConstraintStatus status, String location) {
		super(status.getConstraint(),
			  status.getTarget(),
			  status.getMessage(),
			  status.getResultLocus());
		this.status = status;
		this.location = location;
	}

	public String getLocation() { return location; }

	@Override public IStatus[] getChildren()    { return status.getChildren(); }
	@Override public int       getCode()        { return status.getCode(); }
	@Override public Throwable getException()   { return status.getException(); }
	@Override public String    getMessage()     { return status.getMessage(); }
	@Override public String    getPlugin()      { return status.getPlugin(); }
	@Override public int       getSeverity()    { return status.getSeverity(); }
	@Override public boolean   isMultiStatus()  { return status.isMultiStatus(); }
	@Override public boolean   isOK()           { return status.isOK(); }
	@Override public boolean   matches(int arg) { return status.matches(arg); }
}
