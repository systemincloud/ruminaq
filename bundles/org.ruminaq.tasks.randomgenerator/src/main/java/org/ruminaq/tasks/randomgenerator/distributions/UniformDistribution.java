package org.ruminaq.tasks.randomgenerator.distributions;

import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ruminaq.util.RandomUtil;

public class UniformDistribution implements Distributon {

	float low;
	float heigh;
	
	public UniformDistribution(String textDistribution) {
		Matcher matcher = Pattern.compile(RandomUtil.UNIFORM).matcher(textDistribution);
        if(matcher.find()) {
        	String expr = matcher.group();
        	int coma    = expr.indexOf(",");
        	low         = Float.parseFloat(expr.substring(3, coma));
        	heigh       = Float.parseFloat(expr.substring(coma + 1, expr.length() - 1));
        	if(low > heigh) throw new RuntimeException("Not valid");
        }
	}

	@Override public double getNext() {
		double ret = ThreadLocalRandom.current().nextDouble()*(heigh - low) + low;
		return ret; 
	}
}
