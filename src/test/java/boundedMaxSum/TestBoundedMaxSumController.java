package boundedMaxSum;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import junit.framework.TestCase;
import maxSumController.DiscreteVariableState;
import maxSumController.FixedIterationStoppingCriterion;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.DiscreteVariableDomain;
import maxSumController.discrete.VariableJointState;
import maxSumController.io.Color;
import maxSumController.io.ColorDomain;

public class TestBoundedMaxSumController extends TestCase {

	private int numberOfColors = 2;
	private BoundedDiscreteMaxSumController controller;
	private double minimumFunction1;
	private double maximumFunction1;
	private double minimumFunction2;
	private double maximumFunction2;

	// private long seed = 4378227L; // known issue, with the GHS
	// implementation,
	// unsolved for now.
	// also, when more that one minimum weight is present tree might be
	// disconnected

	private long seed = 437822734L; // OK
	// private long seed = System.currentTimeMillis();

	private Random random = new Random(seed);
	private double epsilon = 1E-5;

	@Override
	protected void setUp() throws Exception {

		DiscreteVariableDomain<Color> domain = new ColorDomain(numberOfColors);

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
		minimumFunction1 = Collections.min(payoffMatrix1.values());
		maximumFunction1 = Collections.max(payoffMatrix1.values());

		minimumFunction2 = Collections.min(payoffMatrix2.values());
		maximumFunction2 = Collections.max(payoffMatrix2.values());

		// System.out.println(minimumFunction1);
		// System.out.println(maximumFunction1);
		// System.out.println(minimumFunction2);
		// System.out.println(maximumFunction2);
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

		controller = new BoundedDiscreteMaxSumController("controller");

		controller.addInternalFunction(rf1);
		controller.addInternalFunction(rf2);
		controller.addInternalFunction(rf3);

		controller.addInternalVariable(v1);
		controller.addInternalVariable(v2);
		controller.addInternalVariable(v3);

		// System.out.println(treecontroller);
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

		System.out.println("Synchronising the initial factor graph. Result: ");
		controller.synchroniseInitFactorGraph();

		System.out.println(controller);
	}

	public void testSimpleBounds() throws Exception {
		controller.synchroniseInitFactorGraph();
		double expectedMin = (2 * minimumFunction1 + minimumFunction2);
		double actualMin = controller.computeSimpleLowerBound();
		assertTrue(Math.abs(expectedMin - actualMin) <= epsilon);
		assertTrue(Math.abs((2 * maximumFunction1 + maximumFunction2)
				- controller.computeSimpleUpperBound()) <= epsilon);
	}

	public void testInitialize() throws Exception {
		System.out.println("controller before initialisation:");
		System.out.println(controller);

		controller.initialize();

		System.out.println("controller after initialisation:");
		System.out.println(controller);
	}

	public void testOptimalOnTreeValue() throws Exception {
		controller.initialize();
		Map<DiscreteVariable<?>, DiscreteVariableState> state = controller
				.getOptimalState();
		System.out.println("optimal state on tree \n"
				+ controller.getSpanningTreeValue(state));
	}

	public void testMSState() throws Exception {
		controller.initialize();
		int i = 0;
		while (i < 100) {
			controller.calculateNewOutgoingMessages();
			i++;
		}
		Map<DiscreteInternalVariable<?>, DiscreteVariableState> state = controller
				.computeCurrentState();
		System.out.println("maxsum state on tree \n" + state);
		System.out.println("maxsum state evaluated on tree \n"
				+ controller.getSpanningTreeValue(state));
		System.out.println("maxsum state evaluated on loopy Factor Graph \n"
				+ controller.getOriginalFactorGraphValue(state));
		System.out.println("maxsum upper bound \n"
				+ (controller.getSpanningTreeValue(state) + controller
						.getBound()));

		System.out.println("******************************************** \n");
		System.out.println("optimal state \n" + controller.getOptimalState());
		System.out.println("optimal value \n" + controller.getOptimalValue());
		System.out.println("max sum ratio = "
				+ controller.getApproxRatio(state));

		double expectedRatio = (1 + (controller.getSpanningTreeValue(state)
				+ controller.getBound() - controller
				.getOriginalFactorGraphValue(state))
				/ controller.getOptimalValue());
		System.out.println("maxsum ratio bound \n" + expectedRatio);
		assertTrue(Math.abs(expectedRatio - controller.getApproxRatio(state)) <= epsilon);
		assertTrue(controller.getSpanningTreeValue(state)
				+ controller.getBound() >= controller.getOptimalValue());

	}

	public void testOptimal() throws Exception {
		controller.initialize();
		System.out.println("optimal state \n" + controller.getOptimalState());
		System.out.println("optimal value \n" + controller.getOptimalValue());
	}

	public void testMetrics() throws Exception {
		controller.initialize();

		// execute maxsum

		int numberOfNodes = controller.getInternalFunctions().size()
				+ controller.getInternalVariables().size();
		controller.setStoppingCriterion(new FixedIterationStoppingCriterion(
				2 * numberOfNodes));
		while (!controller.stoppingCriterionIsMet()) {
			controller.calculateNewOutgoingMessages();
		}

		Map<DiscreteInternalVariable<?>, DiscreteVariableState> currentState = controller
				.computeCurrentState();

		// these four measure should be plotted on the same graph
		// if correct upperBound should always be the highest, treeValue should
		// always be the lowest
		// factorGraphValue should be between treeValue and optimalValue; the
		// closer to optimalValue the better
		double treeValue = controller.getSpanningTreeValue(currentState);
		double factorGraphValue = controller
				.getOriginalFactorGraphValue(currentState);
		double optimalValue = controller.getOptimalValue();
		double upperBoundValue = treeValue + controller.getBound();

		// this should be the total message expressed in number of values
		int messageSize = controller.getMessageSize();

		// this is the approximation ratio and should be higher than one, the
		// closer to one the better.
		double approxRatio = controller.getApproxRatio(currentState);

		System.out.println("treeValue: " + treeValue + " factorGraphValue: "
				+ factorGraphValue + " optimalValue: " + optimalValue
				+ " upperBoundValue: " + upperBoundValue);
		System.out.println("message size: " + messageSize + " approxRatio "
				+ approxRatio);
	}

}
