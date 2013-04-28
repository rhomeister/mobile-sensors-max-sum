package maxSumController.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import maxSumController.DiscreteVariableState;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.VariableJointState;
import maxSumController.discrete.bb.AbstractBBDiscreteInternalFunction;
import maxSumController.discrete.bb.PartialJointVariableState;

import org.apache.commons.lang.NotImplementedException;

public class ConflictFunction extends AbstractBBDiscreteInternalFunction {

	private ArrayList<DiscreteVariable> variableExpansionOrder;
	public static int calls;

	public ConflictFunction(String name) {
		super(name);
	}

	@Override
	public double evaluate(VariableJointState states) {
		Map<DiscreteVariableState, Integer> variableStateCardinality = new HashMap<DiscreteVariableState, Integer>();

		Map<? extends DiscreteVariable<?>, DiscreteVariableState> variables = states
				.getVariableJointStates();

		for (DiscreteVariable<?> variable : variables.keySet()) {
			DiscreteVariableState variableState = variables.get(variable);

			if (variableStateCardinality.get(variableState) == null)
				variableStateCardinality.put(variableState, 0);

			int currentCardinality = variableStateCardinality
					.get(variableState);
			variableStateCardinality.put(variableState, currentCardinality + 1);
		}
		int conflicts = 0;

		for (DiscreteVariableState state : variableStateCardinality.keySet()) {
			conflicts += Math.max(0, variableStateCardinality.get(state) - 1);
		}

		return -conflicts;
	}

	@Override
	public double getLowerBound(PartialJointVariableState state) {
		calls++;

		if (state.isFullyDetermined()) {
			return -getConflictsCount(state);
		} else {
			return -getConflictsCount(state) - state.getNotDeterminedCount();
		}
	}

	@Override
	public double getLowerBound(DiscreteVariable variable,
			DiscreteVariableState state) {
		throw new NotImplementedException();
	}

	@Override
	public double getUpperBound(DiscreteVariable variable,
			DiscreteVariableState state) {
		throw new NotImplementedException();
	}

	@Override
	public double getUpperBound(PartialJointVariableState state) {
		calls++;

		return -getConflictsCount(state);
	}

	private int getConflictsCount(PartialJointVariableState state) {
		Set<DiscreteVariableState> states = new HashSet<DiscreteVariableState>();

		int conflicts = 0;

		for (DiscreteVariable variable : getDiscreteVariableDependencies()) {
			if (state.isSet(variable) && !states.add(state.getState(variable)))
				conflicts++;
		}
		return conflicts;
	}

	@Override
	public List<DiscreteVariable> getVariableExpansionOrder() {
		if (variableExpansionOrder == null) {
			variableExpansionOrder = new ArrayList<DiscreteVariable>(
					getDiscreteVariableDependencies());
		}

		return variableExpansionOrder;
	}
}
