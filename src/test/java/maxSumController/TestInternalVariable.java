package maxSumController;

import junit.framework.TestCase;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.dummy.ThreeColorsDomain;
import maxSumController.io.Color;
import maxSumController.io.ConflictFunction;

public class TestInternalVariable extends TestCase {

	private DiscreteInternalVariable<Color> variableA1;

	private DiscreteInternalVariable<Color> variableB;

	private DiscreteInternalVariable<Color> variableA2;

	@Override
	protected void setUp() throws Exception {
		variableA1 = new DiscreteInternalVariable<Color>("a",
				new ThreeColorsDomain());
		variableB = new DiscreteInternalVariable<Color>("b",
				new ThreeColorsDomain());
		variableA2 = new DiscreteInternalVariable<Color>("a",
				new ThreeColorsDomain());
	}

	public void testEquals() throws Exception {
		assertTrue(variableA1.equals(variableA1));
		assertTrue(variableA1.equals(variableA2));
		assertFalse(variableA1.equals(variableB));
	}

	public void testHashCode() throws Exception {
		assertTrue(variableA1.hashCode() == variableA1.hashCode());
		assertTrue(variableA1.hashCode() == variableA2.hashCode());
		assertFalse(variableB.hashCode() == variableA2.hashCode());
	}

	public void testAddFunctionDependency() throws Exception {
		DiscreteInternalFunction f1 = new ConflictFunction("f1");
		variableA1.addFunctionDependency(f1);

		assertTrue(variableA1.getFunctionDependencies().contains(f1));
		assertTrue(f1.getVariableDependencies().contains(variableA1));

	}

}
