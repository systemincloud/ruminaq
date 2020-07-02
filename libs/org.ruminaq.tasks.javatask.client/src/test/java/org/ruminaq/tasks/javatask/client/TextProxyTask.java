/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.javatask.client;

import org.ruminaq.tasks.javatask.client.annotations.InputPortInfo;
import org.ruminaq.tasks.javatask.client.annotations.JavaTaskInfo;
import org.ruminaq.tasks.javatask.client.annotations.OutputPortInfo;
import org.ruminaq.tasks.javatask.client.data.Text;

/**
 *
 * @author Marek Jagielski
 */
@JavaTaskInfo
public class TextProxyTask extends JavaTask {

	@InputPortInfo(name = "In", dataType = Text.class)
	public InputPort in;

	@OutputPortInfo(name = "Out", dataType = Text.class)
	public OutputPort out;

	@Override
	public void execute(int grp) {
		String value = in.getData(Text.class).getValues().get(0);
		out.putData(new Text(value));
	}

	@Override
	public void executeAsync(InputPort asynchIn) {
	}

}
