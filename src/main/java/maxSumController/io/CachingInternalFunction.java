package maxSumController.io;

import java.util.HashMap;
import java.util.Map;

import maxSumController.DiscreteInternalFunction;
import maxSumController.discrete.VariableJointState;

public class CachingInternalFunction extends DiscreteInternalFunction {

	private DiscreteInternalFunction function;

	private Map<VariableJointState, Double> values = new HashMap<VariableJointState, Double>();

	public CachingInternalFunction(DiscreteInternalFunction function) {
		super(function.getName());
		this.function = function;
	}

	@Override
	public double evaluate(VariableJointState state) {
		if (!values.containsKey(state)) {
			values.put(state, function.evaluate(state));
		} else {
			System.out.println("cached value");
		}

		return values.get(state);
	}
}
