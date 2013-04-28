package maxSumController.continuous.linear;

import java.util.ArrayList;
import java.util.Map;

import junit.framework.TestCase;
import maxSumController.continuous.ContinuousInternalVariable;
import maxSumController.continuous.ContinuousMaxSumController;
import maxSumController.continuous.ContinuousVariable;
import maxSumController.continuous.ContinuousVariableDomainImpl;
import maxSumController.continuous.ContinuousVariableState;

public class TestIntegration extends TestCase {

	private MultiVariatePieceWiseLinearFunction jointUtility;

	private ContinuousInternalVariable v1;

	private ContinuousInternalVariable v2;

	private ContinuousInternalFunction function;

	private NDimensionalPoint a;

	private NDimensionalPoint b;

	private NDimensionalPoint c;

	private NDimensionalPoint d;

	@Override
	protected void setUp() throws Exception {
		NCube domain = new NCube(new double[] { 0, 0 }, new double[] { 1, 1 });
		jointUtility = new MultiVariatePieceWiseLinearFunction(domain);

		a = new NDimensionalPoint(new double[] { 0, 0 });
		b = new NDimensionalPoint(new double[] { 1, 0 });
		c = new NDimensionalPoint(new double[] { 0, 1 });
		d = new NDimensionalPoint(new double[] { 1, 1 });

		jointUtility.addPartition(new NSimplex(a, b, c));
		jointUtility.addPartition(new NSimplex(b, c, d));

		ArrayList<ContinuousVariable> variables = new ArrayList<ContinuousVariable>();
		v1 = new ContinuousInternalVariable("v1",
				new ContinuousVariableDomainImpl(0, 1));
		v2 = new ContinuousInternalVariable("v2",
				new ContinuousVariableDomainImpl(0, 1));
		variables.add(v1);
		variables.add(v2);

		function = new ContinuousInternalFunction("f", jointUtility);

		for (ContinuousVariable variable : variables) {
			function.addVariableDependency(variable);
		}
	}

	public void testFunction1() throws Exception {
		jointUtility.setValue(a, 0.0);
		jointUtility.setValue(b, 1.0);
		jointUtility.setValue(c, 2.0);
		jointUtility.setValue(d, 3.0);

		for (int i = 0; i < 5; i++) {
			Map<ContinuousInternalVariable, ContinuousVariableState> state = runMaxSumController();

			assertEquals(1.0, state.get(v1).getValue());
			assertEquals(1.0, state.get(v2).getValue());

			assertEquals(3.0, jointUtility.evaluate(state.get(v1).getValue(),
					state.get(v2).getValue()));
		}
	}

	public void testFunction2() throws Exception {
		jointUtility.setValue(a, 0.0);
		jointUtility.setValue(b, 3.0);
		jointUtility.setValue(c, 1.0);
		jointUtility.setValue(d, 0.0);

		Map<ContinuousInternalVariable, ContinuousVariableState> state = runMaxSumController();

		assertEquals(1.0, state.get(v1).getValue());
		assertEquals(0.0, state.get(v2).getValue());

		assertEquals(3.0, jointUtility.evaluate(state.get(v1).getValue(), state
				.get(v2).getValue()));

	}

	// public void testFunction3() throws Exception {
	// jointUtility.setValue(a, 0.0);
	// jointUtility.setValue(b, 3.0);
	// jointUtility.setValue(c, 3.0);
	// jointUtility.setValue(d, 0.0);
	//
	// Map<ContinuousInternalVariable, ContinuousVariableState> state =
	// runMaxSumController();
	//
	// double value1 = state.get(v1).getValue();
	// double value2 = state.get(v2).getValue();
	//
	// assertTrue(value1 + " " + value2, value1 + value2 == 1.0
	// && value1 * value2 == 0);
	//
	// assertEquals(3.0, jointUtility.evaluate(state.get(v1).getValue(), state
	// .get(v2).getValue()));
	//
	// }

	private Map<ContinuousInternalVariable, ContinuousVariableState> runMaxSumController() {
		ContinuousMaxSumController cmsc = new ContinuousMaxSumController("A");

		cmsc.addInternalVariable(v1);
		cmsc.addInternalVariable(v2);
		cmsc.addInternalFunction(function);

		for (int i = 0; i < 5; i++) {

			cmsc.calculateNewOutgoingMessages();
		}

		Map<ContinuousInternalVariable, ContinuousVariableState> state = cmsc
				.computeCurrentState();

		return state;
	}
}
