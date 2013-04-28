package boundedMaxSum;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import maxSumController.DiscreteVariableState;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.DiscreteVariableDomain;
import maxSumController.discrete.VariableJointState;
import maxSumController.io.Color;
import maxSumController.io.ColorDomain;

public class TestTwoVariableRandomPayoff extends TestCase {

	private TwoVariablesRandomPayoffFunction function;
	private DiscreteInternalVariable<Color> v0;
	private DiscreteInternalVariable<Color> v1;

	@Override
	protected void setUp() throws Exception {
		DiscreteVariableDomain<Color> domain = new ColorDomain();

		for (int i = 0; i < 2; i++) {
			domain.add(new Color("" + i));
		}

		v0 = new DiscreteInternalVariable<Color>("v0", domain);
		v1 = new DiscreteInternalVariable<Color>("v1", domain);

		function = new TwoVariablesRandomPayoffFunction("testFunction", v0, v1);

		Map<VariableJointState, Double> randomMatrix = function
				.getPayoffMatrix();
		System.out.println(function.getPayoffMatrix());

		int val = 1;
		Map<VariableJointState, Double> payoffMatrix = new HashMap<VariableJointState, Double>();
		// for (Color varState0 : v0.getDomain().getStates()) {
		// for (Color varState1 : v1.getDomain().getStates()) {
		// Map<DiscreteVariable<Color>, DiscreteVariableState>
		// currentJointStateMap = new HashMap<DiscreteVariable<Color>,
		// DiscreteVariableState>();
		// currentJointStateMap.put(v0, varState0);
		// currentJointStateMap.put(v1, varState1);
		// VariableJointState currentJointState = new
		// VariableJointState(currentJointStateMap);
		// Double currentJointStateValue = (double) val++;
		// payoffMatrix.put(currentJointState, currentJointStateValue);
		// }
		// }

		Map<DiscreteVariable<Color>, DiscreteVariableState> currentJointStateMap = new HashMap<DiscreteVariable<Color>, DiscreteVariableState>();
		currentJointStateMap.put(v0, (Color) v0.getDomain().getStates()
				.toArray()[0]);
		currentJointStateMap.put(v1, (Color) v1.getDomain().getStates()
				.toArray()[0]);
		VariableJointState currentJointState = new VariableJointState(
				currentJointStateMap);
		Double currentJointStateValue = (double) 1;
		payoffMatrix.put(currentJointState, currentJointStateValue);

		currentJointStateMap = new HashMap<DiscreteVariable<Color>, DiscreteVariableState>();
		currentJointStateMap.put(v0, (Color) v0.getDomain().getStates()
				.toArray()[0]);
		currentJointStateMap.put(v1, (Color) v1.getDomain().getStates()
				.toArray()[1]);
		currentJointState = new VariableJointState(currentJointStateMap);
		currentJointStateValue = (double) 2;
		payoffMatrix.put(currentJointState, currentJointStateValue);

		currentJointStateMap = new HashMap<DiscreteVariable<Color>, DiscreteVariableState>();
		currentJointStateMap.put(v0, (Color) v0.getDomain().getStates()
				.toArray()[1]);
		currentJointStateMap.put(v1, (Color) v1.getDomain().getStates()
				.toArray()[0]);
		currentJointState = new VariableJointState(currentJointStateMap);
		currentJointStateValue = (double) 4;
		payoffMatrix.put(currentJointState, currentJointStateValue);

		currentJointStateMap = new HashMap<DiscreteVariable<Color>, DiscreteVariableState>();
		currentJointStateMap.put(v0, (Color) v0.getDomain().getStates()
				.toArray()[1]);
		currentJointStateMap.put(v1, (Color) v1.getDomain().getStates()
				.toArray()[1]);
		currentJointState = new VariableJointState(currentJointStateMap);
		currentJointStateValue = (double) 3;
		payoffMatrix.put(currentJointState, currentJointStateValue);

		function.setPayoffMatrix(payoffMatrix);

	}

	public void testWeights() throws Exception {

		Map<VariableJointState, Double> randomMatrix = function
				.getPayoffMatrix();

		double expectedW0 = 3.0;
		double actualW0 = function.getWeight(v0.getName());

		double expectedW1 = 1.0;
		double actualW1 = function.getWeight(v1.getName());

		double expectedMin = 1.0;
		double actualMin = function.getMinimumBound();

		double expectedMax = 4.0;
		double actualMax = function.getMaximumBound();

		System.out.println("Payoff matrix for " + function.getName() + " : "
				+ randomMatrix);

		System.out.println("w(0) " + function.getWeight(v0.getName()));
		System.out.println("w(1) " + function.getWeight(v1.getName()));

		System.out.println("min " + function.getMinimumBound());
		System.out.println("max " + function.getMaximumBound());

		assertEquals(expectedW0, actualW0);
		assertEquals(expectedW1, actualW1);
		assertEquals(expectedMin, actualMin);
		assertEquals(expectedMax, actualMax);

	}

	public void testEvaluate() throws Exception {
		double expectedValue = 4.0;
		Map<DiscreteVariable<Color>, DiscreteVariableState> currentJointStateMap = new HashMap<DiscreteVariable<Color>, DiscreteVariableState>();
		currentJointStateMap.put(v0, (Color) v0.getDomain().getStates()
				.toArray()[1]);
		currentJointStateMap.put(v1, (Color) v1.getDomain().getStates()
				.toArray()[0]);
		VariableJointState currentJointState = new VariableJointState(
				currentJointStateMap);

		double actualValue = function.evaluate(currentJointState);

		assertEquals(expectedValue, actualValue);

	}

	public void testNewFunction() throws Exception {

		System.out.println(function.payoffMatrix);

		Map<VariableJointState, Double> expectedMatrixV1 = new HashMap<VariableJointState, Double>();

		Map<DiscreteVariable<Color>, DiscreteVariableState> currentJointStateMap = new HashMap<DiscreteVariable<Color>, DiscreteVariableState>();
		currentJointStateMap.put(v1, (Color) v1.getDomain().getStates()
				.toArray()[1]);
		VariableJointState currentJointState = new VariableJointState(
				currentJointStateMap);
		Double currentJointStateValue = (double) 2.0;
		expectedMatrixV1.put(currentJointState, currentJointStateValue);

		currentJointStateMap = new HashMap<DiscreteVariable<Color>, DiscreteVariableState>();
		currentJointStateMap.put(v1, (Color) v1.getDomain().getStates()
				.toArray()[0]);
		currentJointState = new VariableJointState(currentJointStateMap);
		currentJointStateValue = (double) 1.0;
		expectedMatrixV1.put(currentJointState, currentJointStateValue);

		System.out.println("expected " + expectedMatrixV1);

		LinkBoundedInternalFunction newFunction = function.getNewFunction(v0
				.getName());
		SingleVariablesRandomPayoffFunction singleFunction = (SingleVariablesRandomPayoffFunction) newFunction;
		System.out.println("actual " + singleFunction.getPayoffMatrix());

		assertEquals(expectedMatrixV1, singleFunction.getPayoffMatrix());

		Map<VariableJointState, Double> expectedMatrixV0 = new HashMap<VariableJointState, Double>();

		currentJointStateMap = new HashMap<DiscreteVariable<Color>, DiscreteVariableState>();
		currentJointStateMap.put(v0, (Color) v0.getDomain().getStates()
				.toArray()[1]);
		currentJointState = new VariableJointState(currentJointStateMap);
		currentJointStateValue = (double) 3.0;
		expectedMatrixV0.put(currentJointState, currentJointStateValue);

		currentJointStateMap = new HashMap<DiscreteVariable<Color>, DiscreteVariableState>();
		currentJointStateMap.put(v0, (Color) v0.getDomain().getStates()
				.toArray()[0]);
		currentJointState = new VariableJointState(currentJointStateMap);
		currentJointStateValue = (double) 1.0;
		expectedMatrixV0.put(currentJointState, currentJointStateValue);

		System.out.println("expected " + expectedMatrixV0);

		LinkBoundedInternalFunction newFunction2 = function.getNewFunction(v1
				.getName());
		SingleVariablesRandomPayoffFunction singleFunction2 = (SingleVariablesRandomPayoffFunction) newFunction2;
		System.out.println("actual " + singleFunction2.getPayoffMatrix());

		assertEquals(expectedMatrixV0, singleFunction2.getPayoffMatrix());

	}

	public void testArgMin() throws Exception {
		System.out.println(function.payoffMatrix);

		Color argmin = function.argMin((Color) v0.getDomain().getStates()
				.toArray()[0], v1, v0);
		Color expectedArgMin = (Color) v0.getDomain().getStates().toArray()[0];
		assertEquals(expectedArgMin, argmin);
		System.out.println("expected " + expectedArgMin);
		System.out.println("actual argmin v0 = "
				+ (Color) v0.getDomain().getStates().toArray()[0]
				+ " for v1 : " + argmin);

		Color argmin2 = function.argMin((Color) v1.getDomain().getStates()
				.toArray()[0], v0, v1);
		Color expectedArgMin2 = (Color) v1.getDomain().getStates().toArray()[0];
		assertEquals(expectedArgMin2, argmin2);
		System.out.println("expected " + expectedArgMin2);
		System.out.println("argmin v1 = "
				+ (Color) v1.getDomain().getStates().toArray()[0]
				+ " for v0 : " + argmin2);

		Color argmin3 = function.argMin((Color) v0.getDomain().getStates()
				.toArray()[1], v1, v0);
		Color expectedArgMin3 = (Color) v0.getDomain().getStates().toArray()[1];
		assertEquals(expectedArgMin3, argmin3);
		System.out.println("expected " + expectedArgMin3);
		System.out.println("argmin v0 = "
				+ (Color) v0.getDomain().getStates().toArray()[1]
				+ " for v1 : " + argmin3);

		Color argmin4 = function.argMin((Color) v1.getDomain().getStates()
				.toArray()[1], v0, v1);
		Color expectedArgMin4 = (Color) v1.getDomain().getStates().toArray()[0];
		assertEquals(expectedArgMin4, argmin4);
		System.out.println("expected " + expectedArgMin4);
		System.out.println("argmin v1 = "
				+ (Color) v1.getDomain().getStates().toArray()[1]
				+ " for v0 : " + argmin4);
	}

}
