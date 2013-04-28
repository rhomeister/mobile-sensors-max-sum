package maxSumController;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractInternalFunction extends AbstractFunction
		implements InternalFunction {
	private Comparable owningAgentIdentifier;

	private Set<Variable<?, ?>> variables = new HashSet<Variable<?, ?>>();

	public AbstractInternalFunction(String name) {
		super(name);
	}

	public Set<Variable<?, ?>> getVariableDependencies() {
		return variables;
	}

	public Variable<?, ?> getVariableDependency(String variableName) {
		for (Variable<?, ?> variable : variables) {
			if (variable.getName().equals(variableName)) {
				return variable;
			}
		}

		return null;
	}

	@Override
	public Comparable getOwningAgentIdentifier() {
		return owningAgentIdentifier;
	}

	public void setOwningAgentIdentifier(Comparable owningAgentIdentifier) {
		this.owningAgentIdentifier = owningAgentIdentifier;
	}

	public void removeVariableDependency(String name) {
		Variable<?, ?> toremove = null;
		for (Variable<?, ?> v : variables) {
			if (v.getName().equals(name)) {
				toremove = v;
				break;
			}
		}
		if (toremove != null) {
			variables.remove(toremove);
		}
	}

	public void addVariableDependency(Variable<?, ?> variable) {
		variables.add(variable);

		if (variable instanceof InternalVariable) {
			InternalVariable<?, ?> internalVariable = (InternalVariable<?, ?>) variable;
			if (!internalVariable.getFunctionDependencies().contains(this))
				internalVariable.addFunctionDependency(this);
		}

		validateVariableDependency(variable);
	}

	protected void validateVariableDependency(Variable<?, ?> variable) {
	}

	public Set<? extends FactorGraphNode> getDependencies() {
		return getVariableDependencies();
	}

	@Override
	public Variable<?, ?> getVariableDependency(Variable<?, ?> variable) {
		for (Variable<?, ?> dependency : getVariableDependencies()) {
			if (variable.equals(dependency)) {
				return dependency;
			}
		}

		return null;
	}
}
