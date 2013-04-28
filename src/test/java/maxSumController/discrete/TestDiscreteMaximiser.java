package maxSumController.discrete;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import junit.framework.TestCase;
import maxSumController.DiscreteInternalFunction;
import maxSumController.DiscreteVariableState;
import maxSumController.dummy.ThreeColorsDomain;
import maxSumController.io.Color;
import maxSumController.io.ConflictFunction;

public class TestDiscreteMaximiser extends TestCase {

	private DiscreteMaximiser maximiser;
	private HashSet<DiscreteInternalVariable<?>> variables;
	private HashSet<DiscreteInternalFunction> functions;
	private DiscreteInternalVariable<DiscreteVariableState> v1;
	private DiscreteInternalVariable<DiscreteVariableState> v2;
	private DiscreteInternalVariable<DiscreteVariableState> v3;

	@Override
	protected void setUp() throws Exception {
		variables = new HashSet<DiscreteInternalVariable<?>>();
		functions = new HashSet<DiscreteInternalFunction>();

		DiscreteVariableDomain domain = new ThreeColorsDomain();
		v1 = new DiscreteInternalVariable<DiscreteVariableState>("v1", domain);
		v2 = new DiscreteInternalVariable<DiscreteVariableState>("v2", domain);
		v3 = new DiscreteInternalVariable<DiscreteVariableState>("v3", domain);

		DiscreteInternalFunction f1 = new ConflictFunction("f1");
		DiscreteInternalFunction f2 = new ConflictFunction("f2");

		variables.add(v1);
		variables.add(v2);
		variables.add(v3);

		f1.addVariableDependency(v1);
		f1.addVariableDependency(v2);
		f2.addVariableDependency(v2);
		f2.addVariableDependency(v3);

		functions.add(f1);
		functions.add(f2);

		maximiser = new DiscreteMaximiser(variables, functions);
	}

	public void testEvaluate() throws Exception {
		Map<DiscreteInternalVariable<DiscreteVariableState>, DiscreteVariableState> state = new HashMap<DiscreteInternalVariable<DiscreteVariableState>, DiscreteVariableState>();
		state.put(v1, new Color("RED"));
		state.put(v2, new Color("RED"));
		state.put(v3, new Color("RED"));

		VariableJointState currentState = new VariableJointState(state);

		double actual = maximiser.evaluate(currentState);
		System.out.println(actual);
		assertEquals(-2., actual);

		Map<DiscreteInternalVariable<DiscreteVariableState>, DiscreteVariableState> state2 = new HashMap<DiscreteInternalVariable<DiscreteVariableState>, DiscreteVariableState>();
		state2.put(v1, new Color("RED"));
		state2.put(v2, new Color("BLUE"));
		state2.put(v3, new Color("RED"));

		currentState = new VariableJointState(state2);

		actual = maximiser.evaluate(currentState);
		System.out.println(actual);
		assertEquals(0., actual);

	}

	public void testOptimal() throws Exception {
		Map<DiscreteVariable<?>, DiscreteVariableState> optState = maximiser
				.getOptimalState();

		System.out.println(optState);

		assertEquals(0., maximiser.evaluate(new VariableJointState(optState)));

	}

}
