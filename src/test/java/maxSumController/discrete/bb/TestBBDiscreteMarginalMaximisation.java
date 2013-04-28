package maxSumController.discrete.bb;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import maxSumController.FunctionNode;
import maxSumController.FunctionToVariableMessage;
import maxSumController.MarginalValues;
import maxSumController.Variable;
import maxSumController.VariableToFunctionMessage;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteMarginalMaximisationImpl;
import maxSumController.discrete.DiscreteMarginalValues;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.dummy.ThreeColorsDomain;
import maxSumController.io.Color;
import maxSumController.io.ColorDomain;
import maxSumController.io.ConflictFunction;

public class TestBBDiscreteMarginalMaximisation extends TestCase {
	private Collection<VariableToFunctionMessage> messages;

	private FunctionNode regularNode;

	private DiscreteVariable<Color> variableA;

	private DiscreteVariable<Color> variableB;

	private DiscreteVariable<Color> variableC;

	private FunctionNode bbNode;

	private ConflictFunction functionA;

	private HashMap<Variable<?, ?>, MarginalValues<?>> sortedMessages;

	@Override
	protected void setUp() throws Exception {
		BBDiscreteMarginalMaximisation.validate = true;

		ColorDomain domain = new ThreeColorsDomain();

		variableA = new DiscreteInternalVariable<Color>("a", domain);
		variableB = new DiscreteInternalVariable<Color>("b", domain);
		variableC = new DiscreteInternalVariable<Color>("c", domain);
		functionA = new ConflictFunction("fa");

		functionA.addVariableDependency(variableA);
		functionA.addVariableDependency(variableB);
		functionA.addVariableDependency(variableC);

		regularNode = new FunctionNode(functionA,
				new DiscreteMarginalMaximisationImpl());

		bbNode = new FunctionNode(functionA,
				new BBDiscreteMarginalMaximisation());

		Map<Color, Double> valuesFromA = new HashMap<Color, Double>();
		valuesFromA.put(Color.RED, 1.0);
		valuesFromA.put(Color.BLUE, 2.0);
		valuesFromA.put(Color.GREEN, 1.0);

		VariableToFunctionMessage messageFromA = new VariableToFunctionMessage(
				variableA, functionA, new DiscreteMarginalValues<Color>(
						valuesFromA));

		Map<Color, Double> valuesFromB = new HashMap<Color, Double>();

		valuesFromB.put(Color.RED, 3.0);
		valuesFromB.put(Color.BLUE, 2.0);
		valuesFromB.put(Color.GREEN, 1.0);

		VariableToFunctionMessage messageFromB = new VariableToFunctionMessage(
				variableB, functionA, new DiscreteMarginalValues<Color>(
						valuesFromB));

		Map<Color, Double> valuesFromC = new HashMap<Color, Double>();
		valuesFromC.put(Color.RED, 3.0);
		valuesFromC.put(Color.BLUE, 4.0);
		valuesFromC.put(Color.GREEN, 0.0);

		VariableToFunctionMessage messageFromC = new VariableToFunctionMessage(
				variableC, functionA, new DiscreteMarginalValues<Color>(
						valuesFromC));

		// messageFromA.normalise();
		// messageFromB.normalise();
		// messageFromC.normalise();

		messages = new HashSet<VariableToFunctionMessage>();
		messages.add(messageFromA);
		messages.add(messageFromB);
		messages.add(messageFromC);

		sortedMessages = new HashMap<Variable<?, ?>, MarginalValues<?>>();
		sortedMessages.put(variableA, messageFromA.getMarginalFunction());
		sortedMessages.put(variableB, messageFromB.getMarginalFunction());
		sortedMessages.put(variableC, messageFromC.getMarginalFunction());
	}

	public void testCompare() throws Exception {
		Set<FunctionToVariableMessage> regularMessages = regularNode
				.updateOutgoingMessages(messages);

		Set<FunctionToVariableMessage> bbMessages = bbNode
				.updateOutgoingMessages(messages);

		assertEquals(regularMessages, bbMessages);
	}

	public void testFindMaxValue() throws Exception {
		BBDiscreteMarginalMaximisation mm = new BBDiscreteMarginalMaximisation();
		
		mm.setFunction(functionA);
		mm.setVariables(functionA.getVariableDependencies());

		Color colorA = Color.BLUE;
		assertEquals(6.0, mm.findMaxValue(variableA, colorA, sortedMessages));
		assertEquals(colorA, mm.getBestState(variableA, colorA, variableA));
		assertEquals(Color.RED, mm.getBestState(variableA, colorA, variableB));
		assertEquals(Color.BLUE, mm.getBestState(variableA, colorA, variableC));

		colorA = Color.RED;
		assertEquals(6.0, mm.findMaxValue(variableA, colorA, sortedMessages));
		assertEquals(colorA, mm.getBestState(variableA, colorA, variableA));
		assertEquals(Color.RED, mm.getBestState(variableA, colorA, variableB));
		assertEquals(Color.BLUE, mm.getBestState(variableA, colorA, variableC));

		colorA = Color.GREEN;
		assertEquals(7.0, mm.findMaxValue(variableA, colorA, sortedMessages));
		assertEquals(colorA, mm.getBestState(variableA, colorA, variableA));
		assertEquals(Color.RED, mm.getBestState(variableA, colorA, variableB));
		assertEquals(Color.BLUE, mm.getBestState(variableA, colorA, variableC));
	}
}
