package senseSleep;

import org.apache.commons.lang.Validate;

public class SenseSleepFunctionWrapAroundValidator {

	private double overlapSize;

	private double schedulingInterval;

	public SenseSleepFunctionWrapAroundValidator(String name,
			double overlapSize, double schedulingInterval) {
		this.overlapSize = overlapSize;
		this.schedulingInterval = schedulingInterval;
	}

	public double evaluate(double[] dutyCycles, double[] startTimes) {
		Validate.isTrue(dutyCycles.length <= 2);

		if (dutyCycles.length == 1) {
			return dutyCycles[0];
		}

		double l1 = dutyCycles[0];
		double l2 = dutyCycles[1];

		Validate.isTrue(l1 <= schedulingInterval);
		Validate.isTrue(l2 <= schedulingInterval);

		double x1 = startTimes[0];
		double x2 = startTimes[1];
		double l = schedulingInterval;

		double l3 = Math.max(x1 + l1 - l, 0);
		double l4 = Math.max(x2 + l2 - l, 0);

		l1 = Math.min(l1, l - x1);
		l2 = Math.min(l2, l - x2);

		double x3 = 0;
		double x4 = 0;

		double O_1 = l1 + l2 + l3 + l4;

		double O12 = computePairWiseOverlap(x1, x2, l1, l2);
		double O14 = computePairWiseOverlap(x1, x4, l1, l4);
		double O23 = computePairWiseOverlap(x2, x3, l2, l3);
		double O34 = computePairWiseOverlap(x3, x4, l3, l4);

		double O_2 = O12 + O14 + O23 + O34;

		return (O_1 - O_2) * overlapSize;
	}

	private double computePairWiseOverlap(double x1, double x2, double l1,
			double l2) {
		return Math.max(0, Math.min(x1 + l1, x2 + l2) - Math.max(x1, x2));
	}

	// public static void main(String[] args) {
	// EventDetectionFunctionWrapAround function2 = new
	// EventDetectionFunctionWrapAround(
	// "test", 1.0);
	//
	// double[] dutyCycles = { 2.0, 3.0 };
	//
	// List<SensorProperties> sensors = new ArrayList<SensorProperties>();
	//
	// for (int i = 0; i < dutyCycles.length; i++) {
	// double d = dutyCycles[i];
	// sensors.add(new SensorProperties(d));
	// }
	//
	// MultiVariatePieceWiseLinearFunction function =
	// SenseSleepUtilityFunctionFactory
	// .createSenseSleepFunction(new ScenarioProperties(10.0), sensors);
	//
	// double[] startTimes = { 0.0, 1.0 };
	// double d = function2.evaluate(dutyCycles, startTimes);
	//
	// double q = function.evaluate(startTimes);
	//
	// System.out.println(d);
	// System.out.println(q);
	//
	// }
}
