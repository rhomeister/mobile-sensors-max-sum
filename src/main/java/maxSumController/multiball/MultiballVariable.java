package maxSumController.multiball;

import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteVariableDomain;

public class MultiballVariable extends
		DiscreteInternalVariable<MultiballVariableState> {

	public MultiballVariable(String name,
			DiscreteVariableDomain<MultiballVariableState> states) {
		super(name, states);
	}

}
