package boundedMaxSum;

import maxSumController.AbstractMaxSumController;
import maxSumController.DiscreteInternalFunction;
import maxSumController.DiscreteVariableState;
import maxSumController.InternalFunction;
import maxSumController.VariableNode;
import maxSumController.discrete.DiscreteInternalVariable;

/**
 * 
 * A Discrete Max Sum Controller that handles clusters of variables.
 * 
 * 
 * @author sandrof
 * 
 * @param <V>
 *            the type of variables that will be handled by the controller
 * @param <F>
 *            the type of functions that will be handled by the controller
 */
public class ClusteredDiscreteMaxSumController<V extends DiscreteInternalVariable<?>, F extends DiscreteInternalFunction, S extends DiscreteVariableState>
		extends AbstractMaxSumController<DiscreteInternalVariable<S>, S, F> {

	public ClusteredDiscreteMaxSumController(Comparable agentIdentifier) {
		super(agentIdentifier);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ClusteredFunctionNode createFunctionNode(InternalFunction function) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected VariableNode<?, ?> createVariableNode(
			DiscreteInternalVariable<S> variable) {
		// TODO Auto-generated method stub
		return null;
	}

}
