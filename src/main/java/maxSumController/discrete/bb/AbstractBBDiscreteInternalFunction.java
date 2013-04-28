package maxSumController.discrete.bb;

import maxSumController.DiscreteInternalFunction;

public abstract class AbstractBBDiscreteInternalFunction extends
		DiscreteInternalFunction implements BBDiscreteInternalFunction {

	public AbstractBBDiscreteInternalFunction(String name) {
		super(name);
	}

	@Override
	public void debug(PartialJointVariableState state) {

	}

	// public void debug() {
	// DiscreteVariable<?> rootVariable = getVariableExpansionOrder().get(0);
	//
	// System.out.println(this);
	//		
	// for (DiscreteVariableState state : rootVariable.getDomain()) {
	//
	// PartialJointVariableState rootState = new PartialJointVariableState(
	// getVariableExpansionOrder(), rootVariable, state);
	//
	// DiscreteMaxSumDecisionNode root = new DiscreteMaxSumDecisionNode(
	// rootState, this, new EmptyMessageValueFunction());
	//
	// BranchAndBoundAlgorithm<DiscreteMaxSumDecisionNode> bb = new
	// BranchAndBoundAlgorithm<DiscreteMaxSumDecisionNode>(
	// root);
	// bb.debug(false);
	// }
	//		
	// System.out.println();
	// }
}
