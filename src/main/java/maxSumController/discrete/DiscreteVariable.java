package maxSumController.discrete;

import maxSumController.DiscreteVariableState;
import maxSumController.Variable;

public interface DiscreteVariable<S extends DiscreteVariableState> extends
		Variable<DiscreteVariableDomain<S>, S>{

	public int getDomainSize();
}