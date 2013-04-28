package maxSumController.discrete;

import maxSumController.DiscreteVariableState;
import maxSumController.MarginalValues;

public class DiscreteInternalVariable<S extends DiscreteVariableState> extends
		AbstractInternalVariable<DiscreteVariableDomain<S>, S> implements
		DiscreteVariable<S> {

	MarginalValues<S> preference;

	public DiscreteInternalVariable(String name,
			DiscreteVariableDomain<S> domain) {
		super(name, domain);
	}

	@Override
	public final void setDomain(DiscreteVariableDomain<S> variableDomain) {
		super.setDomain(variableDomain);
		preference = getDomain().createPreference();
	}

	public MarginalValues<S> getPreference(MarginalValues<S> marginalValues) {
		return preference;
	}

	public void setPreference(MarginalValues<S> newPreference) {
		preference = newPreference;
	}

	@Override
	public int getDomainSize() {
		return getDomain().size();
	}

	public DiscreteInternalVariable<S> clone() {
		DiscreteInternalVariable<S> clone = new DiscreteInternalVariable<S>(
				getName(), getDomain());
		clone.setOwningAgentIdentifier(getOwningAgentIdentifier());
		return clone;
	}
}
