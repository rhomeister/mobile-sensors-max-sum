package boundedMaxSum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import maxSumController.DiscreteVariableState;
import maxSumController.Variable;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.JointStateIterator;
import maxSumController.discrete.VariableJointState;
import maxSumController.discrete.bb.BBDiscreteInternalFunction;
import maxSumController.discrete.bb.BBFunctionCache;
import maxSumController.discrete.bb.PartialJointVariableState;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.Validate;

public class MinimisingFunction extends LinkBoundedInternalFunction implements
		BBDiscreteInternalFunction {

	private final BBDiscreteInternalFunction wrappedFunction;
	private final Set<DiscreteInternalVariable> rejectedVariables;
	private boolean debug;
	private final BBFunctionCache cache;

	public MinimisingFunction(BBDiscreteInternalFunction wrappedFunction,
			Set<DiscreteInternalVariable> rejectedVariables) {
		super(wrappedFunction.getName());
		setOwningAgentIdentifier(wrappedFunction.getOwningAgentIdentifier());

		Validate.isTrue(
				wrappedFunction.getVariableDependencies().containsAll(
						rejectedVariables),
				"Rejected variables have to be a subset "
						+ "of the variable dependencies");

		this.wrappedFunction = wrappedFunction;
		this.rejectedVariables = rejectedVariables;

		Set<Variable<?, ?>> variableDependencies = wrappedFunction
				.getVariableDependencies();
		for (Variable<?, ?> variableDependency : variableDependencies) {
			if (!rejectedVariables.contains(variableDependency)) {
				addVariableDependency(variableDependency);
			}
		}

		cache = new BBFunctionCache(this);

	}

	@Override
	public double getBound(Set<DiscreteInternalVariable> arg0) {
		throw new NotImplementedException();
	}

	@Override
	public LinkBoundedInternalFunction getNewFunction(String variableName) {
		throw new NotImplementedException();
	}

	@Override
	public LinkBoundedInternalFunction getNewFunction(
			Set<DiscreteInternalVariable> var) {
		throw new NotImplementedException();
	}

	@Override
	public double getWeight(String arg0) {
		throw new NotImplementedException();
	}

	@Override
	public BoundedInternalFunction clone() {
		throw new NotImplementedException();
	}

	@Override
	public double getMaximumBound() {
		throw new NotImplementedException();
	}

	@Override
	public double getMinimumBound() {
		throw new NotImplementedException();
	}

	@Override
	public double evaluate(VariableJointState jointState) {
		Validate.isTrue(jointState.getVariables().containsAll(
				getVariableDependencies()));

		return getLeafValue(PartialJointVariableState
				.createPartialJointVariableState(getVariableExpansionOrder(),
						jointState));
	}

	// lower bound on the maximum
	public double getLowerBound(PartialJointVariableState partialState) {
		return getBound(partialState, true);
	}

	// upper bound on the maximum
	public double getUpperBound(PartialJointVariableState partialState) {
		// return getBound(partialState, true);

		if (partialState.isFullyDetermined()) {
			return getLowerBound(partialState);
		}

		return getBound(partialState, false);
	}

	private double getBound(PartialJointVariableState partialState,
			boolean lower) {
		cache.initialise();

		if (lower && cache.containsLowerBound(partialState)) {
			return cache.getLowerBound(partialState);
		}
		if (!lower && cache.containsUpperBound(partialState)) {
			return cache.getUpperBound(partialState);
		}

		// fully calculate....

		if (partialState.isFullyDetermined()) {
			double leafValue = getLeafValue(partialState);
			cache.putLowerBound(partialState, leafValue);
			return leafValue;
		} else {
			double bestUtility = lower ? Double.POSITIVE_INFINITY
					: Double.NEGATIVE_INFINITY;

			Collection<PartialJointVariableState> children = partialState
					.getChildren();

			// expand children to find maximum value
			for (PartialJointVariableState partialJointVariableState : children) {
				if (lower)
					bestUtility = Math.min(bestUtility,
							getBound(partialJointVariableState, true));
				else
					bestUtility = Math.max(bestUtility,
							getBound(partialJointVariableState, false));
			}

			// store values so recomputation is not needed
			if (lower)
				cache.putLowerBound(partialState, bestUtility);
			else
				cache.putUpperBound(partialState, bestUtility);

			return bestUtility;

		}

		// JointStateIterator fixedVariableValueIterator = new
		// JointStateIterator(
		// (Set<? extends DiscreteVariable<?>>) rejectedVariables);
		//
		//
		// double bestUtility = lower ? Double.POSITIVE_INFINITY
		// : Double.NEGATIVE_INFINITY;
		//
		// while (fixedVariableValueIterator.hasNext()) {
		// VariableJointState next = fixedVariableValueIterator.next();
		//
		// PartialJointVariableState combinedState = PartialJointVariableState
		// .combineStates(wrappedFunction.getVariableExpansionOrder(),
		// partialState, next);
		//
		// if (debug)
		// wrappedFunction.debug(combinedState);
		//
		// if (lower) {
		// bestUtility = Math.min(bestUtility, wrappedFunction
		// .getLowerBound(combinedState));
		// } else {
		// bestUtility = Math.max(bestUtility, wrappedFunction
		// .getUpperBound(combinedState));
		// }
		// }
		//
		// return bestUtility;
	}

	private double getLeafValue(PartialJointVariableState<?> partialState) {
		double bestUtility = Double.POSITIVE_INFINITY;

		JointStateIterator fixedVariableValueIterator = null; // new
																// JointStateIterator(
		// (Set<? extends DiscreteVariable<?>>) rejectedVariables);

		while (fixedVariableValueIterator.hasNext()) {
			VariableJointState next = fixedVariableValueIterator.next();
			PartialJointVariableState combinedState = PartialJointVariableState
					.combineStates(wrappedFunction.getVariableExpansionOrder(),
							partialState, next);

			Validate.isTrue(combinedState.isFullyDetermined());

			double lowerBound = wrappedFunction.getLowerBound(combinedState);
			double evaluate = wrappedFunction.evaluate(combinedState
					.getJointState());

			if (lowerBound != evaluate) {
				System.out.println(lowerBound);
				System.out.println(evaluate);
				throw new IllegalArgumentException();
			}

			bestUtility = Math.min(bestUtility,
					wrappedFunction.evaluate(combinedState.getJointState()));
		}

		return bestUtility;

	}

	public double getLowerBound(DiscreteVariable variable,
			DiscreteVariableState state) {
		throw new NotImplementedException();
	}

	public double getUpperBound(DiscreteVariable variable,
			DiscreteVariableState arg1) {
		throw new NotImplementedException();
	}

	public List<DiscreteVariable> getVariableExpansionOrder() {
		List<DiscreteVariable> variableExpansionOrder = new ArrayList<DiscreteVariable>(
				wrappedFunction.getVariableExpansionOrder());

		variableExpansionOrder.removeAll(rejectedVariables);

		return variableExpansionOrder;
	}

	@Override
	public void debug(PartialJointVariableState state) {
		System.out.println("MINIMISING FUNCTION TEST STARTS");

		debug = true;

		double lowerBound = getLowerBound(state);

		double upperBound = getUpperBound(state);

		System.out.println("___*******MIN FUNCTION RESULT*******___ " + state);
		System.out.println(lowerBound);
		System.out.println(upperBound);
		System.out.println("___*******END MIN FUNCTION RESULT*******___");

	}

	@Override
	public String toString() {
		return "Minimising" + super.toString();
	}
}
