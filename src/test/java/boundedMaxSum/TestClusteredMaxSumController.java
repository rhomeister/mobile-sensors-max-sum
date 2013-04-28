package boundedMaxSum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import junit.framework.TestCase;
import maxSumController.DiscreteInternalFunction;
import maxSumController.DiscreteVariableState;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteMaxSumController;
import maxSumController.discrete.DiscreteMaximiser;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.DiscreteVariableDomain;
import maxSumController.discrete.VariableJointState;
import maxSumController.io.Color;
import maxSumController.io.ColorDomain;

public class TestClusteredMaxSumController extends TestCase {

	private int numberOfColors = 2;
	private int numberOfVariables = 2;
	// private long seed = 437822734L;
	private long seed = System.currentTimeMillis();

	private Random random = new Random(seed);

	private ClusteredDiscreteMaxSumController<DiscreteInternalVariable<DiscreteVariableState>, DiscreteInternalFunction, DiscreteVariableState> clustercontroller;
	private DiscreteMaxSumController<DiscreteInternalFunction> controller;

	protected void setUp() {

		controller = new DiscreteMaxSumController<DiscreteInternalFunction>(
				"controller");
		clustercontroller = new ClusteredDiscreteMaxSumController<DiscreteInternalVariable<DiscreteVariableState>, DiscreteInternalFunction, DiscreteVariableState>(
				"clusteredController");
		DiscreteVariableDomain<Color> domain = new ColorDomain(numberOfColors);
		ArrayList<DiscreteVariableDomain<Color>> domains = new ArrayList<DiscreteVariableDomain<Color>>();

		DiscreteInternalVariable<Color> v1 = new DiscreteInternalVariable<Color>(
				"v1", domain);
		DiscreteInternalVariable<Color> v2 = new DiscreteInternalVariable<Color>(
				"v2", domain);
		DiscreteInternalVariable v3 = new DiscreteInternalVariable<Color>("v3",
				domain);

		JointDiscreteVariableDomain<JointColor> jointDomain12 = new JointColorDomain();

		for (Color state1 : v1.getDomain()) {
			for (Color state2 : v2.getDomain()) {
				Map<DiscreteVariable<Color>, DiscreteVariableState> currentJointStateMap = new HashMap<DiscreteVariable<Color>, DiscreteVariableState>();
				currentJointStateMap.put(v1, state1);
				currentJointStateMap.put(v2, state2);
				jointDomain12.add(new JointColor(currentJointStateMap));
			}
		}

		JointDiscreteVariableDomain<JointColor> jointDomain3 = new JointColorDomain();

		for (Color state3 : ((DiscreteInternalVariable<Color>) v3).getDomain()) {
			Map<DiscreteVariable<Color>, DiscreteVariableState> currentJointStateMap = new HashMap<DiscreteVariable<Color>, DiscreteVariableState>();
			currentJointStateMap.put(v3, state3);
			jointDomain3.add(new JointColor(currentJointStateMap));
		}

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

		controller.addInternalFunction(rf1);
		controller.addInternalFunction(rf2);
		controller.addInternalFunction(rf3);

		controller.addInternalVariable(v1);
		controller.addInternalVariable(v2);
		controller.addInternalVariable(v3);

		ClusteredVariable cv12 = new ClusteredVariable<JointColor>("cv12",
				jointDomain12);
		ClusteredVariable cv3 = new ClusteredVariable<JointColor>("cv3",
				jointDomain3);
		cv12.addVariable(v1);
		cv12.addVariable(v2);
		cv3.addVariable(v3);

		ClusteredTwoVariableRandomPayoffFunction crf1 = new ClusteredTwoVariableRandomPayoffFunction(
				rf1.getName(), rf1.clone());
		crf1.addClusterVariableDependency(cv12);
		crf1.setOriginalDependencies(rf1.getVariableDependencies());

		ClusteredTwoVariableRandomPayoffFunction crf2 = new ClusteredTwoVariableRandomPayoffFunction(
				rf2.getName(), rf2.clone());
		crf2.addClusterVariableDependency(cv12);
		crf2.setOriginalDependencies(rf2.getVariableDependencies());

		ClusteredTwoVariableRandomPayoffFunction crf3 = new ClusteredTwoVariableRandomPayoffFunction(
				rf3.getName(), rf3.clone());
		crf3.addClusterVariableDependency(cv3);
		crf3.setOriginalDependencies(rf3.getVariableDependencies());

		cv12.addFunctionDependency(crf1);
		cv12.addFunctionDependency(crf2);
		cv12.addFunctionDependency(crf3);
		cv3.addFunctionDependency(crf3);

		clustercontroller.addInternalVariable(cv12);
		clustercontroller.addInternalVariable(v3);

		clustercontroller.addInternalFunction(crf1);

		clustercontroller.addInternalFunction(crf2);

		clustercontroller.addInternalFunction(crf3);

	}

	private Map<VariableJointState, Double> buildPayoffMatrix(
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
		System.out.println("payoff Matrix \n" + payoffMatrix);

		return payoffMatrix;
	}

	public void testCreation() throws Exception {
		System.out.println(controller);
		System.out.println(clustercontroller);
	}

	public void testOptimal() throws Exception {
		DiscreteMaximiser<ClusteredVariable<?>, VariableJointState> clusterMaximiser = new DiscreteMaximiser<ClusteredVariable<?>, VariableJointState>(
				(Set<? extends DiscreteInternalVariable<?>>) clustercontroller
						.getInternalVariables(), clustercontroller
						.getInternalFunctions());
		Map<ClusteredVariable<?>, VariableJointState> clusteredState = clusterMaximiser
				.getOptimalState();

		DiscreteMaximiser<DiscreteVariable<?>, DiscreteVariableState> maximiser = new DiscreteMaximiser<DiscreteVariable<?>, DiscreteVariableState>(
				controller.getInternalVariables(), controller
						.getInternalFunctions());
		Map<DiscreteVariable<?>, DiscreteVariableState> state = maximiser
				.getOptimalState();

		System.out.println("Clustered state = " + clusteredState);
		System.out.println("Clustered state = " + state);

		// VariableJointState vjs = new VariableJointState(state);
		ClusteredVariableJointState cvjs = new ClusteredVariableJointState(
				clusteredState);

		assertEquals(cvjs.getVariableJointStates(), state);

	}

}
