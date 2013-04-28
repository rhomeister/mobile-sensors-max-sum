package maxSumController.discrete;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import maxSumController.DiscreteVariableState;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class VariableJointState implements DiscreteVariableState {

	protected Map<DiscreteVariable<?>, DiscreteVariableState> variableJointStates;

	private Integer hashCode = null;

	public VariableJointState(
			Map<? extends DiscreteVariable<?>, ? extends DiscreteVariableState> state) {
		variableJointStates = new HashMap<DiscreteVariable<?>, DiscreteVariableState>();
		variableJointStates.putAll(state);
	}

	public VariableJointState(VariableJointState jointState1,
			VariableJointState jointState2) {
		Map<DiscreteVariable<?>, DiscreteVariableState> variableJointStates1 = jointState1
				.getVariableJointStates();
		Map<DiscreteVariable<?>, DiscreteVariableState> variableJointStates2 = jointState2
				.getVariableJointStates();

		variableJointStates = new HashMap<DiscreteVariable<?>, DiscreteVariableState>();
		variableJointStates.putAll(variableJointStates1);
		variableJointStates.putAll(variableJointStates2);
	}

	public VariableJointState(VariableJointState state,
			DiscreteVariable<?> variable, DiscreteVariableState move) {

		Map<DiscreteVariable<?>, DiscreteVariableState> variableJointStates1 = state
				.getVariableJointStates();

		variableJointStates = new HashMap<DiscreteVariable<?>, DiscreteVariableState>();
		variableJointStates.putAll(variableJointStates1);
		variableJointStates.put(variable, move);
	}

	/**
	 * copy constructor
	 * 
	 * @param state
	 */
	public VariableJointState(VariableJointState state) {
		variableJointStates = new HashMap<DiscreteVariable<?>, DiscreteVariableState>();
		variableJointStates.putAll(state.getVariableJointStates());
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder();
		for (DiscreteVariable<?> variable : variableJointStates.keySet()) {
			hcb.append(variable);
			hcb.append(variableJointStates.get(variable));
		}
		hashCode = hcb.toHashCode();

		return hashCode;
	}

	public <T extends DiscreteVariableState> T get(DiscreteVariable<T> key) {
		return (T) variableJointStates.get(key);
	}

	public Set<? extends DiscreteVariable<?>> getVariables() {
		return variableJointStates.keySet();
	}

	public Map<DiscreteVariable<?>, DiscreteVariableState> getVariableJointStates() {
		return variableJointStates;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof VariableJointState) {
			VariableJointState vstate = (VariableJointState) obj;
			return variableJointStates.equals(vstate.variableJointStates);
		}
		return false;
	}

	@Override
	public String toString() {
		return variableJointStates.toString();
	}

}