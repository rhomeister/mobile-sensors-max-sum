package boundedMaxSum;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import maxSumController.DiscreteVariableState;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.VariableJointState;

public class TestTrackingPayoffFunction extends TestCase {

	private TrackingVariable var1;
	private TrackingVariable var2;
	private TrackingVariable var3;
	private TargetTrackingPayoffFunction func5;
	private TrackingObject target4;
	private TrackingObject target5;
	private TrackingObject target6;
	private HashSet<DiscreteInternalVariable<TrackingObject>> variables1;
	private HashSet<DiscreteInternalVariable<TrackingObject>> variables2;
	private HashSet<DiscreteInternalVariable<TrackingObject>> variables3;

	@Override
	protected void setUp() throws Exception {

		variables1 = new HashSet<DiscreteInternalVariable<TrackingObject>>();
		variables2 = new HashSet<DiscreteInternalVariable<TrackingObject>>();
		variables3 = new HashSet<DiscreteInternalVariable<TrackingObject>>();

		Set<TrackingObject> tobjs1 = new HashSet<TrackingObject>();
		Set<TrackingObject> tobjs2 = new HashSet<TrackingObject>();
		target4 = new TrackingObject(4, 0., 0.);
		target5 = new TrackingObject(5, 0., 2.);
		target6 = new TrackingObject(6, 0., 4.);

		tobjs1.add(target5);
		tobjs1.add(target6);

		tobjs2.add(target5);
		tobjs2.add(target4);

		TrackingSensor sensor1 = new TrackingSensor(1, 0., 1., 2.);
		TrackingSensor sensor2 = new TrackingSensor(2, 0., 3., 2.);
		TrackingSensor sensor3 = new TrackingSensor(3, 1., 3., 2.);

		var1 = new TrackingVariable("v" + sensor1.getId(),
				TrackingDomainFactory.buildTrackingDomain(tobjs1), sensor1);
		var2 = new TrackingVariable("v" + sensor2.getId(),
				TrackingDomainFactory.buildTrackingDomain(tobjs2), sensor2);
		var3 = new TrackingVariable("v" + sensor3.getId(),
				TrackingDomainFactory.buildTrackingDomain(tobjs2), sensor3);

		variables1.add(var1);

		variables2.add(var1);
		variables2.add(var2);
		variables2.add(var3);

		variables3.add(var2);
		variables3.add(var3);

		func5 = new TargetTrackingPayoffFunction("function5", target5,
				variables2);

	}

	public void testEvaluate() throws Exception {
		Map<TrackingVariable, DiscreteVariableState> map = new HashMap<TrackingVariable, DiscreteVariableState>();
		map.put(var1, target5);
		map.put(var2, target5);
		map.put(var3, target6);

		VariableJointState state = new VariableJointState(map);

		Map<TrackingVariable, DiscreteVariableState> map1 = new HashMap<TrackingVariable, DiscreteVariableState>();
		map1.put(var1, target6);
		map1.put(var2, target5);
		map1.put(var3, target6);

		VariableJointState state1 = new VariableJointState(map1);

		assertTrue(func5.evaluate(state) > func5.evaluate(state1));

		double expectedValueState1 = 1 - (1 - var2.getSensor()
				.getIdentificationProbability(target5));
		assertEquals(expectedValueState1, func5.evaluate(state1));

		double expectedValueState = 1
				- (1 - var2.getSensor().getIdentificationProbability(target5))
				* (1 - var1.getSensor().getIdentificationProbability(target5));
		assertEquals(expectedValueState, func5.evaluate(state));

	}

	public void testGetWeight() throws Exception {

		assertEquals(0., func5.getMinimumBound());
		double expectedValue = 1
				- (1 - var1.getSensor().getIdentificationProbability(target5))
				* (1 - var2.getSensor().getIdentificationProbability(target5))
				* (1 - var3.getSensor().getIdentificationProbability(target5));
		assertEquals(expectedValue, func5.getMaximumBound());

		double expectedWeight1 = var1.getSensor().getIdentificationProbability(
				target5);
		assertEquals(expectedWeight1, func5.getWeight(var1.getName()));

		double expectedWeight2 = var2.getSensor().getIdentificationProbability(
				target5);
		assertEquals(expectedWeight2, func5.getWeight(var2.getName()));

		double expectedWeight3 = var3.getSensor().getIdentificationProbability(
				target5);
		assertEquals(expectedWeight3, func5.getWeight(var3.getName()));

	}

	public void testGetWeight2() throws Exception {

		TargetTrackingPayoffFunction func4 = new TargetTrackingPayoffFunction(
				"function4", target4, variables1);

		double expectedWeight1 = var1.getSensor().getIdentificationProbability(
				target4);
		assertEquals(expectedWeight1, func4.getWeight(var1.getName()));
	}

	public void testGetNewFunction() throws Exception {
		HashSet<DiscreteInternalVariable<TrackingObject>> variables2Deleted1 = new HashSet<DiscreteInternalVariable<TrackingObject>>();
		variables2Deleted1.add(var2);
		variables2Deleted1.add(var3);

		LinkBoundedInternalFunction newFunc5 = func5.getNewFunction(var1
				.getName());
		TargetTrackingPayoffFunction ttNewFunc5 = (TargetTrackingPayoffFunction) newFunc5;

		assertEquals(variables2Deleted1, ttNewFunc5.variables);
		assertEquals("" + func5.getName() + "d" + var1.getName(), ttNewFunc5
				.getName());
		System.out.println(ttNewFunc5.variables);
		System.out.println(func5.variables);

	}

}
