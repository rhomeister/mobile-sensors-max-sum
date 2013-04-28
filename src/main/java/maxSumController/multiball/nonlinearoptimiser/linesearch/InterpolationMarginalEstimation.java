package maxSumController.multiball.nonlinearoptimiser.linesearch;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import maxSumController.discrete.DiscreteMarginalValues;
import maxSumController.multiball.MultiballVariableState;
import maxSumController.multiball.math.InterpolationFunction;

public class InterpolationMarginalEstimation implements TrueMarginalFunctionEstimation{
	private int repetitionsRequired;
	private InterpolationFunction interpolation;
	private MultiballVariableState worstState;
	private HashMap<MultiballVariableState, LinkedList<Double>> currentDomainHistory;
	private LinkedList<double[]> previouslyDeterminedPoints;
	private double[] interpolationDataPoints;
	private double valueOfTheUnknown;
	private LinkedList<MultiballVariableState> lastConvergences;
	private double offset;
	private int lastKnownReliable;
	
	public InterpolationMarginalEstimation(int repetitionsRequired,
			InterpolationFunction interpolation, double valueOfTheUnknown) {
		this.repetitionsRequired = repetitionsRequired;
		this.interpolation = interpolation;
		worstState = null;
		currentDomainHistory = new HashMap<MultiballVariableState, LinkedList<Double>>();
		previouslyDeterminedPoints = new LinkedList<double[]>();
		this.valueOfTheUnknown = valueOfTheUnknown;
		interpolationDataPoints = null;
		lastConvergences = null;
		offset = 0;
		lastKnownReliable = 0;
	}

	@Override
	public TrueMarginalFunctionEstimation copy() {
		return new InterpolationMarginalEstimation(repetitionsRequired,
				interpolation, valueOfTheUnknown);
	}

	private boolean converged(MultiballVariableState state){
		LinkedList<Double> stateHistory = currentDomainHistory.get(state);
		if(stateHistory.size() < repetitionsRequired) return false;
		Iterator<Double> descendingIterator = stateHistory.descendingIterator();
		double lastVal = stateHistory.getLast().doubleValue();
		for(int i=1; i<repetitionsRequired; i++){
			if(lastVal != descendingIterator.next().doubleValue()) return false;
		}
		return true;
	}
	
	private boolean repeating(MultiballVariableState state){
		LinkedList<Double> stateHistory = currentDomainHistory.get(state);
		
		int maxRefrainSize = stateHistory.size() / repetitionsRequired;

		for(int j=2; j<=maxRefrainSize; j++) {
			boolean rep = true;
			for(int k=1; k<=j; k++) {
				int index = stateHistory.size() - k;
				double val = stateHistory.get(index);
				for(int i=1; i<repetitionsRequired; i++){
					index -= j;
					if(val != stateHistory.get(index)){
						rep = false;
						continue;
					}					
				}
				if(!rep) continue;
			}
			if(rep) return true;
		}
		return false;
	}
	
	@Override
	public MultiballVariableState stateToLose() {
		if(worstState == null) return null;
		if(converged(worstState)) {
			double[] point = new double[2];
			point[0] = worstState.getValue();
			point[1] = currentDomainHistory.get(worstState).getLast().doubleValue();
			previouslyDeterminedPoints.add(point);
		}
		currentDomainHistory.remove(worstState);
		lastKnownReliable = 1;
		return worstState;
	}

	
	private void applyDifference(double d){
		offset += d;
		for(MultiballVariableState state : currentDomainHistory.keySet()){
			LinkedList<Double> history = currentDomainHistory.get(state);
			for(int i= Math.max(0, history.size() - lastKnownReliable); i < history.size(); i++){
				history.set(i, Double.valueOf(history.get(i).doubleValue() + d));
			}
			
		}
	}
	
	
	@Override
	public void updateEstimate(DiscreteMarginalValues<MultiballVariableState> observedValues) {
		LinkedList<MultiballVariableState> convergedStates = new
			LinkedList<MultiballVariableState>();
		LinkedList<MultiballVariableState> repeatingStates = new
			LinkedList<MultiballVariableState>();
		
		if((lastKnownReliable > 0) && (lastKnownReliable < repetitionsRequired))
		{
			worstState = null;
			lastKnownReliable++;
			interpolationDataPoints = null;
			return;
		}
		Set<MultiballVariableState> states = observedValues.getValues().keySet();
		
		for(MultiballVariableState state : states){
			if(currentDomainHistory.get(state) == null) {
				currentDomainHistory.put(state, new LinkedList<Double>());
			}
			currentDomainHistory.get(state).add(observedValues.getValue(state) + offset);
			if(converged(state)) {
				convergedStates.add(state);
			} else if(repeating(state)) {
				repeatingStates.add(state);
			}
		}
		
		if((lastKnownReliable > 0) && (!convergedStates.isEmpty())){
			for(MultiballVariableState state : convergedStates){
				if(!lastConvergences.contains(state)) continue;
				LinkedList<Double> history = currentDomainHistory.get(state);
				int lastHistory = history.size() - 1;
				applyDifference(history.get(lastHistory - lastKnownReliable) -
						history.get(lastHistory));
				lastKnownReliable = 0;
				break;
			}
		}
		

		if(lastKnownReliable > 0) {
			lastKnownReliable++;
		} else {
			lastConvergences = convergedStates;
		}
		
		worstState = null;
		
		if((convergedStates.size() + repeatingStates.size()) < states.size())
		{
			interpolationDataPoints = null;
			return;
		}

		int n = convergedStates.size() +  previouslyDeterminedPoints.size();
		
		double[] stateValues = new double[n];
		double[] functionValues = new double[n];

		double worstStateEvaluation = 0;
		
		int i=0;
		for(MultiballVariableState state : convergedStates){
			stateValues[i] = state.getValue();
			functionValues[i] = observedValues.getValue(state);
			if((worstState == null) || (worstStateEvaluation > functionValues[i])){
				worstState = state;
				worstStateEvaluation = functionValues[i];
			}
			i++;
		}
		
		for(double[] point : previouslyDeterminedPoints){
			stateValues[i] = point[0];
			functionValues[i] = point[1];
			i++;
		}
		
		interpolation.setDataPoints(stateValues, functionValues);
		interpolationDataPoints = stateValues;
		if(!repeatingStates.isEmpty()) 	worstState = repeatingStates.getFirst();
	}

	@Override
	public double evaluate(double x) {
		if(worstState == null) return 0;
		double mindist = Math.abs(interpolationDataPoints[0] - x);
		for(int i=1; i<interpolationDataPoints.length; i++){
			mindist = Math.min(mindist,Math.abs(interpolationDataPoints[i] - x));
		}
		return (valueOfTheUnknown*mindist) + interpolation.evaluate(x);
	}

	@Override
	public double[] getSearchDivisions() {
		return interpolationDataPoints;
	}
	
	
}
