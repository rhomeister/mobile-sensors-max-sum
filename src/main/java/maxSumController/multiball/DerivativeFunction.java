package maxSumController.multiball;

import maxSumController.discrete.DiscreteMarginalValues;
import maxSumController.discrete.DiscreteVariableDomain;

public class DerivativeFunction extends
		DiscreteMarginalValues<MultiballVariableState> {

	public DerivativeFunction(DiscreteMarginalValues<MultiballVariableState> state) {
		this();
		values = state.getValues();
	}

	public DerivativeFunction() {
		super();
	}

	public static DerivativeFunction createZeroFunction(
			DiscreteVariableDomain<MultiballVariableState> states) {

		DerivativeFunction derivativeFunction = new DerivativeFunction();
		for (MultiballVariableState multiballVariableState : states) {
			derivativeFunction.put(multiballVariableState, 0);
		}

		return derivativeFunction;
	}

}
