package senseSleep;

import java.util.List;

import maxSumController.continuous.ContinuousVariable;
import maxSumController.continuous.linear.MultiVariatePieceWiseLinearFunction;
import maxSumController.continuous.linear.MultiVariatePieceWiseLinearFunctionFactory;
import maxSumController.continuous.linear.MultiVariatePieceWiseLinearFunctionUtilities;
import maxSumController.continuous.linear.NCube;
import maxSumController.continuous.linear.NDimensionalPoint;
import maxSumController.continuous.linear.NSimplex;

import org.apache.commons.lang.Validate;

public class SenseSleepUtilityFunctionWrapAroundFactory implements
		MultiVariatePieceWiseLinearFunctionFactory {

	private double overlapSize;

	private double scheduleIntervalLength;

	public SenseSleepUtilityFunctionWrapAroundFactory(double overlapSize,
			double scheduleIntervalLength) {
		this.overlapSize = overlapSize;
		this.scheduleIntervalLength = scheduleIntervalLength;
	}

	public static MultiVariatePieceWiseLinearFunction createFunction(
			double[] dutyCycles, double schedulingInterval,
			double overlappingArea) {
		Validate.isTrue(dutyCycles.length <= 2);

		// System.out.println("Creating continuous sense sleep function with "
		// + dutyCycles.length + " dimensions");

		NCube domain = createDomain(schedulingInterval, dutyCycles.length);

		if (dutyCycles.length == 1) {
			return MultiVariatePieceWiseLinearFunctionUtilities
					.createConstantFunction(domain, overlappingArea
							* dutyCycles[0]);
		}

		double l1 = dutyCycles[0];
		double l2 = dutyCycles[1];
		double l = schedulingInterval;

		Validate.isTrue(l1 <= schedulingInterval);
		Validate.isTrue(l2 <= schedulingInterval);

		MultiVariatePieceWiseLinearFunction result = createManualFunction(
				dutyCycles[0], dutyCycles[1], l, domain);

		// MultiVariatePieceWiseLinearFunction result = createAutoFunction(
		// dutyCycles[0], dutyCycles[1], l, domain);

		result = result.multiply(overlappingArea);

		validateFunction(result, schedulingInterval, overlappingArea, l1, l2);

		return result;
	}

	private static MultiVariatePieceWiseLinearFunction createAutoFunction(
			double l1, double l2, double l, NCube domain) {
		System.out
				.println("Creating auto continuous sense sleep function with "
						+ 2 + " dimensions");

		MultiVariatePieceWiseLinearFunction x1 = MultiVariatePieceWiseLinearFunctionUtilities
				.createUnivariateFunction(domain, 0);

		MultiVariatePieceWiseLinearFunction x2 = MultiVariatePieceWiseLinearFunctionUtilities
				.createUnivariateFunction(domain, 1);

		// split the sensing intervals into two: one for the interval up
		// until
		// the end of the scheduling interval, and one for the wraparound,
		// which
		// is potentially of zero length

		MultiVariatePieceWiseLinearFunction intervalLength1 = x1.subtract(l)
				.multiply(-1).min(l1);

		MultiVariatePieceWiseLinearFunction intervalLength2 = x2.subtract(l)
				.multiply(-1).min(l2);

		MultiVariatePieceWiseLinearFunction intervalLength1Prime = x1.add(l1)
				.subtract(l).max(0.0);

		MultiVariatePieceWiseLinearFunction intervalLength2Prime = x2.add(l2)
				.subtract(l).max(0.0);

		double overlapTotal = l1 + l2;

		MultiVariatePieceWiseLinearFunction overlap12 = x1.add(intervalLength1)
				.min(x2.add(intervalLength2)).subtract(x1.max(x2)).max(0);

		MultiVariatePieceWiseLinearFunction overlap14 = x1.add(intervalLength1)
				.min(intervalLength2Prime).subtract(x1).max(0);

		MultiVariatePieceWiseLinearFunction overlap23 = x2.add(intervalLength2)
				.min(intervalLength1Prime).subtract(x2).max(0);

		MultiVariatePieceWiseLinearFunction overlap34 = intervalLength1Prime
				.min(intervalLength2Prime);

		MultiVariatePieceWiseLinearFunction result = overlap12.add(overlap14)
				.add(overlap23).add(overlap34).subtract(overlapTotal).multiply(
						-1);

		return result;
	}

	private static MultiVariatePieceWiseLinearFunction createManualFunction(
			double l1, double l2, double ls, NCube domain) {
		System.out
				.println("Creating manual continuous sense sleep function with "
						+ 2 + " dimensions");

		boolean swapCoordinates = l1 > l2;

		boolean exceedsLs = l1 + l2 >= ls;

		if (swapCoordinates) {
			double temp = l1;
			l1 = l2;
			l2 = temp;
		}

		MultiVariatePieceWiseLinearFunction result = new MultiVariatePieceWiseLinearFunction(
				domain);

		double m1 = Math.max(l1, l2);
		double m2 = Math.min(ls, l1 + l2);

		double q1 = Math.abs(l2 - l1);
		double q3 = ls - l1;
		double q6 = ls - l2;
		double q7 = ls + l1 - l2;

		NDimensionalPoint a = createPoint(0, 0, swapCoordinates);
		NDimensionalPoint b = createPoint(q1, 0, swapCoordinates);
		NDimensionalPoint c = createPoint(ls, 0, swapCoordinates);
		NDimensionalPoint d = createPoint(0, l1, swapCoordinates);
		NDimensionalPoint e = createPoint(l2, 0, swapCoordinates);
		NDimensionalPoint f = createPoint(q3, 0, swapCoordinates);
		NDimensionalPoint g = createPoint(ls, l1, swapCoordinates);
		NDimensionalPoint h = createPoint(ls, q6, swapCoordinates);
		NDimensionalPoint i = createPoint(ls, q7, swapCoordinates);
		NDimensionalPoint j = createPoint(ls, ls, swapCoordinates);
		NDimensionalPoint k = createPoint(q3, ls, swapCoordinates);
		NDimensionalPoint l = createPoint(l2, ls, swapCoordinates);
		NDimensionalPoint m = createPoint(q1, ls, swapCoordinates);
		NDimensionalPoint n = createPoint(0, ls, swapCoordinates);
		NDimensionalPoint o = createPoint(0, q7, swapCoordinates);
		NDimensionalPoint p = createPoint(0, q6, swapCoordinates);

		if (!exceedsLs) {
			addPartition(result, n, o, m);
			addPartition(result, o, m, p);
			addPartition(result, p, m, l);
			addPartition(result, l, k, d);
			addPartition(result, p, l, d);
			addPartition(result, k, d, a);
			addPartition(result, a, k, j);
			addPartition(result, a, j, b);
			addPartition(result, b, j, i);
			addPartition(result, b, i, e);
			addPartition(result, e, i, h);
			addPartition(result, e, h, f);
			addPartition(result, f, h, g);
			addPartition(result, f, g, c);
		} else {
			addPartition(result, n, o, m);
			addPartition(result, o, m, d);
			addPartition(result, d, m, k);
			addPartition(result, l, k, d);
			addPartition(result, p, l, d);
			addPartition(result, l, p, a);
			addPartition(result, a, l, j);
			addPartition(result, a, j, b);
			addPartition(result, b, j, i);
			addPartition(result, b, i, f);
			addPartition(result, f, i, g);
			addPartition(result, e, h, f);
			addPartition(result, f, h, g);
			addPartition(result, e, h, c);
		}

		result.setValue(a, m1);
		result.setValue(b, m1);
		result.setValue(c, m1);
		result.setValue(d, m2);
		result.setValue(e, m2);
		result.setValue(f, m2);
		result.setValue(g, m2);
		result.setValue(h, m2);
		result.setValue(i, m1);
		result.setValue(j, m1);
		result.setValue(k, m2);
		result.setValue(l, m2);
		result.setValue(m, m1);
		result.setValue(n, m1);
		result.setValue(o, m1);
		result.setValue(p, m2);

		return result;
	}

	private static NDimensionalPoint createPoint(double x1, double x2,
			boolean swapCoordinates) {
		if (!swapCoordinates)
			return new NDimensionalPoint(x1, x2);
		else
			return new NDimensionalPoint(x2, x1);
	}

	private static void addPartition(
			MultiVariatePieceWiseLinearFunction result, NDimensionalPoint n,
			NDimensionalPoint o, NDimensionalPoint m) {
		try {
			result.addPartition(new NSimplex(n, o, m));
		} catch (Exception e) {
		}
	}

	private static boolean validateFunction(
			MultiVariatePieceWiseLinearFunction result, double l,
			double overlapSize, double l1, double l2) {

		SenseSleepFunctionWrapAroundValidator validator = new SenseSleepFunctionWrapAroundValidator(
				"", overlapSize, l);

		for (double x1 = 0; x1 <= l; x1 += 0.5) {
			for (double x2 = 0; x2 <= l; x2 += 0.5) {
				double expected = validator.evaluate(new double[] { l1, l2 },
						new double[] { x1, x2 });

				double actual = result.evaluate(x1, x2);

				if (Math.abs(expected - actual) > 1e-7) {
					System.out.println(expected + " " + actual);

					// dumpToConsole(result);

					System.out.println("Function is not correct");
					return false;
				}
			}
		}

		// System.out.println("Function validated");
		return true;

	}

	private static void dumpToConsole(
			MultiVariatePieceWiseLinearFunction function) {
		// System.out.println(function.evaluate(new double[] { 1.0}));

		for (double x1 = 0; x1 <= function.getDomain().getDomainEnd(0); x1 += 0.5) {
			for (double x2 = 0; x2 <= function.getDomain().getDomainEnd(1); x2 += 0.5) {

				System.out.println(x1 + " " + x2 + " "
						+ function.evaluate(x1, x2));
			}
		}

	}

	private static NCube createDomain(double schedulingInterval,
			int dimensionality) {
		NCube domain = new NCube(dimensionality);

		for (int i = 0; i < dimensionality; i++) {
			domain.setBoundaries(i, 0, schedulingInterval);
		}

		return domain;
	}

	public static void main(String[] args) {
		double l1 = 5;
		double l2 = 7;
		double l = 10.0;

		for (int i = 0; i < 100; i++) {
			l1 = l * Math.random();
			l2 = l * Math.random();
			System.out.println(i);

			MultiVariatePieceWiseLinearFunction function = createFunction(
					new double[] { l1, l2 }, l, 1.0);
		}

		// for (double l1 = 0.0; l1 <= l; l1 += 0.1) {
		// System.out.println(i);
		// i++;
		//			 
		// for (double l2 = 0.0; l2 <= l; l2 += 0.1) {
		// MultiVariatePieceWiseLinearFunction function = createFunction(
		// new double[] { l1, l2 }, l, 1.0);
		// }
		// }

		// MultiVariatePieceWiseLinearFunction function = createFunction(
		// new double[] { l1, l2 }, l, 1.0);
		//
		// // double overlap12 = function.evaluate(new double[] { 10.0, 3.3 });
		// // double overlap23 = function.evaluate(new double[] { 10.0, 6.7 });
		// // double overlap13 = function.evaluate(new double[] { 6.7, 3.3 });
		// //
		// // System.out.println(overlap12);
		// // System.out.println(overlap13);
		// // System.out.println(overlap23);
		// //
		// // System.out.println(overlap12 + overlap13 + overlap23);
		//
		// for (double x1 = 0; x1 <= l; x1 += 0.5) {
		// for (double x2 = 0; x2 <= l; x2 += 0.5) {
		//
		// System.out.println(x1 + " " + x2 + " "
		// + function.evaluate(x1, x2));
		// }
		// }
	}

	public MultiVariatePieceWiseLinearFunction create(
			List<ContinuousVariable> variableList) {
		double[] dutyCycles = new double[variableList.size()];

		for (int i = 0; i < dutyCycles.length; i++) {
			dutyCycles[i] = ((SensorStartVariable) variableList.get(i))
					.getDutyCycle();
		}

		return createFunction(dutyCycles, scheduleIntervalLength, overlapSize);
	}
}
