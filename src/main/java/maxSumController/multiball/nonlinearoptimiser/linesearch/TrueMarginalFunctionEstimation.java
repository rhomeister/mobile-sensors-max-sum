package maxSumController.multiball.nonlinearoptimiser.linesearch;

import maxSumController.discrete.DiscreteMarginalValues;
import maxSumController.discrete.DiscreteVariableDomain;
import maxSumController.multiball.MultiballVariableState;
import maxSumController.multiball.math.RealFunction;

public interface TrueMarginalFunctionEstimation extends RealFunction{

	/*
	 * This interface is for estimates of the true marginal function based on 
	 * updates the variable receives.
	 *
	 * Inherits the method evaluate.
	 */
	
	//Update the information the estimation has.
	public void updateEstimate(DiscreteMarginalValues<MultiballVariableState> observedValues);

	/*
	 * Gives the state in the variable domain which should be replaced by a new one at the 
	 * best evaluated point since the last update.
	 * If the evaluate method has not returned a suitably worthy value, then this should return null.
	 */
	public MultiballVariableState stateToLose();
	
	// Gives the divisions to be used by the GoldenLineSearch search method.
	public double[] getSearchDivisions();

	public TrueMarginalFunctionEstimation copy();
	
}
