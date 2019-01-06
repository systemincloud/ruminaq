package org.ruminaq.tasks.randomgenerator.impl.service;

import org.eclipse.emf.common.util.EMap;
import org.ruminaq.model.sic.DataType;
import org.ruminaq.tasks.randomgenerator.impl.RandomGeneratorStrategy;

public interface RandomGeneratorService {
	RandomGeneratorStrategy getStrategy(DataType dt, EMap<String, String> eMap);
}
