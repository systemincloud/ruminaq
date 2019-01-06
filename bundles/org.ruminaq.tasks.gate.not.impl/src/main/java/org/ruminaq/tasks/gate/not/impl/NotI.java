package org.ruminaq.tasks.gate.not.impl;

import org.ruminaq.model.model.ruminaq.Task;
import org.ruminaq.runner.impl.BasicTaskI;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.runner.impl.PortMap;
import org.ruminaq.runner.impl.data.BoolI;

public class NotI extends BasicTaskI {

	public NotI(EmbeddedTaskI parent, Task task) {
		super(parent, task);
	}

	@Override
	protected void execute(PortMap portIdData, int grp) {
		BoolI data = portIdData.get(Port.IN).get(BoolI.class);
		int n = data.getNumberOfElements();
		if(n == 1) putData(Port.OUT, new BoolI(!data.getValues()[0]));
		else {
			boolean[] oldValues = data.getValues();
			boolean[] newValues = new boolean[n];
			for(int i = 0; i < n; i++) newValues[i] = !oldValues[i];
			putData(Port.OUT, new BoolI(newValues, data.getDimensions()));
		}
	}
}
