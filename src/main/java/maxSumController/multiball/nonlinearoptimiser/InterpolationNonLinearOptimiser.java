package maxSumController.multiball.nonlinearoptimiser;

import maxSumController.AbstractVariable;
import maxSumController.discrete.DiscreteVariableDomain;
import maxSumController.discrete.DiscreteMarginalValues;
import maxSumController.multiball.DerivativeFunction;
import maxSumController.multiball.MultiballVariableState;
import maxSumController.multiball.math.InterpolationFunction;

public class InterpolationNonLinearOptimiser extends
	NonLinearOptimiser {

	private NonLinearOptimiser optimiser;
	
	private InterpolationFunction interpolation;
	
	public InterpolationNonLinearOptimiser(InterpolationFunction interpolation) {
		this.interpolation = interpolation;
		reportMarginalFunction = true;
		reportDerivative = false;
		reportSecondDerivative = false;
		optimiser = null;
	}
	public InterpolationNonLinearOptimiser(InterpolationFunction interpolation,
			NonLinearOptimiser optimiser) {
		this.interpolation = interpolation;
		reportMarginalFunction = true;
		reportDerivative = false;
		reportSecondDerivative = false;
		this.optimiser = optimiser;
	}

	public void setOptimiser(NonLinearOptimiser optimiser) {
		this.optimiser = optimiser;
	}
	
	@Override
	public void updateStates(AbstractVariable<
			DiscreteVariableDomain<MultiballVariableState>,
			MultiballVariableState> variable,
			DiscreteMarginalValues<MultiballVariableState> marginalFunction,
			DerivativeFunction derivativeFunction,
			DerivativeFunction secondDerivativeFunction) {
		DiscreteVariableDomain<MultiballVariableState> variableDomain = variable.getDomain();
		double[] functionValues = new double[variableDomain.size()];
		double[] stateValues = new double[variableDomain.size()];
		int i = 0;
		for (MultiballVariableState state : variableDomain) {
			stateValues[i] = state.getValue();
			functionValues[i] = marginalFunction.getValue(state);
			i++;
		}
		double xMin = stateValues[0];
		double xMax = stateValues[0];
		for (i = 1; i<stateValues.length; i++) {
			xMin = Math.min(xMin, Math.abs(stateValues[i]));
			xMax = Math.max(xMax, Math.abs(stateValues[i]));
		}
		double yMin = functionValues[0];
		double yMax = functionValues[0];
		for (i = 1; i<functionValues.length; i++) {
			yMin = Math.min(yMin, Math.abs(functionValues[i]));
			yMax = Math.max(yMax, Math.abs(functionValues[i]));
		}
		
		double bound = 100*((yMax - yMin)/(xMax - xMin));

		interpolation.setDataPoints(stateValues, functionValues);

		DerivativeFunction newDerivativeFunction = null;
		DerivativeFunction newSecondDerivativeFunction  = null;
		if(optimiser.reportDerivative) {
			newDerivativeFunction = new DerivativeFunction();
			for (MultiballVariableState state : variableDomain) {
				double deriv = interpolation.evaluateDerivative(state.getValue());
				if (Math.abs(deriv) > bound) deriv = Math.signum(deriv)*bound;
				newDerivativeFunction.put(state, deriv);
			}
		}
		if(optimiser.reportSecondDerivative) {
			newSecondDerivativeFunction = new DerivativeFunction();
			for (MultiballVariableState state : variableDomain){
				double deriv = interpolation.evaluateSecondDerivative(state.getValue());
				if (Math.abs(deriv) > bound) deriv = Math.signum(deriv)*bound;
				newSecondDerivativeFunction.put(state, deriv);
			}
		}
		optimiser.updateStates(variable, marginalFunction, 
				newDerivativeFunction, newSecondDerivativeFunction);
	}
	@Override
	public NonLinearOptimiser copy() {
		return new InterpolationNonLinearOptimiser(interpolation.copy(),
				optimiser.copy());
	}
		
}
