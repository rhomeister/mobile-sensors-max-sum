package maxSumController;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.JointStateIterator;
import maxSumController.discrete.VariableJointState;
import maxSumController.dummy.ThreeColorsDomain;
import maxSumController.io.Color;
import maxSumController.io.ColorDomain;

public class TestJointStateIteratorFixed extends TestCase {
	private JointStateIterator variablesStateMap;
	private Color fixedStateA;
	private DiscreteVariable<Color> variableA;

	@Override
	protected void setUp() throws Exception {

		Set<DiscreteVariable<?>> variables = new HashSet<DiscreteVariable<?>>();

		variableA = new DiscreteInternalVariable<Color>("a",
				new ThreeColorsDomain());
		DiscreteVariable<Color> variableB = new DiscreteInternalVariable<Color>(
				"b", new ThreeColorsDomain());
		DiscreteVariable<Color> variableC = new DiscreteInternalVariable<Color>(
				"c", new ThreeColorsDomain());
		variableA.setDomain(new ThreeColorsDomain());

		ColorDomain domainB = new ColorDomain();
		domainB.add(new Color("BLUE"));
		domainB.add(new Color("YELLOW"));

		variableB.setDomain(domainB);
		variableC.setDomain(new ThreeColorsDomain());

		variables.add(variableA);
		variables.add(variableB);
		variables.add(variableC);

		fixedStateA = variableA.getDomain().iterator().next();
		variablesStateMap = new JointStateIterator(variables, variableA,
				fixedStateA);
	}

	public void testHasNext() throws Exception {
		int i = 0;
		while (variablesStateMap.hasNext()) {
			VariableJointState state = variablesStateMap.next();
			assertEquals(state.get(variableA), fixedStateA);
			i++;
		}

		assertEquals(6, i);
	}
}