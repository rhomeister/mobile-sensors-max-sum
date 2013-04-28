package maxSumController;

import org.apache.commons.lang.builder.EqualsBuilder;

public abstract class AbstractVariable<D extends VariableDomain<S>, S extends VariableState>
		implements Variable<D, S> {
	private String name;

	protected D domain;

	protected Comparable owningAgentIdentifier;

	private int hashCode;

	public AbstractVariable(String name, D domain) {
		this.name = name;
		this.hashCode = name.hashCode();
		setDomain(domain);
	}

	@Override
	/*
	 * Two variables are equal if their names are equal
	 */
	public final boolean equals(Object other) {
		if (other == null)
			return false;

		if (other instanceof Variable<?, ?>) {
			Variable<?, ?> variable = (Variable<?, ?>) other;
			return new EqualsBuilder().append(variable.getName(), getName())
					.append(variable.getOwningAgentIdentifier(),
							getOwningAgentIdentifier()).isEquals();

			// return variable.getName().equals(getName())
			// && variable.getOwningAgentIdentifier().equals(
			// getOwningAgentIdentifier());
		}

		return false;
	}

	@Override
	public final int hashCode() {
		return hashCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see maxSumController.Variable#getName()
	 */
	public final String getName() {
		return name;
	}

	public final Comparable getOwningAgentIdentifier() {
		return owningAgentIdentifier;
	}

	@Override
	public String toString() {
		return "Variable " + name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see maxSumController.Variable#setDomain(maxSumController.VariableDomain)
	 */
	public void setDomain(D variableDomain) {
		this.domain = variableDomain;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see maxSumController.Variable#getDomain()
	 */
	public final D getDomain() {
		return domain;
	}

	public final void setOwningAgentIdentifier(Comparable agentIdentifier) {
		this.owningAgentIdentifier = agentIdentifier;
	}

	public int compareTo(FactorGraphNode o) {
		return getName().compareTo(o.getName());
	}
}
