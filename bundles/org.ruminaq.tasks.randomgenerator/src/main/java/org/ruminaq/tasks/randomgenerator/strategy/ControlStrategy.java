package org.ruminaq.tasks.randomgenerator.strategy;

import java.util.List;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;
import org.ruminaq.tasks.randomgenerator.Port;
import org.ruminaq.tasks.randomgenerator.PropertySpecificComposite;
import org.ruminaq.tasks.randomgenerator.ValueSaveListener;
import org.ruminaq.tasks.randomgenerator.impl.RandomGeneratorI;
import org.ruminaq.tasks.randomgenerator.impl.RandomGeneratorStrategy;
import org.slf4j.Logger;

public class ControlStrategy extends RandomGeneratorStrategy {

	private final Logger logger = RunnerLoggerFactory.getLogger(ControlStrategy.class);

	public ControlStrategy(RandomGeneratorI task, EMap<String, String> eMap) {
		super(task);
	}

	@Override public void generate(List<Integer> dims) {
		logger.trace("generating Control");
		task.putData(Port.OUT, new ControlI());
	}

	public static PropertySpecificComposite createSpecificComposite(ValueSaveListener listener, Composite specificRoot, PictogramElement pe, TransactionalEditingDomain ed) {
		return new PropertySpecificComposite(listener, specificRoot, pe, ed) {
			{
				composite = new Composite(this.specificRoot, SWT.NONE);
				composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			}
			@Override public void    initValues(EMap<String, String> eMap) { }
			@Override public void    refresh(EMap<String, String> eMap)    { }
			@Override public boolean hasDimensions()                       { return false;}
		};
	}
}
