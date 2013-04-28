package maxSumController.discrete;

import java.util.Collection;

import maxSumController.AbstractMaxSumController;
import maxSumController.DiscreteInternalFunction;
import maxSumController.DiscreteVariableState;
import maxSumController.FunctionNode;
import maxSumController.InternalFunction;
import maxSumController.MarginalMaximisationFactory;
import maxSumController.SingleMessageIterationPolicy;
import maxSumController.VariableNode;
import maxSumController.discrete.prune.PruneAlgorithmPlugin;
import maxSumController.discrete.prune.PruneMessage;

import org.apache.commons.lang.Validate;

/**
 * 
 * @author sandrof
 * 
 *         Represents the controller for the MaxSum algorithm This is
 *         responsible for computing outgoing messages for variables and
 *         functions, managing internal messages and returning messages that are
 *         addressed to other agents
 * 
 */

public class DiscreteMaxSumController<F extends DiscreteInternalFunction>
		extends
		AbstractMaxSumController<DiscreteInternalVariable<?>, DiscreteVariableState, F> {

	protected MarginalMaximisationFactory maximiserFactory;
	private PruneAlgorithmPlugin pruneAlgorithmPlugin;

	public DiscreteMaxSumController(Comparable<?> agentIdentifier,
			MarginalMaximisationFactory maximiserFactory) {
		super(agentIdentifier);
		setIterationPolicy(new SingleMessageIterationPolicy());
		this.maximiserFactory = maximiserFactory;
		pruneAlgorithmPlugin = new PruneAlgorithmPlugin(this);
		addAlgorithmPlugin(pruneAlgorithmPlugin, 0);
	}

	public DiscreteMaxSumController(Comparable<?> agentIdentifier) {
		this(agentIdentifier, new OptimisedDiscreteMarginalMaximisation());
	}

	@Override
	protected FunctionNode createFunctionNode(InternalFunction function) {
		return new FunctionNode(function, maximiserFactory.create());
	}

	@Override
	protected VariableNode<?, ?> createVariableNode(
			DiscreteInternalVariable<?> variable) {
		Validate.notEmpty(variable.getDomain().getStates());
		return new DiscreteVariableNode(variable);
	}

	public void startPruningAlgorithm() {
		setMaxSumEnabled(false);
		pruneAlgorithmPlugin.setEnabled(true);

		for (FunctionNode node : functionNodes) {
			Collection<PruneMessage> messages = node.startPruningAlgorithm();

			postOffice.process(messages);
		}
	}

	public boolean isPruningAlgorithmRunning() {
		return !pruneAlgorithmPlugin.isDone();
	}

	public boolean isMaxSumEnabled() {
		return maxSumPlugin.isEnabled();
	}

}
