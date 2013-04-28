package maxSumController.discrete;

import maxSumController.AbstractExternalVariable;
import maxSumController.DiscreteVariableState;

public class DiscreteExternalVariable<T extends DiscreteVariableState> extends
		AbstractExternalVariable<DiscreteVariableDomain<T>, T> implements
		DiscreteVariable<T> {

	public DiscreteExternalVariable(String name,
			DiscreteVariableDomain<T> domain, Comparable owningAgentIdentifier) {
		super(name, domain, owningAgentIdentifier);
	}

	@Override
	public int getDomainSize() {
		return getDomain().size();
	}
}
