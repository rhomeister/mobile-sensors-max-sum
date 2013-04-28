package maxSumController.continuous;

import maxSumController.AbstractMaxSumController;
import maxSumController.FunctionNode;
import maxSumController.InternalFunction;
import maxSumController.MarginalMaximisationFactory;
import maxSumController.SingleMessageIterationPolicy;
import maxSumController.VariableNode;
import maxSumController.continuous.linear.ContinuousInternalFunction;

/**
 * 
 * @author sandrof
 * 
 * Represents the controller for the MaxSum algorithm This is responsible for
 * computing outgoing messages for variables and functions, managing internal
 * messages and returning messages that are addressed to other agents
 * 
 */

public class ContinuousMaxSumController
		extends
		AbstractMaxSumController<ContinuousInternalVariable, ContinuousVariableState, ContinuousInternalFunction> {

	private MarginalMaximisationFactory maximiserFactory;

	public ContinuousMaxSumController(Comparable agentIdentifier,
			MarginalMaximisationFactory maximiserFactory) {
		super(agentIdentifier);
		setIterationPolicy(new SingleMessageIterationPolicy());
		this.maximiserFactory = maximiserFactory;
	}

	public ContinuousMaxSumController(Comparable agentIdentifier) {
		this(agentIdentifier, new TwoDimensionalLinearMarginalMaximisation());
	}

	@Override
	protected FunctionNode createFunctionNode(InternalFunction function) {
		return new FunctionNode(function, maximiserFactory.create());
	}

	@Override
	protected VariableNode<?, ?> createVariableNode(
			ContinuousInternalVariable variable) {
		return new ContinuousVariableNode(variable);
	}

}
