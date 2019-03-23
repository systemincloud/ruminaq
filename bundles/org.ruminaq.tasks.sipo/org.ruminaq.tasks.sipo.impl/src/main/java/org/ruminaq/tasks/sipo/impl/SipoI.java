package org.ruminaq.tasks.sipo.impl;

import java.util.LinkedList;

import org.ruminaq.model.desc.PortsDescrUtil;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.BasicTaskI;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.runner.impl.PortMap;
import org.ruminaq.runner.impl.data.DataI;
import org.ruminaq.runner.impl.data.Int32I;
import org.ruminaq.tasks.sipo.model.sipo.Sipo;
import org.ruminaq.util.GroovyExpressionUtil;
import org.slf4j.Logger;

public class SipoI extends BasicTaskI {

	private final Logger logger = RunnerLoggerFactory.getLogger(SipoI.class);

	private boolean trg;
	private boolean idx;
	private boolean nbE;
	private int     size;

	private LinkedList<DataI> queue = new LinkedList<>();

	public SipoI(EmbeddedTaskI parent, Task task) {
		super(parent, task);
		Sipo sipo = (Sipo) task;
		this.trg  = sipo.isTrigger();
		this.idx  = sipo.isIndex();
		this.nbE  = sipo.isSizeOut();

		this.size = (Integer) GroovyExpressionUtil.evaluate(parent.replaceVariables(sipo.getSize()));

		logger.trace("trigger {}",        trg);
		logger.trace("index {}",          idx);
		logger.trace("nb of elem out {}", nbE);
		logger.trace("size {}",           size);
	}

	@Override
	protected void execute(PortMap portIdData, int grp) {
		if(grp == PortsDescrUtil.getGroup(Port.IN)) {
			DataI dataI = portIdData.get(Port.IN);

			if(queue.size() == size) queue.removeLast();

		    queue.addFirst(dataI);

		    if(!trg && !idx)
		    	for(int i = 0; i < queue.size(); i++)
		    		putData(Port.OUT, i, queue.get(i));

		    if(nbE) putData(Port.SIZE, new Int32I(queue.size()));
		} else if(grp == PortsDescrUtil.getGroup(Port.IDX)) {
			DataI data = portIdData.get(Port.IDX);
			int i = data.get(Int32I.class).getValues()[0];
			if(queue.size() > i) putData(Port.LOUT, queue.get(i));
		} else if(grp == PortsDescrUtil.getGroup(Port.TRIGGER)) {
	    	for(int i = 0; i < queue.size(); i++)
	    		putData(Port.OUT, i, queue.get(i));
		}
	}
}
