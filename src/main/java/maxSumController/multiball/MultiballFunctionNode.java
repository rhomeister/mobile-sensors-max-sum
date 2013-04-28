package maxSumController.multiball;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import maxSumController.FunctionNode;
import maxSumController.FunctionToVariableMessage;
import maxSumController.MarginalMaximisation;
import maxSumController.VariableToFunctionMessage;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.VariableJointState;
import maxSumController.multiball.nonlinearoptimiser.NonLinearOptimiser;

public class MultiballFunctionNode extends FunctionNode {

	private NonLinearOptimiser optimiser;

	public MultiballFunctionNode(MultiballInternalFunction function,
			MarginalMaximisation maximiser, NonLinearOptimiser optimiser) {
		super(function, maximiser);
		this.optimiser = optimiser;
	}

	protected MultiballInternalFunction getMultiballFunction() {
		return (MultiballInternalFunction) function;
	}

	@Override
	public Set<FunctionToVariableMessage> updateOutgoingMessages(
			Collection<VariableToFunctionMessage> incomingMessages) {

		Set<FunctionToVariableMessage> originalOutgoingMessages = super
				.updateOutgoingMessages(incomingMessages);

		if (!optimiser.reportDerivative)
			return originalOutgoingMessages;

		Set<FunctionToVariableMessage> outgoingMessages = new HashSet<FunctionToVariableMessage>();

		MultiballInternalFunction multiballFunction = getMultiballFunction();

		for (FunctionToVariableMessage message : originalOutgoingMessages) {

			if (!multiballFunction
					.isContinuous((DiscreteInternalVariable<?>) message
							.getReceiver())) {
				outgoingMessages.add(message);
				continue;
			}

			DiscreteInternalVariable<MultiballVariableState> receiver = (DiscreteInternalVariable<MultiballVariableState>) message
					.getReceiver();

			MultiballFunctionToVariableMessage multiballMessage;

			if (optimiser.reportSecondDerivative) {
				multiballMessage = new MultiballFunctionToVariableMessage(
						multiballFunction, receiver, message
								.getMarginalFunction(),
						new DerivativeFunction(), new DerivativeFunction());
			} else {
				multiballMessage = new MultiballFunctionToVariableMessage(
						multiballFunction, receiver, message
								.getMarginalFunction(),
						new DerivativeFunction(), null);
			}

			for (MultiballVariableState state : receiver.getDomain()) {
				VariableJointState argmax = maximiser.getBestState(receiver,
						state);

				multiballMessage.getDerivativeFunction().put(state,
						multiballFunction.evaluateDerivative(argmax, receiver));

				if (optimiser.reportSecondDerivative) {
					multiballMessage
							.getSecondDerivativeFunction()
							.put(
									state,
									((TwiceDifferentiableInternalFunction) multiballFunction)
											.evaluateSecondDerivative(argmax,
													receiver));
				}
			}

			outgoingMessages.add(multiballMessage);
		}

		return outgoingMessages;
	}
}
