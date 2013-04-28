package maxSumController;

import java.util.Set;

public abstract class AbstractExternalVariable<D extends VariableDomain<S>, S extends VariableState>
		extends AbstractVariable<D, S> implements ExternalVariable<D, S> {

	public AbstractExternalVariable(String name, D domain,
			Comparable owningAgentIdentifier) {
		super(name, domain);
		setOwningAgentIdentifier(owningAgentIdentifier);
	}

	public Set<? extends FactorGraphNode> getDependencies() {
		throw new IllegalArgumentException("The dependencies of ExternalVariables are unknown");
	}
}
