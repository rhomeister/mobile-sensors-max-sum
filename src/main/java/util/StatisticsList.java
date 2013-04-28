package util;

import java.util.ArrayList;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math.stat.StatUtils;

public class StatisticsList extends ArrayList<Double> {

	public double getMean() {
		return StatUtils.mean(toPrimitiveArray());
	}

	public double getVariance() {
		return StatUtils.variance(toPrimitiveArray());
	}

	public double getStandardErrorOfMean() {
		return Math.sqrt(StatUtils.variance(toPrimitiveArray()) / size());
	}

	public double[] toPrimitiveArray() {
		Double[] array = toArray(new Double[size()]);
		return ArrayUtils.toPrimitive(array);
	}

}
