package maxSumController;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteMarginalValues;
import maxSumController.discrete.DiscreteVariableDomain;
import maxSumController.discrete.DiscreteVariableNode;
import maxSumController.dummy.ThreeColorsDomain;
import maxSumController.io.Color;

public class TestVariableNode extends TestCase {

	private DiscreteVariableNode<?> node;

	private Set<FunctionToVariableMessage> messages;

	private DummyFunction functionA;

	private DummyFunction functionB;

	private DummyFunction functionC;

	private DiscreteInternalVariable<Color> variable;

	private FunctionToVariableMessage messageFromB;

	private FunctionToVariableMessage messageFromC;

	@Override
	protected void setUp() throws Exception {
		functionA = new DummyFunction("a");
		functionB = new DummyFunction("b");
		functionC = new DummyFunction("c");
		variable = new DiscreteInternalVariable<Color>("v",
				new ThreeColorsDomain());
		variable.addFunctionDependency(functionA);
		variable.addFunctionDependency(functionB);
		variable.addFunctionDependency(functionC);

		DiscreteVariableDomain<Color> variableDomain = new ThreeColorsDomain();
		variable.setDomain(variableDomain);
		node = new DiscreteVariableNode<Color>(variable);

		Map<Color, Double> values = new HashMap<Color, Double>();

		values.put(Color.RED, 1.0);
		values.put(Color.BLUE, 2.0);
		values.put(Color.GREEN, 2.0);

		FunctionToVariableMessage messageFromA = new FunctionToVariableMessage(
				functionA, variable, new DiscreteMarginalValues<Color>(values));

		values = new HashMap<Color, Double>();
		values.put(Color.RED, 3.0);
		values.put(Color.BLUE, 2.0);
		values.put(Color.GREEN, 1.0);
		messageFromB = new FunctionToVariableMessage(functionB, variable,
				new DiscreteMarginalValues<Color>(values));
		messages = new HashSet<FunctionToVariableMessage>();
		messages.add(messageFromA);
		messages.add(messageFromB);

		values = new HashMap<Color, Double>();
		values.put(Color.RED, 3.0);
		values.put(Color.BLUE, 4.0);
		values.put(Color.GREEN, 0.0);
		messageFromC = new FunctionToVariableMessage(functionC, variable,
				new DiscreteMarginalValues<Color>(values));
		messages = new HashSet<FunctionToVariableMessage>();
		messages.add(messageFromA);
		messages.add(messageFromB);
		messages.add(messageFromC);

	}

	public void testUpdateOutgoingMessages() throws Exception {
		Collection<VariableToFunctionMessage> updateOutgoingMessages = node
				.updateOutgoingMessages(messages);

		// System.out.println(updateOutgoingMessages);

		VariableToFunctionMessage messageToA = null;

		for (VariableToFunctionMessage message : updateOutgoingMessages) {
			if (message.getReceiver().equals(functionA)) {
				messageToA = message;
				break;
			}
		}

		Map<Color, Double> expectedValues = new HashMap<Color, Double>();

		Map<Color, Double> valuesFromB = ((DiscreteMarginalValues<Color>) messageFromB
				.getMarginalFunction()).getValues();
		Map<Color, Double> valuesFromC = ((DiscreteMarginalValues<Color>) messageFromC
				.getMarginalFunction()).getValues();

		for (Color state : valuesFromB.keySet()) {
			expectedValues.put(state, valuesFromB.get(state));
		}

		for (Color state : valuesFromC.keySet()) {
			double newValue = expectedValues.get(state)
					+ valuesFromC.get(state);
			expectedValues.put(state, newValue);
		}

		VariableToFunctionMessage expectedMessageToA = new VariableToFunctionMessage(
				variable, functionA, new DiscreteMarginalValues<Color>(
						expectedValues));
		expectedMessageToA.getMarginalFunction().normalise();

		assertEquals(functionA, messageToA.getReceiver());
		assertEquals(variable, messageToA.getSender());

		DiscreteMarginalValues<Color> marginalValues = (DiscreteMarginalValues<Color>) messageToA
				.getMarginalFunction();

		for (Color state : marginalValues.getValues().keySet()) {
			assertEquals(marginalValues.getValue(state),
					((DiscreteMarginalValues<Color>) expectedMessageToA
							.getMarginalFunction()).getValue(state), 1e-4);
		}
	}

	public void testUpdateCurrentState() throws Exception {
		boolean state = node.updateCurrentState(messages);
		assertTrue(state);
		assertEquals(Color.BLUE, node.getCurrentState());
	}

	public void testNullMessage() throws Exception {

		Collection<VariableToFunctionMessage> message = node
				.updateOutgoingMessages(new HashSet<FunctionToVariableMessage>());
		Map<Color, Double> zeroValues = new HashMap<Color, Double>();
		for (Color vs : variable.getDomain()) {
			zeroValues.put(vs, 0.);
		}
		DiscreteMarginalValues<Color> zero = new DiscreteMarginalValues<Color>(
				zeroValues);

		for (VariableToFunctionMessage m : message) {
			DiscreteMarginalValues<Color> marginalValues = (DiscreteMarginalValues<Color>) m
					.getMarginalFunction();

			for (Color state : marginalValues.getValues().keySet()) {
				assertEquals(marginalValues.getValue(state), zero
						.getValue(state), 1e-4);
			}

		}
	}
}