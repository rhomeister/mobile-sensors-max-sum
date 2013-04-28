package maxSumController.multiball;

import maxSumController.DiscreteVariableState;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.VariableJointState;

public interface MultiMaxContinuousFunction extends DiscreteVariableState {
	public double evaluate(VariableJointState state);
	public double evaluateDerivative(VariableJointState state, 
			DiscreteInternalVariable<MultiballVariableState> variableToDeriveBy);
}
