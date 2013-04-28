package maxSumController.continuous.linear;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import senseSleep.ScenarioProperties;
import senseSleep.SenseSleepUtilityFunctionFactory;
import senseSleep.SensorProperties;

public class TestSenseSleep2 extends TestCase {

	private MultiVariatePieceWiseLinearFunction function;

	private double l1 = 2.0;

	private double l2 = 3.0;

	private double l3 = 1.0;

	private double l = 10.0;

	private ScenarioProperties scenario;

	@Override
	protected void setUp() throws Exception {
		scenario = new ScenarioProperties(l);
	}

	public void test1DSenseSleepUtilityFunction() throws Exception {
		List<SensorProperties> sensorProperties = new ArrayList<SensorProperties>();
		sensorProperties.add(new SensorProperties(l1));

		function = SenseSleepUtilityFunctionFactory.createSenseSleepFunction(
				scenario, sensorProperties);

		for (double x1 = 0; x1 <= 8.0; x1 += 0.5) {
			double expected = l1;

			assertEquals(expected, function.evaluate(x1), 1e-7);

		}
	}

	public void test2DSenseSleepUtilityFunction() throws Exception {
		List<SensorProperties> sensorProperties = new ArrayList<SensorProperties>();
		sensorProperties.add(new SensorProperties(l1));
		sensorProperties.add(new SensorProperties(l2));

		function = SenseSleepUtilityFunctionFactory.createSenseSleepFunction(
				scenario, sensorProperties);

		for (double x1 = 0; x1 <= 8.0; x1 += 0.5) {
			for (double x2 = 0; x2 <= 7.0; x2 += 0.5) {
				double expected = l1
						+ l2
						- (Math.max(Math.min(x1 + l1, x2 + l2)
								- Math.max(x1, x2), 0.0));

				assertEquals(expected, function.evaluate(x1, x2), 1e-7);
			}
		}
	}

	// @Ignore
	// public void test3DSenseSleepUtilityFunction() throws Exception {
	// //
	// List<SensorProperties> sensorProperties = new
	// ArrayList<SensorProperties>();
	// sensorProperties.add(new SensorProperties(l1));
	// sensorProperties.add(new SensorProperties(l2));
	// sensorProperties.add(new SensorProperties(l3));
	// //
	// function = SenseSleepUtilityFunctionFactory
	// .createSenseSleepFunctionVerbose(scenario, sensorProperties);
	// //
	// // System.out.println(function.getPartitioning().getSimplices().size());
	// // System.out.println(function.getPartitioning().getDefiningCoordinates()
	// // .size());
	//
	// //
	// //
	// // for(NDimensionalPoint point :
	// // function.getPartitioning().getDefiningCoordinates()) {
	// // double x1 = point.getCoordinates()[0];
	// // double x2 = point.getCoordinates()[1];
	// // double x3 = point.getCoordinates()[2];
	// //
	// // double expected = l
	// // - Math.max(Math.min(Math.min(x1 + l1, x2 + l2), x3
	// // + l3)
	// // - Math.max(Math.max(x1, x2), x3), 0.0);
	// //
	// // double d = function.evaluate(point);
	// //
	// // assertEquals(expected, d, 1e-7);
	// //
	// //
	// // }
	//
	// for (double x1 = 0; x1 <= 8.0; x1 += 0.5) {
	// for (double x2 = 0; x2 <= 7.0; x2 += 0.5) {
	// for (double x3 = 0; x3 <= 9.0; x3 += 0.5) {
	// double expected = l
	// - Math.max(Math.min(Math.min(x1 + l1, x2 + l2), x3
	// + l3)
	// - Math.max(Math.max(x1, x2), x3), 0.0);
	//
	// double actual = function.evaluate(x1, x2, x3);
	//
	// NDimensionalPoint point = new NDimensionalPoint(x1, x2, x3);
	//
	// if (Math.abs(expected - actual) > 1e-7) {
	// NSimplex enclosingSimplex = function.getPartitioning()
	// .getEnclosingSimplex(point);
	// System.out.println(enclosingSimplex);
	//
	// for (NDimensionalPoint point1 : enclosingSimplex
	// .getPoints()) {
	// System.out.println(point1 + " "
	// + function.getValue(point1));
	// }
	// }
	//
	// assertEquals(expected, actual, 1e-7);
	// }
	// }
	// }
	// }

}
