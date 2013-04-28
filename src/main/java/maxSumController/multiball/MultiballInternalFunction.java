package maxSumController.multiball;

import maxSumController.DiscreteInternalFunction;
import maxSumController.Variable;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.VariableJointState;

public abstract class MultiballInternalFunction extends
		DiscreteInternalFunction {

	public MultiballInternalFunction(String name) {
		super(name);
	}

	public abstract double evaluateDerivative(VariableJointState state, 
			DiscreteInternalVariable<MultiballVariableState> variableToDeriveBy);
	
	public abstract boolean isContinuous(DiscreteInternalVariable<?> variableToTest);

}
