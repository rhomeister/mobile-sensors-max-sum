package maxSumController.discrete;

import java.util.Map;
import java.util.Set;

import maxSumController.DiscreteInternalFunction;
import maxSumController.DiscreteVariableState;
import maxSumController.Maximiser;

public class DiscreteMaximiser<V extends DiscreteVariable<?>, S extends DiscreteVariableState>
		implements Maximiser<V, S> {

	Set<? extends DiscreteInternalVariable<?>> players;
	Set<? extends DiscreteInternalFunction> functions;
	private boolean foundTie;

	public DiscreteMaximiser(Set<? extends DiscreteInternalVariable<?>> p,
			Set<? extends DiscreteInternalFunction> f) {
		players = p;
		functions = f;
	}

	/**
	 * iterates through all possible combinations of variable value combination
	 * and returns the optimal value.
	 */
	public Map<V, S> getOptimalState() {

		JointStateIterator js = new JointStateIterator(players);
		double bestValue = Double.NEGATIVE_INFINITY;
		VariableJointState currentState = null;
		VariableJointState bestState = null;

		int evalState = 0;
		foundTie = false;
		while (js.hasNext()) {
			currentState = js.next();
			// System.out.println("evaluating state "+currentState);
			double currentValue = evaluate(currentState);

			if (bestValue == currentValue) {
				foundTie = true;
			}
			if (bestValue < currentValue) {
				bestValue = currentValue;
				bestState = currentState;
				foundTie = false;
			}
			evalState++;
		}
		System.out.println("number of evaluated states = " + evalState);

		return (Map<V, S>) bestState.variableJointStates;
	}

	public double evaluate(VariableJointState currentState) {
		double result = 0;

		for (DiscreteInternalFunction f : (Set<DiscreteInternalFunction>) functions) {
			VariableJointState jointState = new VariableJointState(
					currentState.variableJointStates);
			result += f.evaluateRestricted(jointState);
		}

		return result;
	}

	public boolean foundTie() {
		return foundTie;
	}

}
