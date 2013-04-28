package maxSumController.multiball.nonlinearoptimiser;

import maxSumController.AbstractVariable;
import maxSumController.discrete.DiscreteMarginalValues;
import maxSumController.discrete.DiscreteVariableDomain;
import maxSumController.multiball.DerivativeFunction;
import maxSumController.multiball.MultiballVariableState;

public class GradientMethod extends NonLinearOptimiser {

	private StepSizeGenerator stepSize;

	public GradientMethod(double stepSize) {
		this(new ConstantStepSize(stepSize));
	}

	public GradientMethod(StepSizeGenerator stepSize) {
		this.stepSize = stepSize;
		reportMarginalFunction = false;
		reportDerivative = true;
		reportSecondDerivative = false;
	}

	@Override
	public void updateStates(AbstractVariable<
			DiscreteVariableDomain<MultiballVariableState>,
			MultiballVariableState> variable,
			DiscreteMarginalValues<MultiballVariableState> marginalFunction,
			DerivativeFunction derivativeFunction,
			DerivativeFunction secondDerivativeFunction) {
		for (MultiballVariableState state : variable.getDomain()) {
			state.update(stepSize.getStepSize() * derivativeFunction.getValue(state));
		}
	}

	@Override
	public NonLinearOptimiser copy() {
		return new GradientMethod(stepSize.copy());
	}

}
