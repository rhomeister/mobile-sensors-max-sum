package boundedMaxSum;

import java.util.Set;

import maxSumController.discrete.DiscreteInternalVariable;

public interface LinkBoundedFunctionInterface extends BoundedFunctionInterface{

	
	public abstract double getWeight(String deletedVariableName);

	public abstract LinkBoundedInternalFunction getNewFunction(String deletedVariable);

	public abstract LinkBoundedInternalFunction getNewFunction(
			Set<DiscreteInternalVariable> rejectedVariables);

	public abstract double getBound(Set<DiscreteInternalVariable> rejectedVariables);

	
	
}
