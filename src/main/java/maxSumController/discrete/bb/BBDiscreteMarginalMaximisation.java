package maxSumController.discrete.bb;

import java.util.List;
import java.util.Map;
import java.util.Set;

import maxSumController.DiscreteInternalFunction;
import maxSumController.DiscreteVariableState;
import maxSumController.InternalFunction;
import maxSumController.MarginalMaximisation;
import maxSumController.MarginalMaximisationFactory;
import maxSumController.MarginalValues;
import maxSumController.Variable;
import maxSumController.VariableState;
import maxSumController.discrete.DiscreteMarginalValues;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.OptimisedDiscreteMarginalMaximisation;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import util.HashTripleStore;
import branchNbound.BranchAndBoundAlgorithm;

public class BBDiscreteMarginalMaximisation extends
		AbstractDiscreteMarginalMaximisation implements
		MarginalMaximisationFactory {

	private static Log log = LogFactory
			.getLog(BBDiscreteMarginalMaximisation.class);

	// private Set<DiscreteVariable> variables;

	// the number of nodes that has been expanded
	private static int nodesExpanded = 0;

	// the total number of nodes that should have been expanded if no heuristic
	// was used
	private static int totalNodes = 0;

	public static boolean validate = false;

	private OptimisedDiscreteMarginalMaximisation groundTruth = new OptimisedDiscreteMarginalMaximisation();

	private List<DiscreteVariable> variableExpansionOrder;

	private BBDiscreteInternalFunction function;

	private TripleStore<DiscreteVariable<?>, DiscreteVariableState, PartialJointVariableState<DiscreteVariableState>> bestStates = new HashTripleStore<DiscreteVariable<?>, DiscreteVariableState, PartialJointVariableState<DiscreteVariableState>>();

	public MarginalValues<?> calculateMarginalMaxFunction(
			Map<Variable<?, ?>, MarginalValues<?>> sortedMessages,
			Variable<?, ?> variable) {

		DiscreteVariable<?> destinationVariable = (DiscreteVariable<?>) variable;

		DiscreteMarginalValues<?> values = new DiscreteMarginalValues();

		for (DiscreteVariableState state : destinationVariable.getDomain()) {
			values.put(state, findMaxValue(destinationVariable, state,
					sortedMessages));
		}

		for (DiscreteVariable<?> var : function.getVariableExpansionOrder()) {
			if (!sortedMessages.containsKey(var))
				continue;

			DiscreteVariable var2 = (DiscreteVariable) function
					.getVariableDependency(var);

			// if (var2 == null) {
			// System.out.println(function.getVariableExpansionOrder());
			// System.out.println(var);
			// System.out.println(function.getVariableDependencies());
			// }
			//
			// if (!var.getDomain().equals(var2.getDomain())) {
			// System.out.println(var.getDomain());
			// System.out.println(var2.getDomain());
			// throw new IllegalArgumentException();
			// }

			DiscreteMarginalValues<?> marginalValues = (DiscreteMarginalValues<?>) sortedMessages
					.get(var);

			if (marginalValues == null) {
				System.out.println(sortedMessages);
				System.out.println(var);
				throw new IllegalArgumentException();
			}

			// if
			// (!marginalValues.getValues().keySet().equals(var.getDomain().getStates()))
			// {
			// System.out.println(marginalValues);
			// System.out.println(var.getDomain());
			// throw new IllegalArgumentException();
			// }

		}

		if (validate) {
			DiscreteMarginalValues<?> expectedValues = (DiscreteMarginalValues<?>) groundTruth
					.calculateMarginalMaxFunction(sortedMessages, variable);

			boolean error = false;

			for (DiscreteVariableState state : values.getValues().keySet()) {
				if (Math.abs(values.getValues().get(state)
						- expectedValues.getValues().get(state)) > 1e-10) {
					error = true;
					break;
				}
			}

			if (error) {
				System.out
						.println("Value from OptimisedMarginalMaximisation is not equal to BBDiscreteMarginalMaximisation");
				System.out.println(sortedMessages);

				System.out.println("Expected: " + expectedValues);
				System.out.println("Got " + values);

				for (DiscreteVariableState state : values.getValues().keySet()) {
					System.out.println(state);
					System.out.println(values.getValues().get(state));
					System.out.println(expectedValues.getValues().get(state));
				}

				throw new IllegalArgumentException();
			}
		}

		return values;
	}

	protected double findMaxValue(DiscreteVariable<?> targetVariable,
			DiscreteVariableState targetVariableState,
			Map<Variable<?, ?>, MarginalValues<?>> sortedMessages) {
		// create tree with the first decision as root, then find maximum with
		// BB
		PartialJointVariableState rootState = new PartialJointVariableState(
				variableExpansionOrder, targetVariable, targetVariableState);

		DiscreteMaxSumDecisionNode root = new DiscreteMaxSumDecisionNode(
				rootState, function, new MessageValueFunction(sortedMessages,
						targetVariable));

		BranchAndBoundAlgorithm<DiscreteMaxSumDecisionNode> bb = new BranchAndBoundAlgorithm<DiscreteMaxSumDecisionNode>(
				root);

		if (log.isDebugEnabled()) {
			System.out.println(function.getOwningAgentIdentifier());
			System.out.println(targetVariable);
			bb.debug();
		}

		try {
			bb.run();
		} catch (IllegalArgumentException e) {
			System.out.println(function.getOwningAgentIdentifier());
			System.out.println(targetVariable);

			throw e;
		}

		updateStatistics(bb, targetVariable);

		PartialJointVariableState optimalPartialJointState = bb.getIncumbent()
				.getState();

		bestStates.put(targetVariable, targetVariableState,
				optimalPartialJointState);

		return bb.getIncumbent().getLowerBound();
	}

	private void updateStatistics(
			BranchAndBoundAlgorithm<DiscreteMaxSumDecisionNode> bb,
			DiscreteVariable<?> targetVariable) {
		nodesExpanded += bb.getExpandedNodes();

		int nodesAtLevel = 1;
		totalNodes++;

		for (DiscreteVariable variable : variableExpansionOrder) {
			if (!variable.equals(targetVariable)) {
				nodesAtLevel *= variable.getDomainSize();
				totalNodes += nodesAtLevel;
			}
		}

		Validate.isTrue(nodesExpanded <= totalNodes);

	}

	public VariableState getBestState(Variable<?, ?> fixedVariable,
			VariableState fixedState, Variable<?, ?> targetVariable) {
		DiscreteVariable<DiscreteVariableState> discreteTargetVariable = (DiscreteVariable<DiscreteVariableState>) targetVariable;
		DiscreteVariableState state = bestStates.get(fixedVariable, fixedState)
				.getState(discreteTargetVariable);

		// the JointVariableState might not contain a state for this variable,
		// in which case it is a "don't care". Pick a random state
		// if (state == null) {
		// return discreteTargetVariable.getDomain().getStates().iterator()
		// .next();
		// }

		Validate.notNull(state);

		return state;
	}

	public void setFunction(InternalFunction function) {
		this.function = new BBFunctionCachingWrapper(
				(BBDiscreteInternalFunction) function);
		this.variableExpansionOrder = this.function.getVariableExpansionOrder();

		if (validate)
			groundTruth.setFunction(function);
	}

	public void setVariables(Set<? extends Variable<?, ?>> variables) {
		// this.variables = (Set<DiscreteVariable>) variables;
		if (validate)
			groundTruth.setVariables(variables);
	}

	public static int getNodesExpanded() {
		return nodesExpanded;
	}

	public static int getTotalNodes() {
		return totalNodes;
	}

	public static void resetStatistics() {
		totalNodes = 0;
		nodesExpanded = 0;
	}

	@Override
	public MarginalMaximisation create() {
		return new BBDiscreteMarginalMaximisation();
	}

	@Override
	public DiscreteInternalFunction getFunction() {
		return (DiscreteInternalFunction) function;
	}

}
