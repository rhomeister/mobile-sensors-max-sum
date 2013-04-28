package senseSleep;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import maxSumController.continuous.ContinuousVariable;
import maxSumController.continuous.linear.ContinuousInternalFunction;
import maxSumController.continuous.linear.MultiVariatePieceWiseLinearFunction;
import maxSumController.continuous.linear.MultiVariatePieceWiseLinearFunctionFactory;
import maxSumController.continuous.linear.MultiVariatePieceWiseLinearFunctionUtilities;
import maxSumController.continuous.linear.NCube;

import org.apache.commons.lang.time.StopWatch;

public class SenseSleepUtilityFunctionFactory implements
		MultiVariatePieceWiseLinearFunctionFactory {

	private double scheduleIntervalLength;

	private double overlapSize;

	public SenseSleepUtilityFunctionFactory(double overlapSize,
			double scheduleIntervalLength) {
		this.overlapSize = overlapSize;
		this.scheduleIntervalLength = scheduleIntervalLength;
	}

	public static MultiVariatePieceWiseLinearFunction createSenseSleepFunctionVerbose(
			ScenarioProperties scenario, List<SensorProperties> sensorProperties) {

		NCube domain = createDomain(sensorProperties, scenario);

		// max(0, min(x1 + l1, x2 + l2) - max(x1, x2))

		MultiVariatePieceWiseLinearFunction zero = MultiVariatePieceWiseLinearFunctionUtilities
				.createZeroFunction(domain);

		double l = scenario.getScheduleIntervalLength();

		StopWatch watch = new StopWatch();

		watch.start();
		MultiVariatePieceWiseLinearFunction maxStart = createMaxStartFunction(domain);
		watch.stop();
		System.out.println("Created maxStart in " + watch.getTime()
				+ ". Simplices "
				+ maxStart.getPartitioning().getSimplices().size());

		watch.reset();
		watch.start();
		MultiVariatePieceWiseLinearFunction minEnd = createMinEndFunction(
				domain, sensorProperties);
		watch.stop();
		System.out.println("Created minEnd in " + watch.getTime()
				+ ". Simplices "
				+ minEnd.getPartitioning().getSimplices().size());

		watch.reset();
		watch.start();
		MultiVariatePieceWiseLinearFunction minMinusMax = minEnd
				.subtract(maxStart);
		watch.stop();
		System.out.println("Created minMinusMax in " + watch.getTime()
				+ ". Simplices "
				+ minMinusMax.getPartitioning().getSimplices().size());

		watch.reset();
		watch.start();
		MultiVariatePieceWiseLinearFunction overlap = minMinusMax.max(zero);
		System.out.println("Created overlap in " + watch.getTime()
				+ ". Simplices "
				+ overlap.getPartitioning().getSimplices().size());

		MultiVariatePieceWiseLinearFunction result = overlap.subtract(l)
				.multiply(-1.0);

		System.out.println("Simplices "
				+ result.getPartitioning().getSimplices().size());

		return result;
	}

	public static MultiVariatePieceWiseLinearFunction createSenseSleepFunction(
			ScenarioProperties scenario, List<SensorProperties> sensorProperties) {
		return createSenseSleepFunction(scenario, sensorProperties, 1.0);
	}

	private static MultiVariatePieceWiseLinearFunction createMinEndFunction(
			NCube domain, List<SensorProperties> sensorProperties) {
		List<MultiVariatePieceWiseLinearFunction> endFunctionsFunctions = createEndFunctions(
				domain, sensorProperties);

		MultiVariatePieceWiseLinearFunction result = endFunctionsFunctions
				.get(0);

		for (int i = 1; i < endFunctionsFunctions.size(); i++) {
			result = result.min(endFunctionsFunctions.get(i));
		}
		return result;
	}

	private static MultiVariatePieceWiseLinearFunction createMaxStartFunction(
			NCube domain) {
		List<MultiVariatePieceWiseLinearFunction> startFunctions = createStartFunctions(domain);

		MultiVariatePieceWiseLinearFunction result = startFunctions.get(0);

		for (int i = 1; i < startFunctions.size(); i++) {
			result = result.max(startFunctions.get(i));
		}
		return result;
	}

	private static List<MultiVariatePieceWiseLinearFunction> createEndFunctions(
			NCube domain, List<SensorProperties> sensorProperties) {
		List<MultiVariatePieceWiseLinearFunction> startFunctions = createStartFunctions(domain);
		List<MultiVariatePieceWiseLinearFunction> result = new ArrayList<MultiVariatePieceWiseLinearFunction>();

		for (int i = 0; i < domain.getDimensionCount(); i++) {
			double senseIntervalLength = sensorProperties.get(i)
					.getSenseIntervalLength();
			result.add(startFunctions.get(i).add(senseIntervalLength));
		}

		return result;
	}

	private static List<MultiVariatePieceWiseLinearFunction> createStartFunctions(
			NCube domain) {
		List<MultiVariatePieceWiseLinearFunction> result = new Vector<MultiVariatePieceWiseLinearFunction>();

		for (int i = 0; i < domain.getDimensionCount(); i++) {
			result.add(MultiVariatePieceWiseLinearFunctionUtilities
					.createUnivariateFunction(domain, i));
		}

		return result;
	}

	private static NCube createDomain(List<SensorProperties> sensorProperties,
			ScenarioProperties scenario) {

		NCube domain = new NCube(sensorProperties.size());
		for (int i = 0; i < sensorProperties.size(); i++) {
			domain.setBoundaries(i, 0, scenario.getScheduleIntervalLength()
					- sensorProperties.get(i).getSenseIntervalLength());
		}

		return domain;
	}

	public static ContinuousInternalFunction createSenseSleepFunction(
			String name, double scheduleLength, double[] dutyCycles,
			double overlappingArea) {
		List<SensorProperties> sensorProperties = new ArrayList<SensorProperties>();

		for (double dutyCycle : dutyCycles) {
			sensorProperties.add(new SensorProperties(dutyCycle));
		}

		return new ContinuousInternalFunction(name, createSenseSleepFunction(
				new ScenarioProperties(scheduleLength), sensorProperties,
				overlappingArea));
	}

	public static MultiVariatePieceWiseLinearFunction createSenseSleepFunction(
			ScenarioProperties scenario,
			List<SensorProperties> sensorProperties, double overlappingArea) {
		System.out.println("Creating continuous sense sleep function with "
				+ sensorProperties.size() + " dimensions");

		NCube domain = createDomain(sensorProperties, scenario);

		if (sensorProperties.size() == 1) {
			return MultiVariatePieceWiseLinearFunctionUtilities
					.createConstantFunction(domain, overlappingArea
							* sensorProperties.iterator().next()
									.getSenseIntervalLength());
		}

		// max(0, min(x1 + l1, x2 + l2) - max(x1, x2))
		MultiVariatePieceWiseLinearFunction zero = MultiVariatePieceWiseLinearFunctionUtilities
				.createZeroFunction(domain);

		// double l = scenario.getScheduleIntervalLength();

		MultiVariatePieceWiseLinearFunction maxStart = createMaxStartFunction(domain);

		MultiVariatePieceWiseLinearFunction minEnd = createMinEndFunction(
				domain, sensorProperties);

		MultiVariatePieceWiseLinearFunction minMinusMax = minEnd
				.subtract(maxStart);

		MultiVariatePieceWiseLinearFunction overlap = minMinusMax.max(zero);

		// MultiVariatePieceWiseLinearFunction result =
		// overlap.add(-l).multiply(
		// -1.0);

		MultiVariatePieceWiseLinearFunction result = overlap;

		for (SensorProperties properties : sensorProperties) {
			result = result.add(-properties.getSenseIntervalLength());
		}

		result = result.multiply(-1);

		return result.multiply(overlappingArea);
	}

	@Override
	public MultiVariatePieceWiseLinearFunction create(
			List<ContinuousVariable> variableList) {
		List<SensorProperties> sensorProperties = new ArrayList<SensorProperties>();

		for (ContinuousVariable variable : variableList) {
			double dutyCycle = scheduleIntervalLength
					- variable.getDomain().getUpperBound();

			sensorProperties.add(new SensorProperties(dutyCycle));
		}

		return createSenseSleepFunction(new ScenarioProperties(
				scheduleIntervalLength), sensorProperties, overlapSize);
	}
}
