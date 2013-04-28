package maxSumController.multiball;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import maxSumController.DiscreteInternalFunction;
import maxSumController.InternalFunction;
import maxSumController.MarginalMaximisationFactory;
import maxSumController.MarginalValues;
import maxSumController.Variable;
import maxSumController.VariableState;
import maxSumController.discrete.DiscreteMarginalMaximisation;
import maxSumController.discrete.DiscreteMarginalValues;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.VariableJointState;
import maxSumController.discrete.bb.AbstractDiscreteMarginalMaximisation;

public class MultiballMarginalMaximisation extends
		AbstractDiscreteMarginalMaximisation implements
		MarginalMaximisationFactory {
/*
 * When calculateMarginalMaxFunction is called it will check the reference of the messages
 * passed to it and if the reference is new, it will calculate the marginal max function for
 * all variables and store them in an IdentityHashMap. Otherwise it will use the functions
 * it stored previously.
 */

	private Map<Variable<?,?>, MarginalValues<?>> messages = null;
	
	private DiscreteInternalFunction function = null;

	private Set<DiscreteVariable<MultiballVariableState>> variables = null;
	
	private HashMap<DiscreteVariable<MultiballVariableState>, DiscreteMarginalValues<MultiballVariableState>> marginalMaxFunctions;

	private HashMap<DiscreteVariable<MultiballVariableState>, HashMap<MultiballVariableState,
		VariableJointState>> bestVariableJointState;

	
	@SuppressWarnings("unchecked")
	private void calculateEverything(){
		/*
		 * 1) create an array made of the variables
		 * 2) create a double array of states with an entry for each variable state
		 * 3) go through messages and get the marginal functions
		 *    as a double array corresponding to the double array of states
		 * 4) go through each joint state, calculate the function at each point
		 *    add onto it the marginal contribution for each state as appropriate
		 *    keep two double arrays (one of doubles, one of joint states) recording 
		 *    best joint state and highest value achieved
		 * 5) create marginalMaxFunctions and bestJointStateInfo
		 */
		
		
		Object[] variableArray = variables.toArray();
		
		int numVar = variableArray.length;

		Object[][] stateArray = new Object[numVar][];
		double[][] marginalValuesArray = new double[numVar][];

		int[] jointStateIndex = new int[numVar];
		int[] numStates = new int[numVar];

		for(int i=0; i<numVar; i++){
			DiscreteVariable<MultiballVariableState> v = (DiscreteVariable<MultiballVariableState>) 
				variableArray[i];
			stateArray[i] = v.getDomain().getStates().toArray();
			numStates[i] = stateArray[i].length;
			DiscreteMarginalValues<MultiballVariableState> marginalValues = 
				(DiscreteMarginalValues<MultiballVariableState>) messages.get(v);
			if (marginalValues == null) {
				marginalValuesArray[i] = new double[numStates[i]];
				for(int j=0; j<numStates[i]; j++) marginalValuesArray[i][j] = 0;
			} else {
				Map<MultiballVariableState, Double> marginalValuesMap = marginalValues.getValues();	
				marginalValuesArray[i] = new double[numStates[i]];
				for(int j=0; j<numStates[i]; j++) marginalValuesArray[i][j] =
					marginalValuesMap.get((MultiballVariableState) stateArray[i][j]).doubleValue();				
			}
		}
		
		double[][] marginalMax = new double[numVar][];
		
		int[][][] bestJointState = new int[numVar][][];
		
		
		for(int i=0; i < numVar; i++) {
			jointStateIndex[i] = 0;
			marginalMax[i] = new double[numStates[i]];
			bestJointState[i] = new int[numStates[i]][numVar];
			for(int j=0; j<numStates[i]; j++) marginalMax[i][j] = Double.NEGATIVE_INFINITY;
		}
		
		boolean notDone = true;
		Map<DiscreteVariable<MultiballVariableState>, MultiballVariableState> state
			= new HashMap<DiscreteVariable<MultiballVariableState>, MultiballVariableState>() ;
		
		for(; notDone; notDone = incrementIndex(jointStateIndex, numStates)) {
			for(int i=0; i<numVar; i++) state.put((DiscreteVariable<MultiballVariableState>)variableArray[i], 
					(MultiballVariableState) stateArray[i][jointStateIndex[i]]);
			double Val = function.evaluate(new VariableJointState(state));
			
			double[] currentMarginalValues = new double[numVar];
			for(int i=0; i<numVar; i++) currentMarginalValues[i] = marginalValuesArray[i][jointStateIndex[i]];
				
			for(int i=0; i<numVar; i++) Val += currentMarginalValues[i];
			
			for(int i=0; i<numVar; i++) {
				Val -= currentMarginalValues[i];				
				if(Val > marginalMax[i][jointStateIndex[i]]) {
					marginalMax[i][jointStateIndex[i]] = Val;
					int[] bJS = bestJointState[i][jointStateIndex[i]];
					for(int j=0; j<numVar; j++) bJS[j] = jointStateIndex[j];
				}
				Val += currentMarginalValues[i];
			}				
		}
		
		
		marginalMaxFunctions = new HashMap<DiscreteVariable<MultiballVariableState>, 
			DiscreteMarginalValues<MultiballVariableState>>();
		for(int i=0; i<numVar; i++){
			Map<MultiballVariableState, Double> mVM = new HashMap<MultiballVariableState, Double>();
			for(int j=0; j<numStates[i]; j++){
				mVM.put((MultiballVariableState) stateArray[i][j], new Double(marginalMax[i][j]));
			}
			marginalMaxFunctions.put((DiscreteVariable<MultiballVariableState>) variableArray[i], 
					new DiscreteMarginalValues(mVM));
		}		

		bestVariableJointState = new HashMap<DiscreteVariable<MultiballVariableState>, 
			HashMap<MultiballVariableState, VariableJointState>>();
		for(int i=0; i<numVar; i++) bestVariableJointState.put(
				(DiscreteVariable<MultiballVariableState>) variableArray[i],
				new HashMap<MultiballVariableState, VariableJointState>());
		
		for(int i=0; i<numVar; i++){
			for(int j=0; j<numStates[i]; j++){
				int[] bJS = bestJointState[i][j];
				Map<DiscreteVariable<MultiballVariableState>, MultiballVariableState> bJSMAP = 
					new HashMap<DiscreteVariable<MultiballVariableState>, MultiballVariableState>();
				
				for(int k=0; k<numVar; k++) {
					bJSMAP.put((DiscreteVariable<MultiballVariableState>) variableArray[k], 
							(MultiballVariableState) stateArray[k][bJS[k]]);
				}
				bestVariableJointState.get((DiscreteVariable<MultiballVariableState>) variableArray[i])
						.put((MultiballVariableState) stateArray[i][j], 
						new VariableJointState(bJSMAP));
			}
		}
		
		/*
		double b = Double.NEGATIVE_INFINITY;
		int bI = 0;
		for(int i=0; i<numStates[0]; i++) {
			if(marginalMax[0][i] > b) {
				b = marginalMax[0][i] + marginalValuesArray[0][i];
				bI = i;
			}
		}
		System.out.println(bestVariableJointState.get((DiscreteVariable<MultiballVariableState>) variableArray[0])
						.get((MultiballVariableState) stateArray[0][bI])); */
	}
	
	private boolean incrementIndex(int[] index, int[] arrayLengths) {
		for(int i=0; i<index.length; i++) {
			index[i]++;
			if(index[i] < arrayLengths[i]) return true;
			index[i] = 0;			
		}		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public MarginalValues<?> calculateMarginalMaxFunction(
			Map<Variable<?, ?>, MarginalValues<?>> sortedMessages,
			Variable<?, ?> variable) {
		if (sortedMessages != messages) {
			messages = sortedMessages;
			calculateEverything();
		}
		return marginalMaxFunctions.get((DiscreteVariable<MultiballVariableState>) variable);
	}


	public void setFunction(InternalFunction function) {
		if (this.function == null || !this.function.equals(function)) {
			messages = null;
		}
		this.function = (DiscreteInternalFunction) function;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setVariables(Set<? extends Variable<?, ?>> variablesUncast) {
		Set<DiscreteVariable<MultiballVariableState>> variables = 
			(Set<DiscreteVariable<MultiballVariableState>>) variablesUncast;
		if (this.variables == null || !this.variables.equals(variables)) {
			messages = null;
		}

		this.variables = new HashSet<DiscreteVariable<MultiballVariableState>>(variables);
	}

	public DiscreteMarginalMaximisation create() {
		return new MultiballMarginalMaximisation();
	}

	@SuppressWarnings("unchecked")
	@Override
	public VariableState getBestState(Variable<?, ?> fixedVariable,
			VariableState fixedState, Variable<?, ?> targetVariable) {
		return (VariableState) getBestState(fixedVariable, fixedState).get(
				(DiscreteVariable<MultiballVariableState>) targetVariable);
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public VariableJointState getBestState(Variable<?, ?> fixedVariable,
			VariableState fixedState) {
		VariableJointState result = bestVariableJointState.get(
				(DiscreteVariable<MultiballVariableState>) fixedVariable).get(
				(MultiballVariableState) fixedState);
		
		return result;
	}

	
	@Override
	protected DiscreteInternalFunction getFunction() {
		return function;
	}
}
