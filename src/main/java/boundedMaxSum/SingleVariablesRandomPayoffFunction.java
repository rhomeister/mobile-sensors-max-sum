package boundedMaxSum;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;

import maxSumController.DiscreteVariableState;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.VariableJointState;
import maxSumController.io.Color;

public class SingleVariablesRandomPayoffFunction extends LinkBoundedInternalFunction{

	DiscreteVariable<Color> var;
	private Map<VariableJointState, Double> payoffMatrix;
	private double min;
	private double max;
	private double weight;
	
	public Map<VariableJointState, Double> getPayoffMatrix() {
		return payoffMatrix;
	}
	
	public SingleVariablesRandomPayoffFunction(String name, DiscreteVariable<Color> pvar) {
		super(name);
		var = pvar;
		payoffMatrix = new HashMap<VariableJointState, Double>();
		for (Color varState0 : var.getDomain().getStates()) {
				Map<DiscreteVariable<?>, DiscreteVariableState> currentJointStateMap = new HashMap<DiscreteVariable<?>, DiscreteVariableState>();
				currentJointStateMap.put(var, varState0);								
				VariableJointState currentJointState = new VariableJointState(currentJointStateMap);				
				Double currentJointStateValue = Math.random();
				payoffMatrix.put(currentJointState, currentJointStateValue);
		}
				
		min = computeMin();
		
		max = computeMax();
		
		weight = computeWeight();

	}
	
	private double computeWeight() {
		return (max - min);
	}

	private double computeMax() {
		return Collections.max(payoffMatrix.values());
	}

	private double computeMin() {
		return Collections.min(payoffMatrix.values());
	}

	@Override
	public LinkBoundedInternalFunction getNewFunction(String deletedVariable) {
		return null;
	}

	@Override
	public double getWeight(String deletedVariableName) {
		return weight;
	}

	@Override
	public double evaluate(VariableJointState state) {
		return payoffMatrix.get(state);
	}

	public void setPayoffMatrix(Map<VariableJointState, Double> singlePayoff) {
		payoffMatrix = singlePayoff;
		min = computeMin();
		
		max = computeMax();
		
		weight = computeWeight();
		
	}

	@Override
	public double getMaximumBound() {
		// TODO Auto-generated method stub
		return max;
	}

	@Override
	public double getMinimumBound() {
		// TODO Auto-generated method stub
		return min;
	}

	@Override
	public LinkBoundedInternalFunction getNewFunction(
			Set<DiscreteInternalVariable> rejectedVariables) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getBound(Set<DiscreteInternalVariable> rejectedVariables) {
		Validate.isTrue(rejectedVariables.size()==1);
		return weight;
	}

	@Override
	public LinkBoundedInternalFunction clone() {
		SingleVariablesRandomPayoffFunction res = new SingleVariablesRandomPayoffFunction(getName(),var);
		res.setPayoffMatrix(payoffMatrix);
		return res;	
	}

}
