package maxSumController;

import maxSumController.communication.MaxSumMessage;

public class VariableToFunctionMessage extends
		MaxSumMessage<Variable<?, ?>, Function> {

	public VariableToFunctionMessage(Variable<?, ?> sender, Function receiver,
			MarginalValues<?> function) {
		super(sender, receiver, function);
	}

	public void normalise() {
		getMarginalFunction().normalise();
	}

}
