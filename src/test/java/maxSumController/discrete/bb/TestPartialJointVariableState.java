package maxSumController.discrete.bb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import maxSumController.DiscreteVariableState;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.DiscreteVariableDomain;
import maxSumController.discrete.DiscreteVariableDomainImpl;
import maxSumController.discrete.VariableJointState;
import maxSumController.io.Color;

public class TestPartialJointVariableState extends TestCase {

	private List<DiscreteVariable<Color>> variables;

	@Override
	protected void setUp() throws Exception {
		variables = new ArrayList<DiscreteVariable<Color>>();

		variables
				.add(new DiscreteInternalVariable<Color>("1", createDomain(3)));
		variables
				.add(new DiscreteInternalVariable<Color>("2", createDomain(4)));
		variables
				.add(new DiscreteInternalVariable<Color>("3", createDomain(1)));
		variables
				.add(new DiscreteInternalVariable<Color>("4", createDomain(2)));
	}

	public static DiscreteVariableDomain<Color> createDomain(int count) {
		DiscreteVariableDomainImpl<Color> domain = new DiscreteVariableDomainImpl<Color>();

		for (int i = 0; i < count; i++) {
			domain.add(new Color("" + i));
		}

		return domain;
	}

	public void testCreatePartialJointVariableState() throws Exception {
		Map<DiscreteVariable<?>, DiscreteVariableState> values = new HashMap<DiscreteVariable<?>, DiscreteVariableState>();
		values.put(variables.get(0), new Color("2"));
		values.put(variables.get(1), new Color("3"));
		values.put(variables.get(2), new Color("0"));
		values.put(variables.get(3), new Color("1"));

		VariableJointState jointState = new VariableJointState(values);

		List<DiscreteVariable> variableExpansionOrder = new ArrayList<DiscreteVariable>();
		variableExpansionOrder.addAll(variables);

		PartialJointVariableState<Color> partialState = PartialJointVariableState
				.createPartialJointVariableState(variableExpansionOrder,
						jointState);

		assertTrue(partialState.isFullyDetermined());
		assertEquals(new Color("2"), partialState.getState(variables.get(0)));
		assertEquals(new Color("3"), partialState.getState(variables.get(1)));
		assertEquals(new Color("0"), partialState.getState(variables.get(2)));
		assertEquals(new Color("1"), partialState.getState(variables.get(3)));
	}

	public void testCombineStates() throws Exception {
		Map<DiscreteVariable<?>, DiscreteVariableState> values = new HashMap<DiscreteVariable<?>, DiscreteVariableState>();
		values.put(variables.get(0), new Color("2"));
		values.put(variables.get(1), new Color("3"));

		VariableJointState jointState = new VariableJointState(values);

		PartialJointVariableState<?> partialState = new PartialJointVariableState<Color>(
				variables.subList(2, 4), variables.get(2), new Color("0"));

		List<DiscreteVariable> list = new ArrayList<DiscreteVariable>();
		list.addAll(variables);

		PartialJointVariableState<Color> combined = PartialJointVariableState
				.combineStates(list, partialState, jointState);

		assertTrue(!combined.isFullyDetermined());
		assertEquals(new Color("2"), combined.getState(variables.get(0)));
		assertEquals(new Color("3"), combined.getState(variables.get(1)));
		assertEquals(new Color("0"), combined.getState(variables.get(2)));
		assertFalse(combined.isSet(variables.get(3)));
	}

	public void testCreate() throws Exception {
		DiscreteVariable<Color> rootVariable = variables.get(0);
		PartialJointVariableState<Color> state = new PartialJointVariableState<Color>(
				variables, rootVariable, new Color("2"));

		assertEquals(new Color("2"), state.getState(rootVariable));
		assertEquals(null, state.getState(variables.get(1)));
		assertEquals(null, state.getState(variables.get(2)));
		assertEquals(null, state.getState(variables.get(3)));

		assertTrue(state.isSet(rootVariable));
		assertTrue(state.isSet(variables.get(0)));
		assertFalse(state.isSet(variables.get(1)));
		assertEquals(1, state.getDeterminedCount());
		assertEquals(3, state.getNotDeterminedCount());

		assertFalse(state.getUndeterminedVariables().contains(variables.get(0)));
		assertTrue(state.getUndeterminedVariables().contains(variables.get(1)));
		assertTrue(state.getUndeterminedVariables().contains(variables.get(2)));
		assertTrue(state.getUndeterminedVariables().contains(variables.get(3)));

		rootVariable = variables.get(1);
		state = new PartialJointVariableState<Color>(variables, rootVariable,
				new Color("2"));

		assertEquals(null, state.getState(variables.get(0)));
		assertEquals(new Color("2"), state.getState(rootVariable));
		assertEquals(null, state.getState(variables.get(2)));
		assertEquals(null, state.getState(variables.get(3)));
	}

	public void testGetChildren() throws Exception {
		DiscreteVariable<Color> rootVariable = variables.get(0);
		PartialJointVariableState<Color> state = new PartialJointVariableState<Color>(
				variables, rootVariable, new Color("2"));

		Collection<PartialJointVariableState<Color>> children = state
				.getChildren();

		assertEquals(4, children.size());

		Set<Color> variableStates = new HashSet<Color>();

		DiscreteVariable<Color> newVariable = variables.get(1);

		for (PartialJointVariableState<Color> childState : children) {
			assertTrue(childState.isSet(rootVariable));
			assertTrue(childState.isSet(newVariable));
			assertFalse(childState.isSet(variables.get(2)));
			assertFalse(childState.isSet(variables.get(3)));
			variableStates.add(childState.getState(newVariable));

			assertEquals(2, childState.getDeterminedCount());
			assertEquals(2, childState.getNotDeterminedCount());
		}

		assertEquals(variableStates, newVariable.getDomain().getStates());
	}

	public void testCurrentVariable() throws Exception {
		DiscreteVariable<Color> rootVariable = variables.get(1);
		PartialJointVariableState<Color> state = new PartialJointVariableState<Color>(
				variables, rootVariable, new Color("2"));

		assertEquals(rootVariable, state.getLastSetVariable());

		PartialJointVariableState<Color> childState = state.getChildren()
				.iterator().next();

		assertTrue(childState.isSet(variables.get(0)));
		assertTrue(childState.isSet(variables.get(1)));
		assertFalse(childState.isSet(variables.get(2)));
		assertFalse(childState.isSet(variables.get(3)));
		assertFalse(childState.isFullyDetermined());
		assertEquals(2, childState.getDeterminedCount());
		assertEquals(2, childState.getNotDeterminedCount());
		assertEquals(variables.get(0), childState.getLastSetVariable());
		assertEquals(2, childState.getUndeterminedVariables().size());
		assertFalse(childState.getUndeterminedVariables().contains(
				variables.get(0)));
		assertFalse(childState.getUndeterminedVariables().contains(
				variables.get(1)));
		assertTrue(childState.getUndeterminedVariables().contains(
				variables.get(2)));
		assertTrue(childState.getUndeterminedVariables().contains(
				variables.get(3)));

		childState = childState.getChildren().iterator().next();

		assertTrue(childState.isSet(variables.get(0)));
		assertTrue(childState.isSet(variables.get(1)));
		assertTrue(childState.isSet(variables.get(2)));
		assertFalse(childState.isSet(variables.get(3)));
		assertFalse(childState.isFullyDetermined());
		assertEquals(3, childState.getDeterminedCount());
		assertEquals(1, childState.getNotDeterminedCount());
		assertEquals(variables.get(2), childState.getLastSetVariable());
		assertEquals(1, childState.getUndeterminedVariables().size());
		assertFalse(childState.getUndeterminedVariables().contains(
				variables.get(0)));
		assertFalse(childState.getUndeterminedVariables().contains(
				variables.get(1)));
		assertFalse(childState.getUndeterminedVariables().contains(
				variables.get(2)));
		assertTrue(childState.getUndeterminedVariables().contains(
				variables.get(3)));

		childState = childState.getChildren().iterator().next();

		assertTrue(childState.isSet(variables.get(0)));
		assertTrue(childState.isSet(variables.get(1)));
		assertTrue(childState.isSet(variables.get(2)));
		assertTrue(childState.isSet(variables.get(3)));
		assertTrue(childState.isFullyDetermined());
		assertEquals(4, childState.getDeterminedCount());
		assertEquals(0, childState.getNotDeterminedCount());
		assertEquals(variables.get(3), childState.getLastSetVariable());
		assertEquals(0, childState.getUndeterminedVariables().size());

		try {
			childState.getChildren();
			fail();
		} catch (IllegalArgumentException e) {

		}
	}

	public void testMaxIndexCode() throws Exception {
		DiscreteVariable<Color> rootVariable = variables.get(0);
		PartialJointVariableState<Color> state = new PartialJointVariableState<Color>(
				variables, rootVariable, new Color("2"));

		assertEquals(120, state.getMaxIndexCode());
	}

	public void testCheckTerminalNodeCount() throws Exception {
		for (DiscreteVariable<Color> variable : variables) {
			LinkedList<PartialJointVariableState<Color>> states = new LinkedList<PartialJointVariableState<Color>>();

			for (Color state : variable.getDomain()) {
				states.add(new PartialJointVariableState<Color>(variables,
						variable, state));
			}

			int count = 0;
			Set<Integer> codes = new HashSet<Integer>();

			while (!states.isEmpty()) {
				PartialJointVariableState<Color> currentState = states.pop();
				assertTrue(codes.add(currentState.getIndexCode()));

				if (!currentState.isFullyDetermined()) {
					states.addAll(currentState.getChildren());
				} else {
					count++;
				}
			}

			assertEquals(24, count);
		}
	}

	public void testIndexCodes() throws Exception {
		LinkedList<PartialJointVariableState<Color>> states = new LinkedList<PartialJointVariableState<Color>>();

		int maxCode = new PartialJointVariableState<Color>(variables, variables
				.get(0), new Color("0")).getMaxIndexCode();

		for (DiscreteVariable<Color> variable : variables) {
			for (Color state : variable.getDomain()) {
				states.add(new PartialJointVariableState<Color>(variables,
						variable, state));
			}
		}

		Set<Integer> codes = new HashSet<Integer>();

		while (!states.isEmpty()) {
			PartialJointVariableState<Color> currentState = states.pop();
			codes.add(currentState.getIndexCode());

			if (!currentState.isFullyDetermined()) {
				states.addAll(currentState.getChildren());
			}
		}

		assertTrue(maxCode > codes.size());
	}

	public void testIndexCodesConsistency() throws Exception {
		for (DiscreteVariable<Color> rootVariable : variables) {
			for (Color rootState : rootVariable.getDomain()) {
				PartialJointVariableState<Color> root = new PartialJointVariableState<Color>(
						variables, rootVariable, rootState);
				checkIndexCodeConsistency(root);
			}
		}
	}

	private void checkIndexCodeConsistency(PartialJointVariableState<Color> root) {
		PartialJointVariableState<Color> checkedState = new PartialJointVariableState<Color>(
				variables);
		for (DiscreteVariable<Color> variable : root.getDeterminedVariables()) {
			checkedState = checkedState.setState(variable, root
					.getState(variable));

			assertEquals(variable, checkedState.getLastSetVariable());
		}

		assertEquals(checkedState.getIndexCode(), root.getIndexCode());

		if (root.isFullyDetermined()) {
			VariableJointState jointState = root.getJointState();
			PartialJointVariableState createPartialJointVariableState = PartialJointVariableState
					.createPartialJointVariableState(
							new ArrayList<DiscreteVariable>(variables),
							jointState);
			assertEquals(root.getIndexCode(), createPartialJointVariableState
					.getIndexCode());

			return;
		} else {
			for (PartialJointVariableState<Color> state : root.getChildren()) {

				assertEquals(root, state.getParent());

				checkIndexCodeConsistency(state);
			}
		}
	}
}
