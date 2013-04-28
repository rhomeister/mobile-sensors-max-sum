package boundedMaxSum;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import maxSumController.DiscreteInternalFunction;
import maxSumController.DiscreteVariableState;
import maxSumController.Variable;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.VariableJointState;

public abstract class ClusterDiscreteFunctionImpl extends DiscreteInternalFunction implements ClusterDiscreteFunction {

	
	
	private Set<Variable<?, ?>> originalDependencies = null;


	public ClusterDiscreteFunctionImpl(String name) {
		super(name);
	}

	public void addClusterVariableDependency(ClusteredVariable<? extends VariableJointState> cv){
		
		//removing dependencies from variable which comprise the cluster variable
		for (Variable v : cv.variables) {
			removeVariableDependency(v.getName());
		}
		
		//adding the dependency for the cluster Variable
		addVariableDependency(cv);
		
	}

	public void setOriginalDependencies(Set<Variable<?, ?>> variableDependencies) {
		originalDependencies = variableDependencies;
	}	

	
	@Override
	public double evaluateRestricted(VariableJointState jointState) {
//		Map<DiscreteVariable<?>, DiscreteVariableState> newMap = new HashMap<DiscreteVariable<?>, DiscreteVariableState>();
//		Map<DiscreteVariable<?>, DiscreteVariableState> map = jointState.getVariableJointStates();
//		for (DiscreteVariable<?> v : map.keySet()) {
//			if (v instanceof ClusteredVariable<?>) {
//				ClusteredVariable<?> cv = (ClusteredVariable<?>) v;
//				//System.out.println(map.get(cv));
//				VariableJointState value = (VariableJointState) map.get(cv);
//				newMap.putAll(value.getVariableJointStates());
//			} else {
//				newMap.put(v, map.get(v));
//			}
//			
//		}
//		VariableJointState newstate = new VariableJointState(newMap);

//		Map<DiscreteVariable<?>, DiscreteVariableState> restrictedstateMap = new HashMap<DiscreteVariable<?>, DiscreteVariableState>();
	
		ClusteredVariableJointState newstate = new ClusteredVariableJointState(jointState);
		
		Map<DiscreteVariable<?>, DiscreteVariableState> restrictedstateMap = new HashMap<DiscreteVariable<?>, DiscreteVariableState>();
		
				
		for (Variable v : originalDependencies ) {
			DiscreteVariable<DiscreteVariableState> dv = (DiscreteVariable<DiscreteVariableState>) v;
			if (newstate.getVariableJointStates().keySet().contains(dv)){
				restrictedstateMap.put(dv, ((DiscreteVariableState)newstate.get(dv)));
			}
		}
		VariableJointState resctrictedJointState = new VariableJointState(restrictedstateMap);
		return evaluate(resctrictedJointState);
	
	}


	
	
}
