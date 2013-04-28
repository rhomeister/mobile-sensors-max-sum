package maxSumController.discrete.bb;

import java.util.List;
import java.util.Set;

import maxSumController.DiscreteInternalFunction;
import maxSumController.DiscreteVariableState;
import maxSumController.FactorGraphNode;
import maxSumController.Variable;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.VariableJointState;

import org.apache.commons.lang.NotImplementedException;

import boundedMaxSum.BoundedInternalFunction;
import boundedMaxSum.LinkBoundedInternalFunction;

public class BBFunctionCachingWrapper extends LinkBoundedInternalFunction
		implements BBDiscreteInternalFunction {

	private BBDiscreteInternalFunction function;

	private BBFunctionCache cache;

	private boolean debug = false;

	// private static int misses;
	//
	// private static int calls;

	public BBFunctionCachingWrapper(BBDiscreteInternalFunction function) {
		super(function.getName());
		this.function = function;
		cache = new BBFunctionCache(function);
		// cache.setCachingEnabled(false);
	}

	@Override
	public double getLowerBound(PartialJointVariableState state) {
		if (!cache.containsLowerBound(state)) {
			cache.putLowerBound(state, function.getLowerBound(state));
		}

		if (debug && state.isFullyDetermined()) {
			double upperBound = cache.getUpperBound(state);
			double lowerBound = cache.getLowerBound(state);

			if (upperBound != lowerBound) {

				System.out
						.println("BEFORE DEBUG " + (upperBound == lowerBound));

				System.out.println("upper " + upperBound);
				System.out.println("lower " + lowerBound);

				System.out.println("DEBUG BEING CALLED NOW");

				debug(state);

				System.out.println("Cached lower bound "
						+ cache.getLowerBound(state));

				System.out.println("Cached upper bound "
						+ cache.getUpperBound(state));
				System.out.println(cache.getUpperBound(state) == cache
						.getLowerBound(state));

				System.out.println("Real lower bound "
						+ function.getLowerBound(state));
				System.out.println("Real upper bound "
						+ function.getUpperBound(state));
				throw new IllegalArgumentException(
						"Node is terminal, but upper bound != lower bound");
			}
		}

		return cache.getLowerBound(state);
	}

	@Override
	public void setOwningAgentIdentifier(Comparable owningAgentIdentifier) {
		function.setOwningAgentIdentifier(owningAgentIdentifier);
	}

	@Override
	public Comparable<?> getOwningAgentIdentifier() {
		return function.getOwningAgentIdentifier();
	}

	@Override
	public String getName() {
		return function.getName();
	}

	public BBDiscreteInternalFunction getFunction() {
		return function;
	}

	@Override
	public double getUpperBound(PartialJointVariableState state) {
		if (!cache.containsUpperBound(state)) {
			cache.putUpperBound(state, function.getUpperBound(state));
		}

		return cache.getUpperBound(state);
	}

	@Override
	public double getLowerBound(DiscreteVariable variable,
			DiscreteVariableState state) {
		return function.getLowerBound(variable, state);
	}

	@Override
	public double getUpperBound(DiscreteVariable variable,
			DiscreteVariableState state) {
		return function.getUpperBound(variable, state);
	}

	@Override
	public List<DiscreteVariable> getVariableExpansionOrder() {
		return function.getVariableExpansionOrder();
	}

	@Override
	public double evaluate(VariableJointState state) {
		PartialJointVariableState partialJointVariableState = PartialJointVariableState
				.createPartialJointVariableState(getVariableExpansionOrder(),
						state);

		return getLowerBound(partialJointVariableState);
	}

	@Override
	public Variable<?, ?> getVariableDependency(Variable<?, ?> variable) {
		return function.getVariableDependency(variable);
	}

	@Override
	public Set<? extends FactorGraphNode> getDependencies() {
		return function.getDependencies();
	}

	@Override
	public Set<DiscreteVariable<?>> getDiscreteVariableDependencies() {
		return ((DiscreteInternalFunction) function)
				.getDiscreteVariableDependencies();
	}

	@Override
	public void addVariableDependency(Variable<?, ?> variable) {
		function.addVariableDependency(variable);
	}

	@Override
	public double getBound(Set<DiscreteInternalVariable> rejectedVariables) {
		throw new NotImplementedException();
	}

	@Override
	public LinkBoundedInternalFunction getNewFunction(String deletedVariable) {
		throw new NotImplementedException();
	}

	@Override
	public LinkBoundedInternalFunction getNewFunction(
			Set<DiscreteInternalVariable> rejectedVariables) {
		throw new NotImplementedException();
	}

	@Override
	public double getWeight(String deletedVariableName) {
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
	public void debug(PartialJointVariableState state) {
		debug = false;
		function.debug(state);

		System.out.println("((((((((((((BBFUNC caching))))))))))) " + state);
		System.out.println("LOWER " + getLowerBound(state));
		System.out.println("UPPER " + getUpperBound(state));

		cache.debug(state);
		System.out.println("((((((((((((BBFUNC caching)))))))))))");
	}
}
