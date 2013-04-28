package maxSumController.continuous.linear;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import maxSumController.FixedIterationStoppingCriterion;
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

public class TestSimpleJointFunction extends TestCase {

	private MultiVariatePieceWiseLinearFunction jointUtility;

	private ContinuousInternalFunction function;

	private ContinuousInternalVariable v1;

	private ContinuousInternalVariable v2;

	private ArrayList<ContinuousVariable> variables;

	private int l1;

	private int l2;

	@Override
	protected void setUp() throws Exception {
		l1 = 2;
		l2 = 3;

		NCube domain = new NCube(new double[] { 0, 0 }, new double[] { l1, l2 });
		jointUtility = new MultiVariatePieceWiseLinearFunction(domain);

		NDimensionalPoint origin = new NDimensionalPoint(new double[] { 0, 0 });
		NDimensionalPoint maxx2 = new NDimensionalPoint(new double[] { 0, l2 });
		NDimensionalPoint maxx1 = new NDimensionalPoint(new double[] { l1, 0 });
		NDimensionalPoint max = new NDimensionalPoint(new double[] { l1, l2 });

		jointUtility.addPartition(new NSimplex(origin, maxx1, max));
		jointUtility.addPartition(new NSimplex(origin, maxx2, max));

		/*
		 * TwoDimensionalTriangulationGUI gui = new
		 * TwoDimensionalTriangulationGUI();
		 * 
		 * gui.draw(jointUtility.getPartitioning()); Thread.sleep(10000);
		 */
		jointUtility.setValue(origin, 0);
		jointUtility.setValue(max, 0);
		jointUtility.setValue(maxx2, 10);
		jointUtility.setValue(maxx1, 10);

		variables = new ArrayList<ContinuousVariable>();
		v1 = new ContinuousInternalVariable("v1",
				new ContinuousVariableDomainImpl(0, l1));
		v2 = new ContinuousInternalVariable("v2",
				new ContinuousVariableDomainImpl(0, l2));

		PieceWiseLinearFunction f1 = new PieceWiseLinearFunctionImpl();
		f1.addSegment(new LineSegment(0, 0.0001, l1, 0.0002));
		MarginalValues<ContinuousVariableState> newPreferences1 = new ContinuousMarginalValues(
				f1);
		v1.setPreference(newPreferences1);

		PieceWiseLinearFunction f2 = new PieceWiseLinearFunctionImpl();
		f2.addSegment(new LineSegment(0, 0.0002, l2, 0.0005));
		MarginalValues<ContinuousVariableState> newPreferences2 = new ContinuousMarginalValues(
				f2);
		v2.setPreference(newPreferences2);

		variables.add(v1);
		variables.add(v2);

		function = new ContinuousInternalFunction("f", jointUtility);

		for (ContinuousVariable variable : variables) {
			function.addVariableDependency(variable);
		}

	}

	public void testProjection() throws Exception {
		List<LineSegment> lineSeg = jointUtility.project(0);
		PieceWiseLinearFunction line = UpperEnvelopeAlgorithm
				.calculateUpperEnvelope(lineSeg);
		//System.out.println("project on V1" + line);
		List<LineSegment> lineSeg2 = jointUtility.project(1);
		PieceWiseLinearFunction line2 = UpperEnvelopeAlgorithm
				.calculateUpperEnvelope(lineSeg2);
		//System.out.println("project on V2" + line2);
	}

	public void testMessages() throws Exception {
		LinearMarginalMaximisation op = new LinearMarginalMaximisation();

		op.setFunction(function);
		op.setVariables(new HashSet<Variable<?, ?>>(variables));

		// message from v2 to f
		ContinuousVariableDomainImpl vd2 = new ContinuousVariableDomainImpl(0,
				l2);
		MarginalValues<ContinuousVariableState> values2 = vd2
				.createZeroMarginalFunction();
		Map<Variable<?, ?>, MarginalValues<?>> mv2f = new HashMap<Variable<?, ?>, MarginalValues<?>>();
		mv2f.put(v2, values2);

		// message from f to v1
		MarginalValues<?> mfv1 = op.calculateMarginalMaxFunction(mv2f, v1);
		List<LineSegment> lineSeg1 = jointUtility.project(0);
		PieceWiseLinearFunction upperEnvV1 = UpperEnvelopeAlgorithm
				.calculateUpperEnvelope(lineSeg1);

		//System.out.println("mfv1 " + mfv1);
		//System.out.println("upperEnvV1" + upperEnvV1);

		// message from v1 to f
		ContinuousVariableDomainImpl vd1 = new ContinuousVariableDomainImpl(0,
				l1);
		MarginalValues<ContinuousVariableState> values1 = vd1
				.createZeroMarginalFunction();
		Map<Variable<?, ?>, MarginalValues<?>> mv1f = new HashMap<Variable<?, ?>, MarginalValues<?>>();
		mv1f.put(v1, values1);

		// message from f to v2
		MarginalValues<?> mfv2 = op.calculateMarginalMaxFunction(mv1f, v2);
		//System.out.println(mfv2);
		List<LineSegment> lineSeg2 = jointUtility.project(1);
		PieceWiseLinearFunction upperEnvV2 = UpperEnvelopeAlgorithm
				.calculateUpperEnvelope(lineSeg2);

		//System.out.println("mfv2 " + mfv2);
		//System.out.println("upperEnvV2" + upperEnvV2);

	}

	public void testMaxSum() throws Exception {
		ContinuousMaxSumController cmsc = new ContinuousMaxSumController(
				"agent");
		cmsc.addInternalFunction(function);
		cmsc.addInternalVariable(v1);
		cmsc.addInternalVariable(v2);

		// System.out.println("v1 pref. " + v1.getPreference());
		// System.out.println("v2 pref. " + v2.getPreference());

		cmsc.setStoppingCriterion(new FixedIterationStoppingCriterion(10));
		while (!cmsc.stoppingCriterionIsMet()) {
			cmsc.calculateNewOutgoingMessages();
		}

		Map<ContinuousInternalVariable, ContinuousVariableState> state = cmsc
				.computeCurrentState();

		double val = function.evaluate(new ContinuousVariableState[] {
				state.get(v1), state.get(v2) });
		//System.out.println(state + " " + val);

	}

}
