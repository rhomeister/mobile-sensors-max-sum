package maxSumController.discrete;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import maxSumController.DiscreteVariableState;

/**
 * 
 * @author sandrof
 * 
 *         This class represents the joint state of a set of variables
 * 
 */

public class JointStateIterator implements Iterator<VariableJointState> {

	// the set of variables and their states
	// protected VariableJointState variableJointStates;

	// the set of variables and their states
	protected Map<DiscreteVariable<?>, Iterator<? extends DiscreteVariableState>> variableJointStateSequencers = new HashMap<DiscreteVariable<?>, Iterator<? extends DiscreteVariableState>>();

	private boolean initialised;

	private HashMap<DiscreteVariable<?>, DiscreteVariableState> states;

	/**
	 * Constructor.
	 */
	public JointStateIterator(Set<? extends DiscreteVariable<?>> var) {
		this(var, null, null);
	}

	/**
	 * Creates a JointStateIterator that iterates over all joint states, whereby
	 * a single variable is fixed to a certain state
	 * 
	 * @param discreteVariableDependencies
	 * @param variable
	 *            if null, then the resulting iterator iterates over all states
	 * @param state
	 */
	public JointStateIterator(Set<? extends DiscreteVariable<?>> variables,
			DiscreteVariable<?> variable, DiscreteVariableState state) {
		states = new HashMap<DiscreteVariable<?>, DiscreteVariableState>();

		for (DiscreteVariable<?> v : variables) {

			Iterator<? extends DiscreteVariableState> iterator;

			if (!v.equals(variable)) {
				iterator = v.getDomain().iterator();
			} else {
				iterator = Collections.singleton(state).iterator();
			}
			variableJointStateSequencers.put(v, iterator);
			states.put(v, iterator.next());
		}
	}

	public boolean hasNext() {
		for (Iterator<? extends DiscreteVariableState> it : variableJointStateSequencers
				.values()) {
			if (!initialised)
				return true;

			if (it.hasNext()) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public VariableJointState next() {
		if (!initialised) {
			initialised = true;
		} else {

			boolean done = false;

			// System.out.println(nodeList);
			Iterator<DiscreteVariable<?>> i = variableJointStateSequencers
					.keySet().iterator();
			DiscreteVariable<?> v = i.next();

			// System.out.println("current state = "+sensorNode);
			while (!done) {

				// VariableDomainSequencer vs =
				// variableJointStateSequencers.get(v);
				Iterator<? extends DiscreteVariableState> vs = variableJointStateSequencers
						.get(v);

				if (vs.hasNext()) {
					states.put(v, vs.next());

					done = true;
				} else {
					vs = v.getDomain().iterator();
					variableJointStateSequencers.put(v, vs);

					states.put(v, vs.next());

					if (i.hasNext()) {
						v = i.next();
					} else {
						throw new NoSuchElementException();
					}
				}
			}
		}

		return new VariableJointState(
				(Map<DiscreteVariable<?>, DiscreteVariableState>) states
						.clone());
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
}
