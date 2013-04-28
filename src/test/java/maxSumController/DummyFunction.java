package maxSumController;

import maxSumController.discrete.VariableJointState;

public class DummyFunction extends DiscreteInternalFunction {

	public DummyFunction(String name) {
		super(name);
	}

	@Override
	public double evaluate(VariableJointState variables) {
		return 0;
	}

}
