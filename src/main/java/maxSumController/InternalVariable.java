package maxSumController;

import java.util.Set;

public interface InternalVariable<D extends VariableDomain<S>, S extends VariableState>
		extends Variable<D, S> {

	/**
	 * An object that identifies the agent that controlles this variable.
	 * 
	 * @param descriptor
	 */
	public void addFunctionDependency(Function function);

	public void removeFunctionDependency(String name);

	public Set<Function> getFunctionDependencies();

	public MarginalValues<S> getPreference(MarginalValues<S> marginalValues);
}