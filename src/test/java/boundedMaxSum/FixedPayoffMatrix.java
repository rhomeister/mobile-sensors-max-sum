package boundedMaxSum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import maxSumController.DiscreteVariableState;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.VariableJointState;

import org.apache.commons.lang.Validate;

public class FixedPayoffMatrix {

	private List<DiscreteInternalVariable<?>> variables;
	private Map<VariableJointState, Double> payoffMatrix = new HashMap<VariableJointState, Double>();

	public FixedPayoffMatrix(List<DiscreteInternalVariable<?>> variables) {
		this.variables = variables;
	}

	public void setValue(double value, DiscreteVariableState... state) {
		Validate.isTrue(state.length == variables.size());

		Map<DiscreteVariable<?>, DiscreteVariableState> jointState = new HashMap<DiscreteVariable<?>, DiscreteVariableState>();
		for (int i = 0; i < state.length; i++) {
			jointState.put(variables.get(i), state[i]);
		}

		payoffMatrix.put(new VariableJointState(jointState), value);
	}

	public double get(VariableJointState state) {
		if(!payoffMatrix.containsKey(state)) {
			throw new IllegalArgumentException(state + " is not defined");
		}
		
		return payoffMatrix.get(state);
	}
}
