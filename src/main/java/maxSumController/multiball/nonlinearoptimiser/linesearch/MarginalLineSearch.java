package maxSumController.multiball.nonlinearoptimiser.linesearch;

import java.util.Arrays;
import java.util.Set;

import maxSumController.AbstractVariable;
import maxSumController.discrete.DiscreteVariableDomain;
import maxSumController.discrete.DiscreteMarginalValues;
import maxSumController.multiball.DerivativeFunction;
import maxSumController.multiball.MultiballVariableDomainImpl;
import maxSumController.multiball.MultiballVariableState;
import maxSumController.multiball.GP.HybridGPMSMarginalEstimate;
import maxSumController.multiball.math.GoldenLineSearch;
import maxSumController.multiball.nonlinearoptimiser.NonLinearOptimiser;

public class MarginalLineSearch extends
	NonLinearOptimiser {
	
	private TrueMarginalFunctionEstimation marginalEstimation;
	private int searchIterations;
	private double searchThreshold;

	//searchThreshold should be between 0 and 1 and determines the thoroughness of the 
	//GoldenLineSearch.search method used.
	public MarginalLineSearch(TrueMarginalFunctionEstimation marginalEstimation,
			double searchThreshold, int searchIterations) {
		this.marginalEstimation = marginalEstimation;
		reportMarginalFunction = true;
		reportDerivative = false;
		reportSecondDerivative = false;
		this.searchIterations = searchIterations;
		this.searchThreshold = searchThreshold;
	}
	
	@Override
	public void updateStates(AbstractVariable<
			DiscreteVariableDomain<MultiballVariableState>,
			MultiballVariableState> variable,
			DiscreteMarginalValues<MultiballVariableState> marginalFunction,
			DerivativeFunction derivativeFunction,
			DerivativeFunction secondDerivativeFunction) {
	
		marginalEstimation.updateEstimate(marginalFunction);
		
		/* Some debug info
		System.out.println(variable + " uom: " + marginalFunction);
		
		double[] x = marginalEstimation.getSearchDivisions();
		if(x != null){
			Arrays.sort(x);
			System.out.println(variable);
			double[] y = new double[x.length];
			for(int i=0; i<x.length; i++) y[i] =  marginalEstimation.evaluate(x[i]);
			System.out.println();
			for(int i=0; i<x.length; i++) System.out.printf("%.2f ", x[i]);
			System.out.println();
			for(int i=0; i<x.length; i++) System.out.printf("%.2f ", y[i]);
			System.out.println();
		}
		*/

		double newX = GoldenLineSearch.search(marginalEstimation,  marginalEstimation.getSearchDivisions(), 0,
				searchIterations);
		
		MultiballVariableState stateToLose = marginalEstimation.stateToLose();

		if((stateToLose != null)&&((newX*0) == 0)){
			
			MultiballVariableState newState = new MultiballVariableState(newX);
			Set<MultiballVariableState> states = variable.getDomain().getStates();
			
			states.remove(stateToLose);
			states.add(newState);
			
//			System.out.println(stateToLose.getValue() + " -> " + newState.getValue());
	
			variable.setDomain(new MultiballVariableDomainImpl<MultiballVariableState>(states));
		}
		
	}
	
	@Override
	public NonLinearOptimiser copy() {
		return new MarginalLineSearch(marginalEstimation.copy(), searchThreshold, searchIterations);
	}
		
}
