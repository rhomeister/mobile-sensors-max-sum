package maxSumController;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.JointStateIterator;
import maxSumController.discrete.VariableJointState;

public abstract class DiscreteInternalFunction extends AbstractInternalFunction {

	protected Set<DiscreteVariable<?>> discreteVariableDependencies;

	public DiscreteInternalFunction(String name) {
		super(name);
	}

	abstract public double evaluate(VariableJointState state);

	public Set<DiscreteVariable<?>> getDiscreteVariableDependencies() {
		if (discreteVariableDependencies == null) {
			discreteVariableDependencies = new HashSet<DiscreteVariable<?>>();

			for (Variable<?, ?> variable : getVariableDependencies()) {
				discreteVariableDependencies.add((DiscreteVariable) variable);
			}
		}

		return discreteVariableDependencies;
	}

	public DiscreteVariable getVariableDependency(String variableName) {
		return (DiscreteVariable) super.getVariableDependency(variableName);
	}

	public double evaluateRestricted(VariableJointState jointState) {

		Map<DiscreteVariable<?>, DiscreteVariableState> restrictedstateMap = new HashMap<DiscreteVariable<?>, DiscreteVariableState>();

		for (Variable v : getVariableDependencies()) {
			DiscreteVariable<DiscreteVariableState> dv = (DiscreteVariable<DiscreteVariableState>) v;
			if (jointState.getVariableJointStates().keySet().contains(dv)) {
				restrictedstateMap.put(dv, ((DiscreteVariableState) jointState
						.get(dv)));
			}
		}
		VariableJointState resctrictedJointState = new VariableJointState(
				restrictedstateMap);
		return evaluate(resctrictedJointState);
	}

	public Map<VariableJointState, Double> getValues() {
		JointStateIterator iterator = new JointStateIterator(
				getDiscreteVariableDependencies());

		Map<VariableJointState, Double> result = new HashMap<VariableJointState, Double>();

		while (iterator.hasNext()) {
			VariableJointState jointState = iterator.next();
			result.put(jointState, evaluate(jointState));
		}

		return result;

	}

	public String getValuesToString() {
		Map<VariableJointState, Double> values = getValues();

		StringBuffer buffer = new StringBuffer();

		for (VariableJointState jointState : values.keySet()) {
			buffer.append(jointState + ": " + values.get(jointState) + "\n");
		}

		return buffer.toString();
	}

	public void debug() {
		System.out.println(getValuesToString());
	}
}