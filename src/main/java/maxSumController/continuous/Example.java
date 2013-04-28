package maxSumController.continuous;

import java.util.ArrayList;
import java.util.Map;

import junit.framework.TestCase;
import maxSumController.continuous.linear.ContinuousInternalFunction;
import maxSumController.continuous.linear.MultiVariatePieceWiseLinearFunction;
import maxSumController.continuous.linear.NCube;
import maxSumController.continuous.linear.NDimensionalPoint;
import maxSumController.continuous.linear.NSimplex;

/**
 * This class contains two examples. Both have two variables connected to a
 * single utility function.
 * 
 * @author rs06r
 * 
 */

public class Example extends TestCase {

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
		// Here, we define a function. This proceeds in three steps:
		// 1. Define the domain
		// 2. Partition the domain into simplices
		// 3. Add values to the definition points of the simplices

		// The domain of an N-ary function is an N-dimensional hypercube.
		// So, since we have a bivariate function, its domain is a 2D hypercube
		// (or square)
		NCube domain = new NCube(new double[] { 0, 0 }, new double[] { 1, 1 });
		jointUtility = new MultiVariatePieceWiseLinearFunction(domain);

		// Now, we create the partitioning of the function. The domain of this
		// function is partitioned into two 2D-simplices (triangles).
		// We name the points of the square a, b, c, and d.
		a = new NDimensionalPoint(new double[] { 0, 0 });
		b = new NDimensionalPoint(new double[] { 1, 0 });
		c = new NDimensionalPoint(new double[] { 0, 1 });
		d = new NDimensionalPoint(new double[] { 1, 1 });

		// The two partitions are abc and bcd.
		jointUtility.addPartition(new NSimplex(a, b, c));
		jointUtility.addPartition(new NSimplex(b, c, d));

		// Now, we define the variables. The variables should have the same
		// dimensions as the function
		ArrayList<ContinuousVariable> variables = new ArrayList<ContinuousVariable>();
		v1 = new ContinuousInternalVariable("v1",
				new ContinuousVariableDomainImpl(0, 1));
		v2 = new ContinuousInternalVariable("v2",
				new ContinuousVariableDomainImpl(0, 1));
		variables.add(v1);
		variables.add(v2);

		// Create the function by giving it a name, and passing the
		// MultiVariatePieceWiseLinearFunction
		function = new ContinuousInternalFunction("f", jointUtility);

		// Assign the variables to the function
		for (ContinuousVariable variable : variables) {
			function.addVariableDependency(variable);
		}

		// Now, there are two different cases in the remainder of this class.
		// These differ in the way we assign values to the definition points of
		// the simplices, thus making two different functions. For both cases,
		// we run the max sum algorithm and verify that the variables have
		// indeed been assigned their optimal values

		// To visualise the domain of the function, uncomment the following
		// statements
		// TwoDimensionalTriangulationGUI gui = new
		// TwoDimensionalTriangulationGUI();
		// gui.draw(jointUtility);
		// Thread.sleep(10000); // without this statement, JUnit will destroy
		// //the GUI immediately

	}

	public void testFunction1() throws Exception {
		jointUtility.setValue(a, 0.0);
		jointUtility.setValue(b, 1.0);
		jointUtility.setValue(c, 2.0);
		// Optimal value occurs when v1 = v2 = 1 (point d)
		jointUtility.setValue(d, 3.0);

		for (int i = 0; i < 5; i++) {
			Map<ContinuousInternalVariable, ContinuousVariableState> state = runMaxSumController();

			// check that v1 and v2 have been assigned the optimal value
			assertEquals(1.0, state.get(v1).getValue());
			assertEquals(1.0, state.get(v2).getValue());

			// check that the function indeed evaluates to the optimal value
			assertEquals(3.0, jointUtility.evaluate(state.get(v1).getValue(),
					state.get(v2).getValue()));
		}
	}

	public void testFunction2() throws Exception {

		jointUtility.setValue(a, 0.0);
		// Optimal value occurs when v1 = 1 and v2 = 0 (point b)
		jointUtility.setValue(b, 3.0);
		jointUtility.setValue(c, 1.0);
		jointUtility.setValue(d, 0.0);

		Map<ContinuousInternalVariable, ContinuousVariableState> state = runMaxSumController();

		assertEquals(1.0, state.get(v1).getValue());
		assertEquals(0.0, state.get(v2).getValue());

		assertEquals(3.0, jointUtility.evaluate(state.get(v1).getValue(), state
				.get(v2).getValue()));
	}

	private Map<ContinuousInternalVariable, ContinuousVariableState> runMaxSumController() {
		// To run the max sum algorithm, we create a ContinuousMaxSumController
		ContinuousMaxSumController cmsc = new ContinuousMaxSumController("A");

		// Make the variables and functions known to the controller
		cmsc.addInternalVariable(v1);
		cmsc.addInternalVariable(v2);
		cmsc.addInternalFunction(function);

		// Run the Max Sum algorithm for 5 iterations
		for (int i = 0; i < 5; i++) {
			cmsc.calculateNewOutgoingMessages();
		}

		// Extract the computed states
		Map<ContinuousInternalVariable, ContinuousVariableState> state = cmsc
				.computeCurrentState();

		return state;
	}
}
