package boundedMaxSum;

import java.util.Set;

import maxSumController.discrete.DiscreteInternalVariable;

/**
 * Represents a function that we can approximate by removing a dependency from a
 * variable
 * 
 * @author sandrof
 * 
 */
public abstract class LinkBoundedInternalFunction extends
		BoundedInternalFunction implements LinkBoundedFunctionInterface {

	/**
	 * function constructor
	 * 
	 * @param name
	 */
	public LinkBoundedInternalFunction(String name) {
		super(name);
	}

	/**
	 * returns the weight we pay if we remove the function deletedVariable from
	 * the function. The weight is used to annotate the Factor graph and create
	 * the minimum spanning tree.
	 * 
	 * @param deletedVariable
	 * @return
	 */
	public abstract double getWeight(String deletedVariableName);

	/**
	 * returns the new function to use if the variable deletedVariable is
	 * removed
	 * 
	 * @param deletedVariable
	 * @return
	 */
	public abstract LinkBoundedInternalFunction getNewFunction(
			String deletedVariable);

	public abstract LinkBoundedInternalFunction getNewFunction(
			Set<DiscreteInternalVariable> rejectedVariables);

	public abstract double getBound(
			Set<DiscreteInternalVariable> rejectedVariables);

}
