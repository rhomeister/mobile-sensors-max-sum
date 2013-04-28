package maxSumController;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.JointStateIterator;
import maxSumController.dummy.ThreeColorsDomain;
import maxSumController.io.Color;
import maxSumController.io.ColorDomain;

public class TestJointStateIterator extends TestCase {
	private JointStateIterator variablesStateMap;

	@Override
	protected void setUp() throws Exception {

		Set<DiscreteVariable<?>> variables = new HashSet<DiscreteVariable<?>>();

		DiscreteVariable<Color> variableA = new DiscreteInternalVariable<Color>(
				"a", new ThreeColorsDomain());
		DiscreteVariable<Color> variableB = new DiscreteInternalVariable<Color>(
				"b", new ThreeColorsDomain());
		DiscreteVariable<Color> variableC = new DiscreteInternalVariable<Color>(
				"c", new ThreeColorsDomain());
		variableA.setDomain(new ThreeColorsDomain());
		variableB.setDomain(new ThreeColorsDomain());
		variableC.setDomain(new ThreeColorsDomain());

		variables.add(variableA);
		variables.add(variableB);
		variables.add(variableC);

		variablesStateMap = new JointStateIterator(variables);
	}

	public void testHasNext() throws Exception {
		int i = 0;
		while (variablesStateMap.hasNext()) {
			variablesStateMap.next();
			i++;
		}

		assertEquals(27, i);
	}

	public void testSingleJointState() throws Exception {
		DiscreteVariable<Color> variableA = new DiscreteInternalVariable<Color>(
				"a", new ColorDomain(1));

		JointStateIterator iterator = new JointStateIterator(Collections
				.singleton(variableA));

		int i = 0;

		while (iterator.hasNext()) {
			iterator.next();
			i++;
		}

		assertEquals(i, 1);
	}
}
