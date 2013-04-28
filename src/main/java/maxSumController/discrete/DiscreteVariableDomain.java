package maxSumController.discrete;

import java.util.Set;

import maxSumController.DiscreteVariableState;
import maxSumController.VariableDomain;

public interface DiscreteVariableDomain<T extends DiscreteVariableState>
		extends VariableDomain<T>, Iterable<T> {

	public void add(T state);

	public Set<T> getStates();

	public int size();

	public DiscreteMarginalValues<T> createZeroMarginalFunction();

	public DiscreteMarginalValues<T> createPreference();

}
