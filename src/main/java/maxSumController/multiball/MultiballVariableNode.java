package maxSumController.multiball;

import java.util.Collection;

import maxSumController.FunctionToVariableMessage;
import maxSumController.VariableToFunctionMessage;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteMarginalValues;
import maxSumController.discrete.DiscreteVariableNode;
import maxSumController.multiball.nonlinearoptimiser.NonLinearOptimiser;

public class MultiballVariableNode extends
		DiscreteVariableNode<MultiballVariableState> {

	private NonLinearOptimiser optimiser;

	public MultiballVariableNode(
			DiscreteInternalVariable<MultiballVariableState> variable,
			NonLinearOptimiser optimiser) {
		super(variable);
		this.optimiser = optimiser;
	}

	@Override
	protected Collection<VariableToFunctionMessage> updateOutgoingMessages(
			Collection<FunctionToVariableMessage> incomingMessages) {
		// update the variable states

		DiscreteMarginalValues<MultiballVariableState> fullMarginalFunction = null;
		DerivativeFunction derivativeFunction = null;
		DerivativeFunction secondDerivativeFunction = null;

		if (optimiser.reportDerivative) {
			derivativeFunction = DerivativeFunction.createZeroFunction(variable
					.getDomain());
			for (FunctionToVariableMessage incomingMessage : incomingMessages) {
				try {
					MultiballFunctionToVariableMessage message = (MultiballFunctionToVariableMessage) incomingMessage;
					derivativeFunction = new DerivativeFunction(
							derivativeFunction.add(message
									.getDerivativeFunction()));
				} catch (ClassCastException e) {
					System.err.println(incomingMessage.getSender());
					System.err.println(incomingMessage.getClass());
					throw e;
				}

			}
		}
		if (optimiser.reportSecondDerivative) {
			secondDerivativeFunction = DerivativeFunction
					.createZeroFunction(variable.getDomain());
			for (FunctionToVariableMessage incomingMessage : incomingMessages) {
				MultiballFunctionToVariableMessage message = ((MultiballFunctionToVariableMessage) incomingMessage);
				secondDerivativeFunction = new DerivativeFunction(
						secondDerivativeFunction.add(message
								.getSecondDerivativeFunction()));
			}
		}

		if (optimiser.reportMarginalFunction) {
			fullMarginalFunction = variable.getDomain()
					.createZeroMarginalFunction();
			for (FunctionToVariableMessage incomingMessage : incomingMessages)
				fullMarginalFunction = fullMarginalFunction
						.add((DiscreteMarginalValues<MultiballVariableState>) incomingMessage
								.getMarginalFunction());
			fullMarginalFunction = fullMarginalFunction.add(variable.getPreference(fullMarginalFunction));
		}

		// at this point we can call for an update
		optimiser.updateStates(variable, fullMarginalFunction,
				derivativeFunction, secondDerivativeFunction);

		return super.updateOutgoingMessages(incomingMessages);
	}
}
