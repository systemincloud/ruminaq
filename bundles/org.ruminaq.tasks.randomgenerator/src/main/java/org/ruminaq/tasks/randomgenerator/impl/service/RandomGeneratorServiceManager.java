package org.ruminaq.tasks.randomgenerator.impl.service;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.eclipse.emf.common.util.EMap;
import org.ruminaq.model.sic.DataType;
import org.ruminaq.tasks.randomgenerator.impl.RandomGeneratorStrategy;

public enum RandomGeneratorServiceManager {

	INSTANCE;
	
	private List<RandomGeneratorService> services = new ArrayList<>();
	
	private RandomGeneratorServiceManager() {
		ServiceLoader<RandomGeneratorService> sl = ServiceLoader.load(RandomGeneratorService.class);
        for (RandomGeneratorService srv : sl) services.add(srv);
	}

	public RandomGeneratorStrategy getStrategy(DataType dt, EMap<String, String> eMap) {
		for(RandomGeneratorService srv : services) {
			RandomGeneratorStrategy strategy = srv.getStrategy(dt, eMap);
			if(strategy != null) return strategy;
		}
		return null;
	}
}
