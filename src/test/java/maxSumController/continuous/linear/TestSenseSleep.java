package maxSumController.continuous.linear;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import maxSumController.MarginalValues;
import maxSumController.Variable;
import maxSumController.continuous.ContinuousInternalVariable;
import maxSumController.continuous.ContinuousMarginalValues;
import maxSumController.continuous.ContinuousMaxSumController;
import maxSumController.continuous.ContinuousVariable;
import maxSumController.continuous.ContinuousVariableDomainImpl;
import maxSumController.continuous.ContinuousVariableState;
import maxSumController.continuous.LineSegment;
import maxSumController.continuous.LinearMarginalMaximisation;
import maxSumController.continuous.PieceWiseLinearFunction;
import maxSumController.continuous.PieceWiseLinearFunctionImpl;
import maxSumController.continuous.UpperEnvelopeAlgorithm;

public class TestSenseSleep extends TestCase {

	private MultiVariatePieceWiseLinearFunction jointUtility;

	private ContinuousInternalFunction function;

	private ContinuousInternalVariable v1;

	private ContinuousInternalVariable v2;

	private ArrayList<ContinuousVariable> variables;

	@Override
	protected void setUp() throws Exception {
		double l = 10;
		double l1 = 2;
		double l2 = 3;

		NCube domain = new NCube(new double[] { 0, 0 }, new double[] { l - l1,
				l - l2 });
		jointUtility = new MultiVariatePieceWiseLinearFunction(domain);

		NDimensionalPoint origin = new NDimensionalPoint(new double[] { 0, 0 });
		NDimensionalPoint maxx2 = new NDimensionalPoint(new double[] { 0,
				l - l2 });
		NDimensionalPoint maxx1 = new NDimensionalPoint(new double[] { l - l1,
				0 });
		NDimensionalPoint max = new NDimensionalPoint(new double[] { l - l1,
				l - l2 });

		NDimensionalPoint a = new NDimensionalPoint(new double[] { l2 - l1, 0 });
		NDimensionalPoint b = new NDimensionalPoint(new double[] { l2, 0 });
		NDimensionalPoint c = new NDimensionalPoint(new double[] { 0, l1 });
		NDimensionalPoint d = new NDimensionalPoint(new double[] { l - l2 - l1,
				l - l2 });
		NDimensionalPoint e = new NDimensionalPoint(new double[] { l - l2,
				l - l2 });
		NDimensionalPoint f = new NDimensionalPoint(new double[] { l - l1,
				l - l1 - l2 });

		jointUtility.addPartition(new NSimplex(maxx2, c, d));
		jointUtility.addPartition(new NSimplex(origin, d, c));
		jointUtility.addPartition(new NSimplex(origin, d, e));
		jointUtility.addPartition(new NSimplex(origin, e, a));
		jointUtility.addPartition(new NSimplex(a, e, max));
		jointUtility.addPartition(new NSimplex(a, max, b));
		jointUtility.addPartition(new NSimplex(b, max, f));
		jointUtility.addPartition(new NSimplex(b, f, maxx1));

		// TwoDimensionalTriangulationGUI gui = new
		// TwoDimensionalTriangulationGUI();
		//		
		// gui.draw(jointUtility.getPartitioning());
		// Thread.sleep(10000);

		jointUtility.setValue(origin, l2);
		jointUtility.setValue(a, l2);
		jointUtility.setValue(e, l2);
		jointUtility.setValue(max, l2);
		jointUtility.setValue(c, l1 + l2);
		jointUtility.setValue(d, l1 + l2);
		jointUtility.setValue(maxx2, l1 + l2);
		jointUtility.setValue(b, l1 + l2);
		jointUtility.setValue(f, l1 + l2);
		jointUtility.setValue(maxx1, l1 + l2);

		v1 = new ContinuousInternalVariable("v1",
				new ContinuousVariableDomainImpl(0, 8));
		v2 = new ContinuousInternalVariable("v2",
				new ContinuousVariableDomainImpl(0, 7));

		variables = new ArrayList<ContinuousVariable>();
		variables.add(v1);
		variables.add(v2);

		function = new ContinuousInternalFunction("f", jointUtility);

		function.addVariableDependency(v1);
		function.addVariableDependency(v2);

	}

	public void testProjection() throws Exception {
		List<LineSegment> lineSeg = jointUtility.project(0);
		PieceWiseLinearFunction line = UpperEnvelopeAlgorithm
				.calculateUpperEnvelope(lineSeg);
		// System.out.println(line);
		List<LineSegment> lineSeg2 = jointUtility.project(1);
		PieceWiseLinearFunction line2 = UpperEnvelopeAlgorithm
				.calculateUpperEnvelope(lineSeg2);
		// System.out.println(line2);
	}

	public void testMessageSum() throws Exception {
		LinearMarginalMaximisation op = new LinearMarginalMaximisation();

		op.setFunction(function);
		op.setVariables(new HashSet<Variable<?, ?>>(variables));

		Map<Variable<?, ?>, MarginalValues<?>> messages = new HashMap<Variable<?, ?>, MarginalValues<?>>();
		PieceWiseLinearFunction values = new PieceWiseLinearFunctionImpl(
				new LineSegment(0, 0, 7, 1));
		messages.put(v2, new ContinuousMarginalValues(values));

		MarginalValues<?> res = op.calculateMarginalMaxFunction(messages, v1);

		// ContinuousVariableNode node = new ContinuousVariableNode(v1);
		// Set<FunctionToVariableMessage> functionToVariableMessages = new
		// HashSet<FunctionToVariableMessage>();
		// functionToVariableMessages.add(new FunctionToVariableMessage())
		//		
		// node.updateCurrentState(functionToVariableMessages);

	}

	public void testMaxSumController() throws Exception {
		ContinuousMaxSumController cmsc = new ContinuousMaxSumController("A");

		cmsc.addInternalVariable(v1);
		cmsc.addInternalVariable(v2);
		cmsc.addInternalFunction(function);

		for (int i = 0; i < 10; i++) {

			cmsc.calculateNewOutgoingMessages();
		}

		Map<ContinuousInternalVariable, ContinuousVariableState> state = cmsc
				.computeCurrentState();

		System.out.println(state);

		System.out.println(jointUtility.evaluate(new NDimensionalPoint(state
				.get(v1).getValue(), state.get(v2).getValue())));
		//		
		// System.out.println(v1.getPreference());
		// System.out.println(v2.getPreference());

	}

}
