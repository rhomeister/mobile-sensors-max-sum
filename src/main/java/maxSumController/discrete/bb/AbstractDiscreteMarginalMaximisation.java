package maxSumController.discrete.bb;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import maxSumController.DiscreteInternalFunction;
import maxSumController.DiscreteVariableState;
import maxSumController.Variable;
import maxSumController.VariableState;
import maxSumController.discrete.DiscreteMarginalMaximisation;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.VariableJointState;
import maxSumController.multiball.MultiballVariableState;

public abstract class AbstractDiscreteMarginalMaximisation implements
		DiscreteMarginalMaximisation {

	@Override
	public VariableJointState getBestState(Variable<?, ?> fixedVariable,
			VariableState fixedState) {
		DiscreteInternalFunction function = getFunction();
		Set<DiscreteVariable<?>> discreteVariableDependencies = function
				.getDiscreteVariableDependencies();

		Map<DiscreteVariable<?>, DiscreteVariableState> result = new HashMap<DiscreteVariable<?>, DiscreteVariableState>();

		for (DiscreteVariable<?> discreteVariable : discreteVariableDependencies) {
			MultiballVariableState bestState = (MultiballVariableState) getBestState(
					fixedVariable, fixedState, discreteVariable);
			result.put(discreteVariable, bestState);
		}

		return new VariableJointState(result);

	}

	protected abstract DiscreteInternalFunction getFunction();

}
