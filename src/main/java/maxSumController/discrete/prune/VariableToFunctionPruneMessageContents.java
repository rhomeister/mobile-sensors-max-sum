package maxSumController.discrete.prune;

import java.util.Set;

import maxSumController.DiscreteVariableState;

public class VariableToFunctionPruneMessageContents implements
		PruneMessageContents {

	private Set<? extends DiscreteVariableState> states;

	public VariableToFunctionPruneMessageContents(
			Set<? extends DiscreteVariableState> states) {
		this.states = states;
	}

	public Set<? extends DiscreteVariableState> getStates() {
		return states;
	}
	
	@Override
	public String toString() {
		return states.toString();
	}
}
