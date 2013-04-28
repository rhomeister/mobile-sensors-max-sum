package boundedMaxSum;

import java.util.HashMap;
import java.util.Map;

import maxSumController.DiscreteInternalFunction;
import maxSumController.DiscreteVariableState;
import maxSumController.Variable;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.VariableJointState;

/**
 * A function able to return its maximum and minimum bounds.
 * 
 * @author mw08v
 * 
 */
public abstract class BoundedInternalFunction extends DiscreteInternalFunction
		implements BoundedFunctionInterface {

	public BoundedInternalFunction(String name) {
		super(name);
	}

	public abstract double getMinimumBound();

	public abstract double getMaximumBound();

	public abstract BoundedInternalFunction clone();

	public VariableJointState createcurrentJointState(
			VariableJointState currentConfiguration) {
		Map<DiscreteInternalVariable<?>, DiscreteVariableState> stateMap = new HashMap<DiscreteInternalVariable<?>, DiscreteVariableState>();
		for (Variable<?, ?> variable : getVariableDependencies()) {
			DiscreteInternalVariable<?> intVariable = (DiscreteInternalVariable<?>) variable;
			stateMap.put(intVariable, currentConfiguration
					.getVariableJointStates().get(intVariable));
		}
		VariableJointState res = new VariableJointState(stateMap);
		return res;
	}

	// public void updateVariableDependency(Set<DiscreteInternalVariable<?>>
	// updateVariables){
	// throw new
	// NotImplementedException("need to change current variables with the ones passed keeping the function the same");
	// }

}
