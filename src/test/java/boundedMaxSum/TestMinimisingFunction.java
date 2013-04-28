package boundedMaxSum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import maxSumController.DiscreteVariableState;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.DiscreteVariableDomain;
import maxSumController.discrete.VariableJointState;
import maxSumController.io.Color;
import maxSumController.io.ColorDomain;

public class TestMinimisingFunction extends TestCase {

	private int numberOfColors = 2;

	DiscreteVariableDomain<Color> domain = new ColorDomain(numberOfColors);

	DiscreteInternalVariable<Color> v1 = new DiscreteInternalVariable<Color>(
			"v1", domain);
	DiscreteInternalVariable<Color> v2 = new DiscreteInternalVariable<Color>(
			"v2", domain);
	DiscreteInternalVariable<Color> v3 = new DiscreteInternalVariable<Color>(
			"v3", domain);

	private FixedInternalFunction function;

	@Override
	protected void setUp() throws Exception {
		List<DiscreteInternalVariable<?>> variables = new ArrayList<DiscreteInternalVariable<?>>();

		variables.add(v1);
		variables.add(v2);
		variables.add(v3);

		FixedPayoffMatrix matrix = new FixedPayoffMatrix(variables);

		matrix.setValue(10, new Color("0"), new Color("0"), new Color("0"));
		matrix.setValue(2, new Color("0"), new Color("0"), new Color("1"));
		matrix.setValue(3, new Color("0"), new Color("1"), new Color("0"));
		matrix.setValue(5, new Color("0"), new Color("1"), new Color("1"));
		matrix.setValue(7, new Color("1"), new Color("0"), new Color("0"));
		matrix.setValue(4, new Color("1"), new Color("0"), new Color("1"));
		matrix.setValue(2, new Color("1"), new Color("1"), new Color("0"));
		matrix.setValue(1, new Color("1"), new Color("1"), new Color("1"));

		function = new FixedInternalFunction(variables, matrix);
		function.setOwningAgentIdentifier("");
	}

	public void testMinimiseSingleRejectedVariable() throws Exception {
		Set<DiscreteInternalVariable> rejectedVariables = new HashSet<DiscreteInternalVariable>();
		rejectedVariables.add(v1);

		MinimisingFunction minFunction = new MinimisingFunction(function,
				rejectedVariables);

		assertFalse(minFunction.getVariableDependencies().contains(v1));
		assertTrue(minFunction.getVariableDependencies().contains(v2));
		assertTrue(minFunction.getVariableDependencies().contains(v3));
		assertEquals(2, minFunction.getVariableDependencies().size());

		Map<DiscreteVariable<?>, DiscreteVariableState> values = new HashMap<DiscreteVariable<?>, DiscreteVariableState>();
		values.put(v2, new Color("0"));
		values.put(v3, new Color("0"));
		VariableJointState jointState = new VariableJointState(values);

		assertEquals(7.0, minFunction.evaluate(jointState));

		values = new HashMap<DiscreteVariable<?>, DiscreteVariableState>();
		values.put(v2, new Color("1"));
		values.put(v3, new Color("0"));
		jointState = new VariableJointState(values);

		assertEquals(2.0, minFunction.evaluate(jointState));
	}

	public void testMinimiseTwoRejectedVariables() throws Exception {
		Set<DiscreteInternalVariable> rejectedVariables = new HashSet<DiscreteInternalVariable>();
		rejectedVariables.add(v1);
		rejectedVariables.add(v2);

		MinimisingFunction minFunction = new MinimisingFunction(function,
				rejectedVariables);

		assertFalse(minFunction.getVariableDependencies().contains(v1));
		assertFalse(minFunction.getVariableDependencies().contains(v2));
		assertTrue(minFunction.getVariableDependencies().contains(v3));
		assertEquals(1, minFunction.getVariableDependencies().size());

		Map<DiscreteVariable<?>, DiscreteVariableState> values = new HashMap<DiscreteVariable<?>, DiscreteVariableState>();
		values.put(v3, new Color("0"));
		VariableJointState jointState = new VariableJointState(values);

		assertEquals(2.0, minFunction.evaluate(jointState));

		values = new HashMap<DiscreteVariable<?>, DiscreteVariableState>();
		values.put(v3, new Color("1"));
		jointState = new VariableJointState(values);

		assertEquals(1.0, minFunction.evaluate(jointState));
	}
}
