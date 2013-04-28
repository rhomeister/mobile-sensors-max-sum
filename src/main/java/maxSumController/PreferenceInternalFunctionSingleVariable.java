package maxSumController;

import java.util.HashMap;
import java.util.Map;

import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.VariableJointState;

public class PreferenceInternalFunctionSingleVariable extends
		PreferenceInternalFunction {

	private DiscreteInternalVariable<?> myVariable;

	private Map<DiscreteVariableState, Double> preferences = new HashMap<DiscreteVariableState, Double>();

	public PreferenceInternalFunctionSingleVariable(
			DiscreteInternalFunction function,
			DiscreteInternalVariable<?> myVariable) {
		this(function, myVariable, 1.0);
	}

	public PreferenceInternalFunctionSingleVariable(
			DiscreteInternalFunction function,
			DiscreteInternalVariable<?> myVariable, double maxValue) {
		super(function, maxValue);

		this.myVariable = myVariable;

		for (DiscreteVariableState state : myVariable.getDomain()) {
			preferences.put(state, Math.random() * 0.0001);
		}
	}

	@Override
	public double evaluate(VariableJointState state) {
		double originalValue = function.evaluate(state);
		double value = originalValue;

		value /= range;

		double perturbation = preferences.get(state.get(myVariable));
		value += perturbation;

		return value;
	}

}
