/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.randomgenerator.impl.strategy;

import java.util.List;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.data.ControlI;
import org.ruminaq.tasks.randomgenerator.impl.Port;
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
}
