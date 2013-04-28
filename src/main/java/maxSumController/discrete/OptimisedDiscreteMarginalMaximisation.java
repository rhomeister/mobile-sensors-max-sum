package maxSumController.discrete;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import maxSumController.DiscreteInternalFunction;
import maxSumController.DiscreteVariableState;
import maxSumController.InternalFunction;
import maxSumController.MarginalMaximisationFactory;
import maxSumController.MarginalValues;
import maxSumController.Variable;
import maxSumController.VariableState;
import maxSumController.discrete.bb.AbstractDiscreteMarginalMaximisation;

import org.apache.commons.lang.Validate;

public class OptimisedDiscreteMarginalMaximisation extends
		AbstractDiscreteMarginalMaximisation implements
		MarginalMaximisationFactory {

	private DiscreteInternalFunction function;

	private Set<DiscreteVariable<? extends DiscreteVariableState>> variables;

	boolean precomputed = false;

	private double[] precomputedFunction;

	private DiscreteVariable<? extends DiscreteVariableState>[] variableArray;

	private DiscreteVariableState[][] variableStatesArray;

	private int[][] individualIntegerStatesArray;

	// keeps track of the best joint state if we fix the state of one variable
	// first index is the variable sequence number (the index of the variable in
	// variableArray)
	// second index is the state sequence number
	// the indexed value is the sequence number of the best joint state given we
	// fix the variable to the given state
	private int[][] bestVariableJointState;

	public MarginalValues<?> calculateMarginalMaxFunction(
			Map<Variable<?, ?>, MarginalValues<?>> sortedMessages,
			Variable<?, ?> variable) {
		if (!precomputed) {
			precompute();
		}

		DiscreteVariable<? extends DiscreteVariableState> discreteVariable = (DiscreteVariable<? extends DiscreteVariableState>) variable;

		double precomputedMessages[][] = precomputeMessages(sortedMessages);

		double[] valuesMatrix = new double[discreteVariable.getDomain().size()];
		Arrays.fill(valuesMatrix, -Double.MAX_VALUE);

		int variableIndex = getVariableIndex(discreteVariable);

		// Loop through all the joint variable states
		for (int jointStateIndex = 0; jointStateIndex < precomputedFunction.length; jointStateIndex++) {
			// evaluate the function
			double utility = precomputedFunction[jointStateIndex];

			// compute marginal maximum for all other variables
			for (int otherVariableIndex = 0; otherVariableIndex < variables
					.size(); otherVariableIndex++) {
				if (variableIndex != otherVariableIndex) {
					// get the state of the other variable in this joint state
					int variableStateIndex = individualIntegerStatesArray[jointStateIndex][otherVariableIndex];

					if (precomputedMessages[otherVariableIndex] != null) {

						double d = precomputedMessages[otherVariableIndex][variableStateIndex];
						utility += d;
					}
				}
			}

			// get the state of the variable we are sending the message to
			int variableStateIndex = individualIntegerStatesArray[jointStateIndex][variableIndex];

			// check if this joint state is better for this variablestate index
			// than the current maximum
			if (valuesMatrix[variableStateIndex] < utility) {
				valuesMatrix[variableStateIndex] = utility;
				bestVariableJointState[variableIndex][variableStateIndex] = jointStateIndex;
			}

		}

		Map<DiscreteVariableState, Double> values = new HashMap<DiscreteVariableState, Double>();

		for (int variableStateIndex = 0; variableStateIndex < discreteVariable
				.getDomain().size(); variableStateIndex++) {
			values.put(variableStatesArray[variableIndex][variableStateIndex],
					valuesMatrix[variableStateIndex]);
			if (variableStatesArray[variableIndex][variableStateIndex] == null) {
				System.out.println();
			}
		}
		return new DiscreteMarginalValues<DiscreteVariableState>(values);
	}

	private double[][] precomputeMessages(
			Map<Variable<?, ?>, MarginalValues<?>> sortedMessages) {
		double[][] result = new double[variables.size()][];

		for (Variable<?, ?> variable : sortedMessages.keySet()) {
			DiscreteVariableDomain<?> domain = (DiscreteVariableDomain<?>) variable
					.getDomain();

			int variableIndex = getVariableIndex((DiscreteVariable<?>) variable);

			DiscreteMarginalValues<?> marginalValues = (DiscreteMarginalValues<?>) sortedMessages
					.get(variable);

			Validate.isTrue(domain.size() == marginalValues.getValues().size(),
					"" + marginalValues + " " + domain.getStates());
			result[variableIndex] = new double[marginalValues.getValues()
					.size()];

			for (DiscreteVariableState state : marginalValues.getValues()
					.keySet()) {
				int stateIndex = getStateIndex(variableIndex, state);

				Validate.notNull(marginalValues.getValues().get(state));

				result[variableIndex][stateIndex] = marginalValues.getValues()
						.get(state);
			}
		}

		return result;
	}

	private int getStateIndex(int variableIndex, DiscreteVariableState state) {
		for (int i = 0; i < variableStatesArray[variableIndex].length; i++) {
			DiscreteVariableState s = variableStatesArray[variableIndex][i];
			if (s.equals(state))
				return i;
		}

		throw new IllegalArgumentException("VariableState " + state
				+ " is not known. "
				+ "Does the state class have equals() and hashCode() methods?");
	}

	private int getVariableIndex(DiscreteVariable<?> variable) {
		for (int i = 0; i < variableArray.length; i++) {
			DiscreteVariable<?> v = variableArray[i];
			if (v.equals(variable))
				return i;
		}

		throw new IllegalArgumentException("Variable " + variable
				+ " is not known");
	}

	private void precompute() {
		variableArray = new DiscreteVariable<?>[variables.size()];
		variableStatesArray = new DiscreteVariableState[variables.size()][];
		bestVariableJointState = new int[variables.size()][];

		int size = 1;

		int variableIndex = 0;

		// represent both variables and variable states with integers
		for (DiscreteVariable<? extends DiscreteVariableState> variable : variables) {

			variableArray[variableIndex] = variable;
			DiscreteVariableDomain<?> domain = variable.getDomain();
			size *= domain.size();

			variableStatesArray[variableIndex] = new DiscreteVariableState[domain
					.size()];
			int variableStateIndex = 0;
			for (DiscreteVariableState state : domain) {
				variableStatesArray[variableIndex][variableStateIndex++] = state;
			}

			bestVariableJointState[variableIndex] = new int[domain.size()];

			variableIndex++;
		}

		precomputedFunction = new double[size];
		individualIntegerStatesArray = new int[size][variables.size()];

		JointStateIterator jointStateIterator = new JointStateIterator(
				variables);

		int jointStateIndex = 0;
		while (jointStateIterator.hasNext()) {
			VariableJointState jointState = jointStateIterator.next();
			precomputedFunction[jointStateIndex] = function
					.evaluate(jointState);

			// associate a number with a variable, and compute the state of that
			// variable based on the integer associated with the state
			for (variableIndex = 0; variableIndex < variableArray.length; variableIndex++) {
				DiscreteVariableState state = jointState
						.get(variableArray[variableIndex]);
				individualIntegerStatesArray[jointStateIndex][variableIndex] = getStateIndex(
						variableIndex, state);
			}
			jointStateIndex++;
		}

		precomputed = true;
	}

	public void setFunction(InternalFunction function) {
		if (this.function == null || !this.function.equals(function)) {
			precomputed = false;
		}

		this.function = (DiscreteInternalFunction) function;
	}

	@Override
	public void setVariables(Set<? extends Variable<?, ?>> variables) {
		if (this.variables == null || !this.variables.equals(variables)) {
			precomputed = false;
		}

		this.variables = new HashSet<DiscreteVariable<? extends DiscreteVariableState>>(
				(Set<DiscreteVariable<? extends DiscreteVariableState>>) variables);
	}

	public DiscreteMarginalMaximisation create() {
		return new OptimisedDiscreteMarginalMaximisation();
	}

	/**
	 * Returns the best state of the target variable, if a variable is fixed to
	 * a certain state
	 * 
	 * @param fixedVariable
	 *            the variable for which the state is fixed
	 * @param fixedState
	 *            the state to which the variable is fixed
	 * @param targetVariable
	 *            the variable for which the best state is required
	 * @return the best state of the targetVariable given the state of the fixed
	 *         variable
	 */
	@Override
	public VariableState getBestState(Variable<?, ?> fixedVariable,
			VariableState fixedState, Variable<?, ?> targetVariable) {
		// find the indices of the fixed variable and the fixed state
		int fixedVariableIndex = getVariableIndex((DiscreteVariable) fixedVariable);
		int stateIndex = getStateIndex(fixedVariableIndex,
				(DiscreteVariableState) fixedState);

		// retrieve the index of the best joint state
		int bestJointStateIndex = bestVariableJointState[fixedVariableIndex][stateIndex];

		// find the indices of the targetVariable and the bestJointState
		int targetVariableIndex = getVariableIndex((DiscreteVariable) targetVariable);
		int targetVariableStateIndex = individualIntegerStatesArray[bestJointStateIndex][targetVariableIndex];

		return variableStatesArray[targetVariableIndex][targetVariableStateIndex];
	}

	@Override
	protected DiscreteInternalFunction getFunction() {
		return function;
	}
}
