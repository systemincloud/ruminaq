/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.console.impl;

import org.ruminaq.model.desc.IN;
import org.ruminaq.model.desc.OUT;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.desc.Position;
import org.ruminaq.model.model.dt.Text;

public enum Port implements PortsDescr {
	@IN (name="In",  type=Text.class, opt=true, label=false, pos=Position.RIGHT) IN,
	@OUT(name="Out", type=Text.class, opt=true, label=false, pos=Position.LEFT) OUT;
}
