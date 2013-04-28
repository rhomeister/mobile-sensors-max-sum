package maxSumController;

import maxSumController.discrete.VariableJointState;

public class PreferenceInternalFunction extends DiscreteInternalFunction {

	DiscreteInternalFunction function;

	private double minValue;

	// private double maxValue;
	protected double range;

	public PreferenceInternalFunction(DiscreteInternalFunction function,
			double minValue, double maxValue) {
		super(function.getName());
		this.function = function;
		this.minValue = minValue;
		// this.maxValue = maxValue;
		this.range = maxValue - minValue;
	}

	public PreferenceInternalFunction(DiscreteInternalFunction function) {
		this(function, 1.0);
	}

	public PreferenceInternalFunction(DiscreteInternalFunction function,
			double maxValue) {
		this(function, 0.0, maxValue);
	}

	@Override
	public double evaluate(VariableJointState state) {
		double originalValue = function.evaluate(state);

		double value = originalValue;

		value += minValue;
		value /= range;

		value += 100.;

		double perturbation = 1 + state.hashCode()
				/ ((double) Integer.MAX_VALUE * 1000);
		value *= perturbation;

		// System.out.println(state + " " + originalValue); // + " " + value);

		return value;
	}

}
