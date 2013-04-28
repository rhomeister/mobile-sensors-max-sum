package maxSumController;

import java.util.Set;

public interface InternalFunction extends Function {
	public Set<Variable<?, ?>> getVariableDependencies();

	public void setOwningAgentIdentifier(Comparable agentIdentifier);

	public void addVariableDependency(Variable<?, ?> variable);

	public Variable<?, ?> getVariableDependency(Variable<?, ?> sender);

	public Variable<?, ?> getVariableDependency(String name);
}
