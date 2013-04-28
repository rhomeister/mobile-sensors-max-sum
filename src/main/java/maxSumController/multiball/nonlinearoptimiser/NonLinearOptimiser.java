package maxSumController.multiball.nonlinearoptimiser;

import maxSumController.AbstractVariable;
import maxSumController.discrete.DiscreteMarginalValues;
import maxSumController.discrete.DiscreteVariableDomain;
import maxSumController.multiball.DerivativeFunction;
import maxSumController.multiball.MultiballVariableState;

public abstract class NonLinearOptimiser {

	public boolean reportMarginalFunction;
	public boolean reportDerivative;
	public boolean reportSecondDerivative;

	public abstract void updateStates(AbstractVariable<DiscreteVariableDomain<MultiballVariableState>,
			MultiballVariableState>	variable,
			DiscreteMarginalValues<MultiballVariableState> marginalFunction,
			DerivativeFunction derivativeFunction,
			DerivativeFunction secondDerivativeFunction);

	public abstract NonLinearOptimiser copy();
}
