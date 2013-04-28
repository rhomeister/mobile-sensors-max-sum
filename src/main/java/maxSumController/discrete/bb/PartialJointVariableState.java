package maxSumController.discrete.bb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import maxSumController.DiscreteVariableState;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.VariableJointState;

import org.apache.commons.lang.Validate;

import branchNbound.State;

public class PartialJointVariableState<T extends DiscreteVariableState>
		implements State {

	private List<DiscreteVariable<T>> variables = new ArrayList<DiscreteVariable<T>>();

	private DiscreteVariable<T> lastSetVariable;

	private final static int NOT_SET = -1;

	private int[] variableStates;

	// indexed by variable number and variable state
	private List<T>[] variableStateIndices;

	// private int currentVariableIndex = 0;

	// private int rootVariableIndex;

	private int indexCode = -1;

	private Object partialSolution;

	private PartialJointVariableState<T> parent;

	public PartialJointVariableState(List<DiscreteVariable<T>> variables,
			DiscreteVariable<T> rootVariable, T firstVariableState) {
		this(variables);

		setVariable(rootVariable, firstVariableState);
	}

	protected PartialJointVariableState(List<DiscreteVariable<T>> variables) {
		this.variables = variables;

		variableStates = new int[variables.size()];
		variableStateIndices = new List[variables.size()];
		Arrays.fill(variableStates, NOT_SET);

		int variableIndex = 0;

		for (DiscreteVariable<T> variable : variables) {
			variableStateIndices[variableIndex] = new ArrayList<T>(variable
					.getDomain().getStates());
			variableIndex++;
		}
	}

	protected void setVariable(DiscreteVariable<T> variable,
			T firstVariableState) {
		int variableIndex = variables.indexOf(variable);
		Validate.isTrue(variableIndex != -1,
				"Variable is not part of this partial joint state");

		int stateIndex = variableStateIndices[variableIndex]
				.indexOf(firstVariableState);

		Validate.isTrue(stateIndex != -1, "Variable state not found");

		variableStates[variableIndex] = stateIndex;

		// if (variableIndex == 0) {
		// currentVariableIndex++;
		// }

		lastSetVariable = variable;
	}

	private PartialJointVariableState(List<DiscreteVariable<T>> variables,
			List<T>[] variableStates, int[] currentState,
			PartialJointVariableState<T> parent,
			DiscreteVariable<T> lastSetVariable) {
		this.variables = variables;
		this.variableStateIndices = variableStates;
		this.variableStates = currentState;
		this.parent = parent;
		this.lastSetVariable = lastSetVariable;
	}

	public static PartialJointVariableState combineStates(
			List<DiscreteVariable> variableExpansionOrder,
			PartialJointVariableState<?> partialState, VariableJointState next) {

		PartialJointVariableState combinedState = new PartialJointVariableState(
				variableExpansionOrder);

		for (DiscreteVariable<?> variable : next.getVariables()) {
			combinedState.setVariable(variable, next.get(variable));
		}

		for (DiscreteVariable variable : partialState.getVariables()) {
			if (partialState.isSet(variable)) {
				combinedState.setVariable(variable, partialState
						.getState(variable));
			}
		}

		return combinedState;
	}

	/**
	 * Creates a PartialJointVariableState that corresponds to the fully
	 * determined VariableJointState. The resulting partial state is therefore
	 * also fully determined.
	 * 
	 * @param state
	 * @return
	 */
	public static PartialJointVariableState createPartialJointVariableState(
			List<DiscreteVariable> variableExpansionOrder,
			VariableJointState state) {
		PartialJointVariableState partialState = new PartialJointVariableState(
				variableExpansionOrder);

		if (variableExpansionOrder.size() != state.getVariables().size()) {
			throw new IllegalArgumentException("Should be equal size "
					+ variableExpansionOrder + " " + state);
		}

		for (DiscreteVariable variable : variableExpansionOrder) {
			partialState.setVariable(variable, state.get(variable));
		}

		if (!partialState.isFullyDetermined()) {
			throw new IllegalArgumentException("Should be equal size "
					+ variableExpansionOrder + " " + state);
		}

		return partialState;
	}

	public T getState(DiscreteVariable<T> variable) {
		int varIndex = variables.indexOf(variable);
		int stateIndex = variableStates[varIndex];

		if (stateIndex == NOT_SET)
			return null;

		return variableStateIndices[varIndex].get(stateIndex);
	}

	public boolean isSet(DiscreteVariable<T> variable) {
		int index = variables.indexOf(variable);

		if (index == -1) {
			System.err.println(variables + " " + variable);

			throw new IllegalArgumentException(
					"Variable is not part of this partial joint variable state. "
							+ "If you are using BoundedMaxSum, check the implementation of the clone() method. "
							+ "Make sure the owningAgentIdentifier is copied correctly.");
		}

		return variableStates[index] != NOT_SET;
	}

	public int size() {
		return variableStates.length;
	}

	public Collection<PartialJointVariableState<T>> getChildren() {
		Validate.isTrue(!isFullyDetermined());

		int firstFreeVariableIndex = getFirstFreeVariableIndex();
		int stateCount = variableStateIndices[firstFreeVariableIndex].size();
		List<PartialJointVariableState<T>> result = new ArrayList<PartialJointVariableState<T>>();

		for (int i = 0; i < stateCount; i++) {
			int size = variables.size();
			int[] currentState = new int[size];

			System.arraycopy(variableStates, 0, currentState, 0, size);

			currentState[firstFreeVariableIndex] = i;

			result.add(new PartialJointVariableState<T>(variables,
					variableStateIndices, currentState, this, variables
							.get(firstFreeVariableIndex)));
		}

		return result;
	}

	/**
	 * Returns the index of the first state that has not been set (i.e. is equal
	 * to NOT_SET)
	 * 
	 * @return
	 */
	private int getFirstFreeVariableIndex() {
		for (int i = 0; i < variableStates.length; i++) {
			if (variableStates[i] == NOT_SET)
				return i;
		}

		return -1;
	}

	public boolean isFullyDetermined() {
		for (int i = 0; i < variableStates.length; i++) {
			if (variableStates[i] == NOT_SET)
				return false;
		}

		return true;
	}

	public VariableJointState getJointState() {
		Map<DiscreteVariable<?>, DiscreteVariableState> state = new HashMap<DiscreteVariable<?>, DiscreteVariableState>();

		for (DiscreteVariable<T> variable : variables) {
			state.put(variable, getState(variable));
		}

		return new VariableJointState(state);
	}

	@Override
	public String toString() {
		String result = "{";
		for (DiscreteVariable<T> variable : variables) {
			result += variable + ": " + getState(variable) + ", ";
		}

		result = result.substring(0, result.length() - 2);
		result += "}";

		return result;
	}

	public int getNotDeterminedCount() {
		return variables.size() - getDeterminedCount();
	}

	public int getDeterminedCount() {
		int count = 0;

		for (int i = 0; i < variableStates.length; i++) {
			if (variableStates[i] != NOT_SET)
				count++;
		}

		return count;
	}

	/**
	 * Code that uniquely identifies this partial state. Used in caching for
	 * quickly looking up its corresponding upper and lower bounds
	 * 
	 * @return
	 */
	protected int getIndexCode() {
		if (indexCode == -1) {

			indexCode = getIndexCode(0);

			for (int i = 1; i < variables.size(); i++) {
				indexCode *= variableStateIndices[i].size() + 1;
				indexCode += getIndexCode(i);
			}

		}
		return indexCode;
	}

	/**
	 * Gets index code for single variable, indexed by i
	 * 
	 * @param i
	 * @return
	 */
	private int getIndexCode(int i) {
		if (variableStates[i] == NOT_SET)
			return variableStateIndices[i].size();
		else
			return variableStates[i];
	}

	public int getMaxIndexCode() {
		return getMaxIndexCode((List) variables);
	}

	protected static int getMaxIndexCode(Collection<DiscreteVariable> variables) {
		int max = 1;

		for (DiscreteVariable<?> variable : variables) {
			max *= variable.getDomain().size() + 1;
		}

		return max;
	}

	public boolean isRoot() {
		return parent == null;
	}

	public PartialJointVariableState<T> getParent() {
		return parent;
	}

	public void setPartialSolution(Object partialSolution) {
		this.partialSolution = partialSolution;
	}

	public Object getPartialSolution() {
		return partialSolution;
	}

	public List<DiscreteVariable<T>> getUndeterminedVariables() {
		List<DiscreteVariable<T>> result = new ArrayList<DiscreteVariable<T>>();

		for (int i = 0; i < variableStates.length; i++) {
			if (variableStates[i] == NOT_SET) {
				result.add(variables.get(i));
			}
		}

		return result;
	}

	public DiscreteVariable<T> getLastSetVariable() {
		return lastSetVariable;
	}

	public List<DiscreteVariable<T>> getVariables() {
		return variables;
	}

	/**
	 * Returns the states of the variables that have been set
	 * 
	 * @return
	 */
	public Collection<T> getStates() {
		Collection<T> result = new ArrayList<T>();

		for (int i = 0; i < variableStates.length; i++) {
			if (variableStates[i] != NOT_SET) {
				result.add(variableStateIndices[i].get(variableStates[i]));
			}
		}

		return result;
	}

	public PartialJointVariableState<T> setState(DiscreteVariable<T> variable,
			T state) {
		int size = variables.size();
		int[] currentState = new int[size];
		System.arraycopy(variableStates, 0, currentState, 0, size);

		PartialJointVariableState<T> partialJointVariableState = new PartialJointVariableState<T>(
				variables, variableStateIndices, currentState, this, variable);

		partialJointVariableState.setVariable(variable, state);

		return partialJointVariableState;
	}

	public List<DiscreteVariable<T>> getDeterminedVariables() {
		List<DiscreteVariable<T>> result = new ArrayList<DiscreteVariable<T>>();

		for (int i = 0; i < variableStates.length; i++) {
			if (variableStates[i] != NOT_SET) {
				result.add(variables.get(i));
			}
		}

		return result;
	}
}
