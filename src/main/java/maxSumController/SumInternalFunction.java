package maxSumController;

import org.apache.commons.lang.Validate;

import maxSumController.discrete.VariableJointState;

public class SumInternalFunction extends DiscreteInternalFunction {

	private DiscreteInternalFunction function1;

	private DiscreteInternalFunction function2;

	public SumInternalFunction(String name, DiscreteInternalFunction function1,
			DiscreteInternalFunction function2) {
		super(name);
		this.function1 = function1;
		this.function2 = function2;
	}

	@Override
	public double evaluate(VariableJointState state) {
		double eval1 = function1.evaluate(state);
		Validate.isTrue(!Double.isNaN(eval1));

		double result = eval1;

		if (result != Double.NEGATIVE_INFINITY) {
			double eval2 = function2.evaluate(state);
			Validate.isTrue(!Double.isNaN(eval2));
			result += eval2;
			Validate.isTrue(!Double.isNaN(result), eval1 + " " + eval2);
		}

		return result;
	}
}
