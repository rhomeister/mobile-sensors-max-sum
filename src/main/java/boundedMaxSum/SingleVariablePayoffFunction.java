package boundedMaxSum;

import maxSumController.DiscreteVariableState;
import maxSumController.Variable;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.VariableJointState;

/**
 * Function for random payoff graph colouring - computes payoff based on mine &
 * their state
 * 
 * @author mpw104
 * 
 */
public class SingleVariablePayoffFunction extends BoundedInternalFunction {

	private boolean debug = true;

	// the local variable to determine 'my' state
	protected DiscreteVariable<?> myVariable;

	// The payoffs
	protected double[][] payoffMatrix;

	protected double minPayoff = Double.MAX_VALUE;

	protected double maxPayoff = Double.MIN_VALUE;

	public SingleVariablePayoffFunction(String name,
			DiscreteVariable<?> myVariable) {
		super(name);
		this.myVariable = myVariable;
		int numStates = myVariable.getDomain().getStates().size();
		payoffMatrix = new double[numStates][numStates];
		// Initialise payoff matrix. Cache min and max payoffs to use for
		// bounds.

		for (Object myState : myVariable.getDomain().getStates()) {
			for (Object theirState : myVariable.getDomain().getStates()) {
				double payoff = Math.random();
				if (payoff < minPayoff)
					minPayoff = payoff;
				if (payoff > maxPayoff)
					maxPayoff = payoff;
				payoffMatrix[Integer.parseInt(myState.toString())][Integer
						.parseInt(theirState.toString())] = payoff;
			}
		}
		if (debug) {
			System.out.println("Payoff matrix for " + name + ":");
			for (int i = 0; i < numStates; i++) {
				for (int j = 0; j < numStates; j++) {
					System.out.println("Mine: " + i + " Theirs: " + j
							+ " Payoff: " + payoffMatrix[i][j]);
				}
			}
			System.out.println("Bound difference: " + (maxPayoff - minPayoff));
			System.out.println();
		}
	}

	public double[][] getPayoffMatrix() {
		return payoffMatrix;
	}
	
	@Override
	public double evaluate(VariableJointState state) {

		double result = 0;
		DiscreteVariableState myVariableState = state.getVariableJointStates()
				.get(myVariable);
		for (Variable<?, ?> v : getVariableDependencies()) {
			if (v == myVariable) {
				continue;
			}
			DiscreteVariableState vs = state.getVariableJointStates().get(v);
			double payoff = payoffMatrix[Integer.parseInt(myVariableState
					.toString())][Integer.parseInt(vs.toString())];

			result += payoff;
		}
		return result;
	}

	@Override
	public double getMaximumBound() {

		return maxPayoff;
	}

	@Override
	public double getMinimumBound() {

		return minPayoff;
	}

	@Override
	public BoundedInternalFunction clone() {
		SingleVariablePayoffFunction res = new SingleVariablePayoffFunction(getName(),myVariable);
		res.payoffMatrix = getPayoffMatrix();
		res.maxPayoff = getMaximumBound();
		res.minPayoff = getMinimumBound();
		return res;
	}

}
