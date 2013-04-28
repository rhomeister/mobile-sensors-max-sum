package maxSumController.discrete.prune;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import maxSumController.DiscreteVariableState;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteMaxSumController;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.DiscreteVariableNode;
import maxSumController.discrete.VariableJointState;
import maxSumController.discrete.bb.AbstractBBDiscreteInternalFunction;
import maxSumController.discrete.bb.BBDiscreteMarginalMaximisation;
import maxSumController.discrete.bb.PartialJointVariableState;
import maxSumController.discrete.bb.TripleStore;
import maxSumController.dummy.ThreeColorsDomain;
import maxSumController.io.Color;
import util.HashTripleStore;

public class TestScenario extends TestCase {

	private DiscreteMaxSumController controller;
	private DiscreteInternalVariable<Color> variable;
	private DummyBBInternalFunction function1;

	@Override
	protected void setUp() throws Exception {
		controller = new DiscreteMaxSumController("agent1",
				new BBDiscreteMarginalMaximisation());

		variable = new DiscreteInternalVariable<Color>("variable",
				new ThreeColorsDomain());

		controller.addInternalVariable(variable);

		function1 = new DummyBBInternalFunction("f1");
		function1.setLowerBound(variable, Color.RED, 0);
		function1.setLowerBound(variable, Color.BLUE, 0);
		function1.setLowerBound(variable, Color.GREEN, 1);
		function1.setUpperBound(variable, Color.RED, 1);
		function1.setUpperBound(variable, Color.BLUE, 1);
		function1.setUpperBound(variable, Color.GREEN, 2);
		controller.addInternalFunction(function1);
		function1.addVariableDependency(variable);

		DummyBBInternalFunction function2 = new DummyBBInternalFunction("f2");
		function2.setLowerBound(variable, Color.RED, 0);
		function2.setLowerBound(variable, Color.BLUE, 0);
		function2.setLowerBound(variable, Color.GREEN, 2);
		function2.setUpperBound(variable, Color.RED, 2);
		function2.setUpperBound(variable, Color.BLUE, 1);
		function2.setUpperBound(variable, Color.GREEN, 2);
		controller.addInternalFunction(function2);
		function2.addVariableDependency(variable);

		DummyBBInternalFunction function3 = new DummyBBInternalFunction("f3");
		function3.setLowerBound(variable, Color.RED, 2);
		function3.setLowerBound(variable, Color.BLUE, 0);
		function3.setLowerBound(variable, Color.GREEN, 0);
		function3.setUpperBound(variable, Color.RED, 2);
		function3.setUpperBound(variable, Color.BLUE, 0);
		function3.setUpperBound(variable, Color.GREEN, 0);
		controller.addInternalFunction(function3);
		function3.addVariableDependency(variable);

		controller.setMaxSumEnabled(false);
	}

	public void testScenario() throws Exception {
		controller.startPruningAlgorithm();

		controller.calculateNewOutgoingMessages();
		controller.setMaxSumEnabled(false);

		assertEquals(2, variable.getDomain().size());
		assertTrue(variable.getDomain().getStates().contains(Color.RED));
		assertTrue(variable.getDomain().getStates().contains(Color.GREEN));

		assertFalse(((DiscreteVariableNode) controller
				.getInternalNode(variable)).isPruningConverged());
		controller.calculateNewOutgoingMessages();
		controller.setMaxSumEnabled(false);
		assertTrue(((DiscreteVariableNode) controller.getInternalNode(variable))
				.isPruningConverged());
		controller.calculateNewOutgoingMessages();
		controller.setMaxSumEnabled(false);

		assertTrue(((DiscreteVariableNode) controller.getInternalNode(variable))
				.isPruningConverged());
	}

	private static class DummyBBInternalFunction extends
			AbstractBBDiscreteInternalFunction {

		TripleStore<DiscreteInternalVariable<Color>, Color, Double> lowerBounds = new HashTripleStore<DiscreteInternalVariable<Color>, Color, Double>();
		TripleStore<DiscreteInternalVariable<Color>, Color, Double> upperBounds = new HashTripleStore<DiscreteInternalVariable<Color>, Color, Double>();

		public DummyBBInternalFunction(String string) {
			super(string);
		}

		public void setUpperBound(DiscreteInternalVariable<Color> variable,
				Color color, double value) {
			upperBounds.put(variable, color, value);
		}

		public void setLowerBound(DiscreteInternalVariable<Color> variable,
				Color color, double value) {
			lowerBounds.put(variable, color, value);
		}

		@Override
		public double getLowerBound(PartialJointVariableState state) {
			return lowerBounds.get(state.getLastSetVariable(), state
					.getState(state.getLastSetVariable()));
		}

		@Override
		public double getUpperBound(PartialJointVariableState state) {
			return upperBounds.get(state.getLastSetVariable(), state
					.getState(state.getLastSetVariable()));
		}

		@Override
		public double getLowerBound(DiscreteVariable variable,
				DiscreteVariableState state) {
			return lowerBounds.get(variable, state);
		}

		@Override
		public double getUpperBound(DiscreteVariable variable,
				DiscreteVariableState state) {
			return upperBounds.get(variable, state);
		}

		@Override
		public List<DiscreteVariable> getVariableExpansionOrder() {
			return new ArrayList<DiscreteVariable>(
					getDiscreteVariableDependencies());
		}

		@Override
		public double evaluate(VariableJointState state) {
			throw new IllegalArgumentException();
		}

	}
}
