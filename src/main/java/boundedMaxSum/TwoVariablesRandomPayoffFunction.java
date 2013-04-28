package boundedMaxSum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import maxSumController.DiscreteVariableState;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.VariableJointState;
import maxSumController.io.Color;

import org.apache.commons.lang.Validate;
import org.apache.commons.math.distribution.ChiSquaredDistributionImpl;
import org.apache.commons.math.random.RandomDataImpl;

import cern.jet.random.Gamma;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;

public class TwoVariablesRandomPayoffFunction extends LinkBoundedInternalFunction {

	
	DiscreteVariable<Color> var0;
	DiscreteVariable<Color> var1;
	private static RandomEngine randomGen = new MersenneTwister();

	private static RandomDataImpl dataGen = new RandomDataImpl();
	private static ChiSquaredDistributionImpl chiSquareGen = new ChiSquaredDistributionImpl(0.5);
	private static Gamma gammaGen = new Gamma(9,2,randomGen);
	
	protected Map<VariableJointState, Double> payoffMatrix;
	
	protected double[] weight;
	
	protected double min = Double.MAX_VALUE;
	
	protected double max = Double.MIN_VALUE;
	private boolean debug = false;
	
	
	public void setPayoffMatrix(Map<VariableJointState, Double> payoffMatrix) {
		this.payoffMatrix = payoffMatrix;
		weight = computeWeight();
		max = computeMax();
		min = computeMin();
	}
		
	public Map<VariableJointState, Double> getPayoffMatrix() {
		return payoffMatrix;
	}
	
//	@Override
//	public void updateVariableDependency(java.util.Set<maxSumController.discrete.DiscreteInternalVariable<?>> updateVariables) {
//		
//		DiscreteVariable<Color> copyVar0 = null;
//		DiscreteVariable<Color> copyVar1 = null;
//		
//		Map<VariableJointState, Double> payoffCopy = payoffMatrix;
//		
//		payoffMatrix = new HashMap<VariableJointState, Double>();
//		
//		for (DiscreteInternalVariable<?> div : updateVariables) {
//			if (div.getName().equals(var0.getName())){
//				copyVar0 = (DiscreteVariable<Color>) div;
//			}
//			if (div.getName().equals(var1.getName())){
//				copyVar1 = (DiscreteVariable<Color>) div;
//			}
//		}
//		
//		for (Color varState0 : copyVar0.getDomain().getStates()) {
//			for (Color varState1 : copyVar1.getDomain().getStates()) {
//				Map<DiscreteVariable<?>, DiscreteVariableState> currentJointStateMap = new HashMap<DiscreteVariable<?>, DiscreteVariableState>();
//				Map<DiscreteVariable<?>, DiscreteVariableState> jointStateMap = new HashMap<DiscreteVariable<?>, DiscreteVariableState>();
//				currentJointStateMap.put(copyVar0, varState0);
//				currentJointStateMap.put(copyVar1, varState1);
//				jointStateMap.put(var0, varState0);
//				jointStateMap.put(var1, varState1);
//				
//				VariableJointState currentJointState = new VariableJointState(currentJointStateMap);
//				VariableJointState jointState = new VariableJointState(jointStateMap);
//				
//				payoffMatrix.put(currentJointState, payoffCopy.get(jointState));
//				
//			}
//		}	
//		
//	}
	
	public TwoVariablesRandomPayoffFunction(String name, DiscreteVariable<Color> pvar1, DiscreteVariable<Color> pvar2) {
		super(name);
		var0 = pvar1;
		var1 = pvar2;
		payoffMatrix = new HashMap<VariableJointState, Double>();
		for (Color varState0 : var0.getDomain().getStates()) {
			for (Color varState1 : var1.getDomain().getStates()) {
				Map<DiscreteVariable<?>, DiscreteVariableState> currentJointStateMap = new HashMap<DiscreteVariable<?>, DiscreteVariableState>();
				Map<DiscreteVariable<?>, DiscreteVariableState> reverseJointStateMap = new HashMap<DiscreteVariable<?>, DiscreteVariableState>();
				currentJointStateMap.put(var0, varState0);
				currentJointStateMap.put(var1, varState1);
				
				reverseJointStateMap.put(var1, varState1);
				reverseJointStateMap.put(var0, varState0);
				
				
				
				VariableJointState currentJointState = new VariableJointState(currentJointStateMap);				
				VariableJointState reverseJointState = new VariableJointState(reverseJointStateMap);				
				
//				Double currentJointStateValue = Math.random();
/*				Double currentJointStateValue = 0.;
				try {
					currentJointStateValue = chiSquareGen.inverseCumulativeProbability(dataGen.nextUniform(0., 1.));
				} catch (MathException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
*/
				Double currentJointStateValue = gammaGen.nextDouble();	
				payoffMatrix.put(currentJointState, currentJointStateValue);
				payoffMatrix.put(reverseJointState, currentJointStateValue);				
			}			
		}
		
		if (debug ) {
			System.out.println(payoffMatrix);
		}
		
		min = computeMin();
		
		max = computeMax();
		
		weight = computeWeight();
		
	}

	private double computeMax() {
		double newmax = Double.MIN_VALUE;
		for (VariableJointState jstate : payoffMatrix.keySet()) {
			if (newmax < payoffMatrix.get(jstate)){
				newmax = payoffMatrix.get(jstate);
			}
		}
		if (debug) {
			System.out.println("newmax " + newmax);
		}
		return newmax;
	}

	private double computeMin() {
		double newmin = Double.MAX_VALUE;
		for (VariableJointState jstate : payoffMatrix.keySet()) {
			if (newmin>payoffMatrix.get(jstate)){
				newmin = payoffMatrix.get(jstate);
			}
		}
		if (debug) {
			System.out.println("newmin " + newmin);
		}
		return newmin;
	}

	private double[] computeWeight() {
		double[] res = new double[2];
		double maxdiffV0 = -Double.MAX_VALUE;
		double maxdiffV1 = -Double.MAX_VALUE;
		
		
		for (Color varState1 : var1.getDomain().getStates()) {	
			double currDiff = ((Double)Collections.max(getArray(var1, varState1))) - ((Double) Collections.min(getArray(var1, varState1)));
			if (maxdiffV0 < currDiff){
				maxdiffV0 = currDiff;
			}		
		}
		
		
		for (Color varState0 : var0.getDomain().getStates()) {	
			double currDiff = ((Double)Collections.max(getArray(var0,varState0))) - ((Double) Collections.min(getArray(var0,varState0)));
			if (maxdiffV1 < currDiff){
				maxdiffV1 = currDiff;
			}		
		}
		
		res[0] = maxdiffV0;
		res[1] = maxdiffV1;
		
		return res;
	}

	
	public double getWeight(String varName) {
		return ((varName.equals(var0.getName())?weight[0]:weight[1]));
	}
	
	public  double[] getWeights() {
		return weight; 
	}
	
	private ArrayList getArray(DiscreteVariable<Color> var, Color val) {
		ArrayList<Double> res = new ArrayList<Double>();

		DiscreteVariable<Color> otherVariable = getOtherVariable(var.getName());
		for (Color ovs : otherVariable.getDomain().getStates()) {
			
			Map<DiscreteVariable<?>, DiscreteVariableState> currentJointStateMap = new HashMap<DiscreteVariable<?>, DiscreteVariableState>();
			currentJointStateMap.put(var, val);
			currentJointStateMap.put(otherVariable, ovs);
			VariableJointState currentJointState = new VariableJointState(currentJointStateMap);				

			VariableJointState currentJointState1 = new VariableJointState(currentJointStateMap);				

			Validate.notNull(payoffMatrix.get(currentJointState));
						
			res.add(payoffMatrix.get(currentJointState));			
			
		}		
		
		return res;
	}

	private DiscreteVariable<Color> getOtherVariable(String varName) {
		return (varName.equals(var0.getName()))? var1: var0;
	}

	@Override
	public LinkBoundedInternalFunction getNewFunction(String deletedVariableName) {
		DiscreteVariable<Color> varToKeep = getOtherVariable(deletedVariableName);
		DiscreteVariable<Color> varToCut = getOtherVariable(varToKeep.getName());
		SingleVariablesRandomPayoffFunction newFunction = new SingleVariablesRandomPayoffFunction(getName()+"m",varToKeep);
		Map<VariableJointState, Double> singlePayoff = new HashMap<VariableJointState, Double>();
		
		for (Color color : varToKeep.getDomain().getStates()) {
			Color argMin = argMin(color, varToCut, varToKeep);
			Map<DiscreteVariable<?>, DiscreteVariableState> currentJointStateMap = new HashMap<DiscreteVariable<?>, DiscreteVariableState>();
			currentJointStateMap.put(varToKeep, color);
			currentJointStateMap.put(varToCut, argMin);			
			VariableJointState currentJointState = new VariableJointState(currentJointStateMap);				
			Double value = payoffMatrix.get(currentJointState);
			
			Map<DiscreteVariable<?>, DiscreteVariableState> currentStateMap = new HashMap<DiscreteVariable<?>, DiscreteVariableState>();
			currentStateMap.put(varToKeep, color);
			VariableJointState currentState = new VariableJointState(currentStateMap);				
			singlePayoff.put(currentState, value);	
		}
		
		newFunction.setPayoffMatrix(singlePayoff);
		return newFunction;
	}


	protected Color argMin(Color color, DiscreteVariable<Color> varToCut, DiscreteVariable<?> varToKeep) {
		Color minColor = null;
		double minValue = Double.MAX_VALUE;
		for (Color otherColor : varToCut.getDomain().getStates()) {
			Map<DiscreteVariable<?>, DiscreteVariableState> currentJointStateMap = new HashMap<DiscreteVariable<?>, DiscreteVariableState>();
			currentJointStateMap.put(varToKeep, color);
			currentJointStateMap.put(varToCut, otherColor);			
			VariableJointState currentJointState = new VariableJointState(currentJointStateMap);				
			Double value = payoffMatrix.get(currentJointState);
			
			if (value < minValue){
				minValue = value;
				minColor = otherColor;
			}
			
		}
		return minColor;
	}

	@Override
	public double evaluate(VariableJointState state) {
		Map<DiscreteVariable<?>, DiscreteVariableState> newMap = new HashMap<DiscreteVariable<?>, DiscreteVariableState>();
		Map<DiscreteVariable<?>, DiscreteVariableState> map = state.getVariableJointStates();
		for (DiscreteVariable<?> v : map.keySet()) {
			if (v instanceof ClusteredVariable<?>) {
				ClusteredVariable<?> cv = (ClusteredVariable<?>) v;
				System.out.println(map.get(cv));
				VariableJointState value = (VariableJointState) map.get(cv);
				newMap.putAll(value.getVariableJointStates());
			} else {
				newMap.put(v, map.get(v));
			}
			
		}
		VariableJointState newstate = new VariableJointState(newMap);
		return payoffMatrix.get(newstate);
	}


	@Override
	public double getMaximumBound() {
		return max;
	}

	@Override
	public double getMinimumBound() {
		return min;
	}

	@Override
	public LinkBoundedInternalFunction getNewFunction(
			Set<DiscreteInternalVariable> rejectedVariables) {
		Validate.isTrue(rejectedVariables.size() == 1);
		for (DiscreteInternalVariable discreteInternalVariable : rejectedVariables) {
			return getNewFunction(discreteInternalVariable.getName());
		}
		return null;
	}

	@Override
	public double getBound(Set<DiscreteInternalVariable> rejectedVariables) {
		Validate.isTrue(rejectedVariables.size()==1);
		return getWeight(((DiscreteInternalVariable) rejectedVariables.toArray()[0]).getName());
	}

	@Override
	public LinkBoundedInternalFunction clone() {
		TwoVariablesRandomPayoffFunction ret = new TwoVariablesRandomPayoffFunction(getName(),var0,var1);
		ret.setPayoffMatrix(payoffMatrix);
		return ret;
	}
	
}
