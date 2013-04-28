package maxSumController.multiball;

import maxSumController.FunctionNode;
import maxSumController.InternalFunction;
import maxSumController.VariableNode;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteMaxSumController;
import maxSumController.multiball.nonlinearoptimiser.NonLinearOptimiser;
import maxSumController.multiball.nonlinearoptimiser.NonLinearOptimiserCloneFactory;
import maxSumController.multiball.nonlinearoptimiser.NonLinearOptimiserFactory;

public class MultiballMaxSumController extends
		DiscreteMaxSumController<MultiballInternalFunction> {

	private NonLinearOptimiserFactory optimiserFactory;

	public MultiballMaxSumController(Comparable<?> agentIdentifier,
			NonLinearOptimiser optimiser) {
		super(agentIdentifier, new MultiballMarginalMaximisation());
		this.optimiserFactory = new NonLinearOptimiserCloneFactory(optimiser);
	}
	
	public MultiballMaxSumController(Comparable<?> agentIdentifier,
			NonLinearOptimiserFactory optimiserFactory) {
		super(agentIdentifier, new MultiballMarginalMaximisation());
		this.optimiserFactory = optimiserFactory;
	}

	@Override
	protected FunctionNode createFunctionNode(InternalFunction function) {
		return new MultiballFunctionNode((MultiballInternalFunction) function,
				maximiserFactory.create(), optimiserFactory.create());
	}

	@Override
	protected VariableNode<?, ?> createVariableNode(
			DiscreteInternalVariable<?> variable) {
		return new MultiballVariableNode(
				(DiscreteInternalVariable<MultiballVariableState>) variable,
				optimiserFactory.create());
	}

}
