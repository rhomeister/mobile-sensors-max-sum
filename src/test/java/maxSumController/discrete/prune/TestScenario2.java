package maxSumController.discrete.prune;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;
import maxSumController.DiscreteVariableState;
import maxSumController.ExternalFunction;
import maxSumController.communication.Message;
import maxSumController.discrete.DiscreteExternalVariable;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteMaxSumController;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.DiscreteVariableDomainImpl;
import maxSumController.discrete.VariableJointState;
import maxSumController.discrete.bb.AbstractBBDiscreteInternalFunction;
import maxSumController.discrete.bb.BBDiscreteMarginalMaximisation;
import maxSumController.discrete.bb.PartialJointVariableState;
import maxSumController.discrete.bb.TripleStore;
import maxSumController.io.Color;
import maxSumController.io.ColorDomain;

import org.apache.commons.lang.Validate;

import util.HashTripleStore;

public class TestScenario2 extends TestCase {

	private DiscreteMaxSumController controller1;
	private DiscreteMaxSumController controller2;
	private DiscreteInternalVariable<Color> variable1;
	private DiscreteInternalVariable<Color> variable2;
	private DummyBBInternalFunction function;

	@Override
	protected void setUp() throws Exception {
		controller1 = new DiscreteMaxSumController("agent1",
				new BBDiscreteMarginalMaximisation());

		ColorDomain domain1 = new ColorDomain();
		domain1.add(Color.RED);
		domain1.add(Color.BLUE);
		variable1 = new DiscreteInternalVariable<Color>("variable1", domain1);
		controller1.addInternalVariable(variable1);

		TripleStore<Color, Color, Double> values = new HashTripleStore<Color, Color, Double>();
		values.put(Color.RED, Color.RED, 1.0);
		values.put(Color.RED, Color.BLUE, 2.0);
		values.put(Color.BLUE, Color.RED, 0.0);
		values.put(Color.BLUE, Color.BLUE, 3.0);

		ColorDomain domain2External = new ColorDomain();
		domain2External.add(Color.RED);
		domain2External.add(Color.BLUE);
		DiscreteExternalVariable<Color> variable2External = new DiscreteExternalVariable<Color>(
				"variable2", domain2External, "agent2");

		function = new DummyBBInternalFunction("f1", values, variable1,
				variable2External);
		controller1.addInternalFunction(function);

		function.addVariableDependency(variable1);
		function.addVariableDependency(variable2External);

		controller2 = new DiscreteMaxSumController("agent2",
				new BBDiscreteMarginalMaximisation());

		ColorDomain domain2 = new ColorDomain();
		domain2.add(Color.RED);
		domain2.add(Color.BLUE);
		variable2 = new DiscreteInternalVariable<Color>("variable2", domain2);

		ExternalFunction functionExternal = new ExternalFunction("f1", "agent1");
		variable2.addFunctionDependency(functionExternal);
		controller2.addInternalVariable(variable2);

		controller1.setMaxSumEnabled(false);
	}

	public void testScenario() throws Exception {

		controller1.startPruningAlgorithm();
		controller2.startPruningAlgorithm();

		Collection<Message> messagesFrom1 = new ArrayList<Message>();
		Collection<Message> messagesFrom2 = new ArrayList<Message>();

		for (int i = 0; i < 10; i++) {
			System.out.println(i);
			controller1.handleIncomingMessages(messagesFrom2);
			controller2.handleIncomingMessages(messagesFrom1);
			messagesFrom1 = controller1.calculateNewOutgoingMessages();
			messagesFrom2 = controller2.calculateNewOutgoingMessages();

			System.out.println("Messages from 1 " + messagesFrom1);
			System.out.println("Messages from 2 " + messagesFrom2);
		}

		System.out.println(controller1.computeCurrentState());
		System.out.println(controller2.computeCurrentState());

		assertEquals(1, variable1.getDomain().size());
		assertTrue(variable1.getDomain().getStates().contains(Color.BLUE));

		assertEquals(1, variable2.getDomain().size());
		assertTrue(variable2.getDomain().getStates().contains(Color.BLUE));
	}

	private static class DummyBBInternalFunction extends
			AbstractBBDiscreteInternalFunction {

		private TripleStore<Color, Color, Double> values;
		private DiscreteVariable<Color> variable1;
		private DiscreteVariable<Color> variable2;

		public DummyBBInternalFunction(String string,
				TripleStore<Color, Color, Double> values,
				DiscreteVariable<Color> variable1,
				DiscreteVariable<Color> variable2) {
			super(string);
			this.values = values;
			this.variable1 = variable1;
			this.variable2 = variable2;
		}

		@Override
		public double getLowerBound(PartialJointVariableState state) {
			// throw new NotImplementedException();
			return 0.0;
		}

		@Override
		public double getUpperBound(PartialJointVariableState state) {
			// throw new NotImplementedException();
			return 0.0;
		}

		@Override
		public double getLowerBound(DiscreteVariable variable,
				DiscreteVariableState state) {
			Validate.isTrue(((DiscreteVariableDomainImpl<?>) variable
					.getDomain()).getStates().contains(state));

			Collection<Double> stateValues = new ArrayList<Double>();

			if (variable.equals(variable1)) {
				for (Color state2 : variable2.getDomain().getStates()) {
					stateValues.add(values.get(state, state2));
				}
			} else if (variable.equals(variable2)) {
				for (Color state1 : variable1.getDomain().getStates()) {
					stateValues.add(values.get(state1, state));
				}
			} else {
				throw new IllegalArgumentException();
			}

			return Collections.min(stateValues);
		}

		@Override
		public double getUpperBound(DiscreteVariable variable,
				DiscreteVariableState state) {
			Validate.isTrue(((DiscreteVariableDomainImpl<?>) variable
					.getDomain()).getStates().contains(state));

			Collection<Double> stateValues = new ArrayList<Double>();

			if (variable.equals(variable1)) {
				for (Color state2 : variable2.getDomain().getStates()) {
					stateValues.add(values.get(state, state2));
				}
			} else if (variable.equals(variable2)) {
				for (Color state1 : variable1.getDomain().getStates()) {
					stateValues.add(values.get(state1, state));
				}
			} else {
				throw new IllegalArgumentException();
			}

			return Collections.max(stateValues);
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
