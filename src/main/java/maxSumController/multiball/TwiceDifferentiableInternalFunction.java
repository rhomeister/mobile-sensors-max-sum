package maxSumController.multiball;

import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.VariableJointState;

public abstract class TwiceDifferentiableInternalFunction extends
	MultiballInternalFunction {

	public TwiceDifferentiableInternalFunction(String name) {
		super(name);
	}

	public abstract double evaluateSecondDerivative(VariableJointState state, 
			DiscreteInternalVariable<MultiballVariableState> variableToDeriveBy);

}
