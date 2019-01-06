package org.ruminaq.tasks.randomgenerator.distributions;

import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ruminaq.util.RandomUtil;

public class NormalDistribution implements Distributon {

	float mean;
	float stdDev;
	
	public NormalDistribution(String textDistribution) {
        Matcher matcher = Pattern.compile(RandomUtil.NORMAL).matcher(textDistribution);
        if(matcher.find()) {
        	String expr = matcher.group();
        	int coma    = expr.indexOf(",");
        	mean        = Float.parseFloat(expr.substring(3, coma));
        	stdDev      = Float.parseFloat(expr.substring(coma + 1, expr.length() - 1));
        }
	}

	@Override public double getNext() {
		double ret = ThreadLocalRandom.current().nextGaussian()*stdDev + mean;
		return ret;
	}
}
