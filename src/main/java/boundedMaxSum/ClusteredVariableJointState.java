package boundedMaxSum;

import java.util.HashMap;
import java.util.Map;

import maxSumController.DiscreteVariableState;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.VariableJointState;

public class ClusteredVariableJointState extends VariableJointState{



	public ClusteredVariableJointState(
			Map<? extends DiscreteVariable<?>, ? extends DiscreteVariableState> clusteredState) {
		super(clusteredState);
		initVariableJointStates();
	}
	
	public ClusteredVariableJointState(VariableJointState jointState1,
			VariableJointState jointState2) {
		super(jointState1, jointState2);
		initVariableJointStates();
	}

	public ClusteredVariableJointState(VariableJointState state) {
		super(state.getVariableJointStates());
		initVariableJointStates();
	}
	
	private void initVariableJointStates(){
		HashMap<DiscreteVariable<?>, DiscreteVariableState> singleVariableJointStates = new HashMap<DiscreteVariable<?>, DiscreteVariableState>();
		for (DiscreteVariable<?> v : variableJointStates.keySet()) {
			if (v instanceof ClusteredVariable<?>) {
				ClusteredVariable<?> cv = (ClusteredVariable<?>) v;
				//System.out.println(map.get(cv));
				VariableJointState value = (VariableJointState) variableJointStates.get(cv);
				singleVariableJointStates.putAll(value.getVariableJointStates());
			} else {
				singleVariableJointStates.put(v, variableJointStates.get(v));
			}
			
		}
		
		variableJointStates = singleVariableJointStates;
		
		
	}
		
}
