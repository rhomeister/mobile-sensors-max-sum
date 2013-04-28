package maxSumController.discrete.bb;

import java.util.List;

import maxSumController.DiscreteVariableState;
import maxSumController.InternalFunction;
import maxSumController.Variable;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.VariableJointState;

public interface BBDiscreteInternalFunction extends InternalFunction {
	public List<DiscreteVariable> getVariableExpansionOrder();

	/**
	 * Returns an upper bound on maximum value of the function, when some of the
	 * variables have been set.
	 * 
	 * @param state
	 * @return
	 */
	public double getUpperBound(PartialJointVariableState state);

	/**
	 * Returns a lower bound on the maximum value of the function, when some of
	 * the variables have been set.
	 * 
	 * @param state
	 * @return
	 */
	public double getLowerBound(PartialJointVariableState state);

	/**
	 * Returns the true lower bound if the variable is set to the state that is
	 * passed as parameter
	 * 
	 * @param variable
	 * @param state
	 * @return
	 */
	public double getLowerBound(DiscreteVariable variable,
			DiscreteVariableState state);

	/**
	 * Returns the true lower bound if the variable is set to the state that is
	 * passed as parameter
	 * 
	 * @param variable
	 * @param state
	 * @return
	 */
	public double getUpperBound(DiscreteVariable variable,
			DiscreteVariableState state);

	public double evaluate(VariableJointState state);

	public void addVariableDependency(Variable<?, ?> variable);

	public void debug(PartialJointVariableState state);
}
