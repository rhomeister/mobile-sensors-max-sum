package maxSumController;

import java.util.Map;
import java.util.Set;

import maxSumController.discrete.VariableJointState;

public interface MarginalMaximisation {

	void setFunction(InternalFunction function);

	void setVariables(Set<? extends Variable<?, ?>> variables);

	MarginalValues<?> calculateMarginalMaxFunction(
			Map<Variable<?, ?>, MarginalValues<?>> sortedMessages,
			Variable<?, ?> variable);

	/**
	 * Returns the best state of the target variable, if a variable is fixed to
	 * a certain state
	 * 
	 * @param fixedVariable
	 *            the variable for which the state is fixed
	 * @param fixedState
	 *            the state to which the variable is fixed
	 * @param targetVariable
	 *            the variable for which the best state is required
	 * @return the best state of the targetVariable given the state of the fixed
	 *         variable
	 */
	VariableState getBestState(Variable<?, ?> fixedVariable,
			VariableState fixedState, Variable<?, ?> targetVariable);

	VariableJointState getBestState(Variable<?, ?> fixedVariable,
			VariableState fixedState);

}
