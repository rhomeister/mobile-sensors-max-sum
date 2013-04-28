package maxSumController.discrete;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import maxSumController.DiscreteVariableState;

public class DiscreteVariableDomainImpl<T extends DiscreteVariableState>
		implements DiscreteVariableDomain<T> {

	public static double SCALER = 0.001;

	protected Set<T> states = new HashSet<T>();

	public DiscreteVariableDomainImpl() {
	}

	public DiscreteVariableDomainImpl(Set<T> states) {
		this.states = states;
	}

	public final void add(T state) {
		states.add(state);
	}

	public final Set<T> getStates() {
		return states;
	}

	public final int size() {
		return states.size();
	}

	public final Iterator<T> iterator() {
		return states.iterator();
	}

	@Override
	public String toString() {
		return states.toString();
	}

	public DiscreteMarginalValues<T> createZeroMarginalFunction() {
		Map<T, Double> zeroValues = new HashMap<T, Double>();
		for (T vs : getStates()) {
			zeroValues.put(vs, 0.);
			if (vs == null) {
				System.out.println();
			}
		}
		DiscreteMarginalValues<T> zero = new DiscreteMarginalValues<T>(
				zeroValues);
		return zero;
	}

	public DiscreteMarginalValues<T> createPreference() {
		Map<T, Double> pref = new HashMap<T, Double>();
		for (T vs : getStates()) {
			pref.put(vs, SCALER * Math.random());
			if (vs == null) {
				System.out.println();
			}
		}
		DiscreteMarginalValues<T> zero = new DiscreteMarginalValues<T>(pref);
		return zero;
	}

	/**
	 * Two domains are considered equals if they contain the same states
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DiscreteVariableDomain<?>) {
			DiscreteVariableDomain<?> domain = (DiscreteVariableDomain<?>) obj;
			return domain.getStates().equals(getStates());
		}

		return false;
	}

}
