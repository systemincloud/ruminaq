package org.ruminaq.tasks.gate.xor.impl;

import java.util.List;

import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.runner.impl.BasicTaskI;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.runner.impl.PortMap;
import org.ruminaq.runner.impl.data.BoolI;
import org.ruminaq.runner.impl.data.DataI;
import org.ruminaq.tasks.gate.Port;

public class XorI extends BasicTaskI {

	public XorI(EmbeddedTaskI parent, Task task) {
		super(parent, task);
	}

	@Override
	protected void execute(PortMap portIdData, int grp) {
		List<BoolI> datas = DataI.get(portIdData.getAll(Port.IN), BoolI.class);
		int n = datas.get(0).getNumberOfElements();
		if(n == 1) {
			int k = 0;
			for(BoolI d : datas)
				if(d.getValues()[0]) { k++; }
			if(k%2 == 1) putData(Port.OUT, new BoolI(true));
			else         putData(Port.OUT, new BoolI(false));
			return;
		} else {
			int[] k = new int[n];
			for(BoolI d : datas) {
				boolean[] bs = d.getValues();
				for(int i = 0; i < n; i++)
					if(bs[i]) k[i]++;
			}
			boolean[] ret = new boolean[n];
			for(int i = 0; i < n; i++) ret[i] = k[i]%2 == 1;
			putData(Port.OUT, new BoolI(ret, datas.get(0).getDimensions()));
			return;
		}
	}
}
