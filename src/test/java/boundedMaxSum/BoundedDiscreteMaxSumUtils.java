package boundedMaxSum;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import maxSumController.DiscreteVariableState;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.DiscreteVariableDomain;
import maxSumController.discrete.VariableJointState;
import maxSumController.io.Color;
import maxSumController.io.ColorDomain;

public class BoundedDiscreteMaxSumUtils {

	private static long seed = 43782445L; //OK
//	private long seed = System.currentTimeMillis();
	
	private static Random random = new Random(seed);

	private static Map<VariableJointState, Double> buildPayoffMatrix(
			DiscreteInternalVariable<Color> v1,
			DiscreteInternalVariable<Color> v2) {

		Map<VariableJointState, Double> payoffMatrix = new HashMap<VariableJointState, Double>();

		for (Color state1 : v1.getDomain()) {
			for (Color state2 : v2.getDomain()) {
				Map<DiscreteVariable<Color>, DiscreteVariableState> currentJointStateMap = new HashMap<DiscreteVariable<Color>, DiscreteVariableState>();
				currentJointStateMap.put(v1, state1);
				currentJointStateMap.put(v2, state2);
				VariableJointState currentJointState = new VariableJointState(
						currentJointStateMap);
				payoffMatrix.put(currentJointState, random.nextDouble());
			}
		}

		return payoffMatrix;
	}

	public static long getSeed() {
		return seed;
	}
	
	public static void setSeed(long s) {
		seed = s;
	}
	
	public static BoundedDiscreteMaxSumController createBoundedController() {
		DiscreteVariableDomain<Color> domain = new ColorDomain(2);

		DiscreteInternalVariable<Color> v1 = new DiscreteInternalVariable<Color>(
				"v1", domain);
		DiscreteInternalVariable<Color> v2 = new DiscreteInternalVariable<Color>(
				"v2", domain);
		DiscreteInternalVariable<Color> v3 = new DiscreteInternalVariable<Color>(
				"v3", domain);

		Map<VariableJointState, Double> payoffMatrix1 = buildPayoffMatrix(v1,
				v2);
		Map<VariableJointState, Double> payoffMatrix2 = buildPayoffMatrix(v2,
				v3);

		System.out.println("Payoff matrices");
		System.out.println(payoffMatrix1);
		System.out.println(payoffMatrix2);

		TwoVariablesRandomPayoffFunction rf1 = new TwoVariablesRandomPayoffFunction(
				"rf1", v1, v2);
		rf1.setPayoffMatrix(payoffMatrix1);

		TwoVariablesRandomPayoffFunction rf2 = new TwoVariablesRandomPayoffFunction(
				"rf2", v1, v2);
		rf2.setPayoffMatrix(payoffMatrix1);

		TwoVariablesRandomPayoffFunction rf3 = new TwoVariablesRandomPayoffFunction(
				"rf3", v2, v3);
		rf3.setPayoffMatrix(payoffMatrix2);

		rf1.addVariableDependency(v1);
		rf1.addVariableDependency(v2);
		rf2.addVariableDependency(v1);
		rf2.addVariableDependency(v2);
		rf3.addVariableDependency(v2);
		rf3.addVariableDependency(v3);

		BoundedDiscreteMaxSumController controller = new BoundedDiscreteMaxSumController(
				"controller");

		controller.addInternalFunction(rf1);
		controller.addInternalFunction(rf2);
		controller.addInternalFunction(rf3);

		controller.addInternalVariable(v1);
		controller.addInternalVariable(v2);
		controller.addInternalVariable(v3);

				
		return controller;
	}

	
	
}
