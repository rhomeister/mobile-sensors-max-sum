package maxSumController;

import maxSumController.communication.MaxSumMessage;

public class FunctionToVariableMessage extends
		MaxSumMessage<Function, Variable<?, ?>> {

	public FunctionToVariableMessage(Function sender, Variable<?, ?> receiver,
			MarginalValues<?> function) {
		super(sender, receiver, function);

		// TODO reimplement check
		// Set<? extends DiscreteVariableState> states = receiver.getDomain()
		// .getStates();
		// Set<DiscreteVariableState> keySet = function.getValues().keySet();
		// Validate.isTrue(states.containsAll(keySet));
	}
}
