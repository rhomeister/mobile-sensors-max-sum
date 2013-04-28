package maxSumController.discrete.prune;

import java.util.Map;
import java.util.Set;

import maxSumController.DiscreteVariableState;
import maxSumController.continuous.linear.Interval;

public class FunctionToVariablePruneMessageContents implements
		PruneMessageContents {

	private Map<DiscreteVariableState, Interval> bounds;

	public FunctionToVariablePruneMessageContents(
			Map<DiscreteVariableState, Interval> bounds) {
		this.bounds = bounds;
	}

	public double getUpperBound(DiscreteVariableState state) {
		return bounds.get(state).getUpperbound();
	}

	public double getLowerBound(DiscreteVariableState state) {
		return bounds.get(state).getLowerbound();
	}

	public Set<DiscreteVariableState> getVariableStates() {
		return bounds.keySet();
	}

	@Override
	public String toString() {
		return bounds.toString();
	}
}
