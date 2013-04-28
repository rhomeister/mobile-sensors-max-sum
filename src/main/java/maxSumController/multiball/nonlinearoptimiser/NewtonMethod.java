package maxSumController.multiball.nonlinearoptimiser;

import maxSumController.AbstractVariable;
import maxSumController.MarginalValues;
import maxSumController.discrete.DiscreteMarginalValues;
import maxSumController.discrete.DiscreteVariableDomain;
import maxSumController.multiball.DerivativeFunction;
import maxSumController.multiball.MultiballVariableState;

public class NewtonMethod extends NonLinearOptimiser{

	private StepSizeGenerator stepSize;
	private double maxStepSize;
	
	public NewtonMethod(double stepSize) {
		this(stepSize, 1);
	}

	public NewtonMethod(double stepSize, double maxStepSize) {
		this(new ConstantStepSize(stepSize), maxStepSize);
	}
	
	public NewtonMethod(StepSizeGenerator stepSize) {
		this(stepSize, 1);
	}
	
	public NewtonMethod(StepSizeGenerator stepSize, double maxStepSize) {
		this.stepSize = stepSize;
		this.maxStepSize = maxStepSize;
		reportMarginalFunction = false;
		reportDerivative = true;
		reportSecondDerivative = true;
	}
	
	@Override
	public void updateStates(AbstractVariable<
			DiscreteVariableDomain<MultiballVariableState>,
			MultiballVariableState> variable,
		DiscreteMarginalValues<MultiballVariableState> marginalFunction,
		DerivativeFunction derivativeFunction,
		DerivativeFunction secondDerivativeFunction) {

		for (MultiballVariableState state : variable.getDomain()) {
			double update = - stepSize.getStepSize() / secondDerivativeFunction.getValue(state);
			if((update <= 0) || (update > maxStepSize)) {
				update = maxStepSize;
			}
			update = update * derivativeFunction.getValue(state);
			state.update(update);
		}
	}
	@Override
	public NonLinearOptimiser copy() {
		return new NewtonMethod(stepSize.copy());
	}
}
