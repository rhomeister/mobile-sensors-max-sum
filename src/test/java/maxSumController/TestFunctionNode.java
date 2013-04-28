package maxSumController;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteMarginalMaximisationImpl;
import maxSumController.discrete.DiscreteMarginalValues;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.io.Color;
import maxSumController.io.ColorDomain;
import maxSumController.io.ConflictFunction;

public class TestFunctionNode extends TestCase {
	private HashSet<VariableToFunctionMessage> messages;

	private FunctionNode node;

	private DiscreteVariable<Color> variableA;

	private DiscreteVariable<Color> variableB;

	private DiscreteVariable<Color> variableC;

	@Override
	protected void setUp() throws Exception {
		ColorDomain twoColors = new ColorDomain();
		twoColors.add(Color.RED);
		twoColors.add(Color.BLUE);

		variableA = new DiscreteInternalVariable<Color>("a", twoColors);
		variableB = new DiscreteInternalVariable<Color>("b", twoColors);
		variableC = new DiscreteInternalVariable<Color>("c", twoColors);
		DiscreteInternalFunction functionA = new ConflictFunction("fa");

		functionA.addVariableDependency(variableA);
		functionA.addVariableDependency(variableB);
		functionA.addVariableDependency(variableC);

		node = new FunctionNode(functionA,
				new DiscreteMarginalMaximisationImpl());

		Map<Color, Double> valuesFromA = new HashMap<Color, Double>();
		valuesFromA.put(Color.RED, 1.0);
		valuesFromA.put(Color.BLUE, 2.0);

		VariableToFunctionMessage messageFromA = new VariableToFunctionMessage(
				variableA, functionA, new DiscreteMarginalValues<Color>(
						valuesFromA));

		Map<Color, Double> valuesFromB = new HashMap<Color, Double>();

		valuesFromB.put(Color.RED, 3.0);
		valuesFromB.put(Color.BLUE, 2.0);

		VariableToFunctionMessage messageFromB = new VariableToFunctionMessage(
				variableB, functionA, new DiscreteMarginalValues<Color>(
						valuesFromB));

		Map<Color, Double> valuesFromC = new HashMap<Color, Double>();
		valuesFromC.put(Color.RED, 3.0);
		valuesFromC.put(Color.BLUE, 4.0);
		VariableToFunctionMessage messageFromC = new VariableToFunctionMessage(
				variableC, functionA, new DiscreteMarginalValues<Color>(
						valuesFromC));

		messages = new HashSet<VariableToFunctionMessage>();
		messages.add(messageFromA);
		messages.add(messageFromB);
		messages.add(messageFromC);
	}

	public void testUpdateOutgoingMessages() throws Exception {
		Set<FunctionToVariableMessage> messagesFromFunction = node
				.updateOutgoingMessages(messages);

		assertEquals(3, messagesFromFunction.size());

		for (FunctionToVariableMessage message : messagesFromFunction) {
			if (message.getReceiver().equals(variableA)) {
				DiscreteMarginalValues<?> function = (DiscreteMarginalValues<?>) message
						.getMarginalFunction();
				Map<Color, Double> expectedValues = new HashMap<Color, Double>();
				expectedValues.put(Color.RED, 6.0);
				expectedValues.put(Color.BLUE, 6.0);

				DiscreteMarginalValues<Color> expectedFunction = new DiscreteMarginalValues<Color>(
						expectedValues);

				assertEquals(expectedFunction, function);
			}
			if (message.getReceiver().equals(variableB)) {
				DiscreteMarginalValues<?> function = (DiscreteMarginalValues<?>) message
						.getMarginalFunction();
				Map<Color, Double> expectedValues = new HashMap<Color, Double>();
				expectedValues.put(Color.RED, 5.0);
				expectedValues.put(Color.BLUE, 4.0);

				DiscreteMarginalValues<Color> expectedFunction = new DiscreteMarginalValues<Color>(
						expectedValues);

				assertEquals(expectedFunction, function);
			}
			if (message.getReceiver().equals(variableC)) {
				DiscreteMarginalValues<?> function = (DiscreteMarginalValues) message
						.getMarginalFunction();
				Map<Color, Double> expectedValues = new HashMap<Color, Double>();
				expectedValues.put(Color.RED, 4.0);
				expectedValues.put(Color.BLUE, 4.0);

				DiscreteMarginalValues<Color> expectedFunction = new DiscreteMarginalValues<Color>(
						expectedValues);

				assertEquals(expectedFunction, function);
			}
		}
	}
}
