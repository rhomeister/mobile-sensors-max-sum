package maxSumController;

import junit.framework.TestCase;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.dummy.ThreeColorsDomain;
import maxSumController.io.Color;

public class TestInternalFunction extends TestCase {

	private DiscreteInternalFunction functionA1;

	private DiscreteInternalFunction functionB;

	private DiscreteInternalFunction functionA2;

	@Override
	protected void setUp() throws Exception {
		functionA1 = new DummyFunction("a");
		functionB = new DummyFunction("b");
		functionA2 = new DummyFunction("a");
	}

	public void testEquals() throws Exception {
		assertTrue(functionA1.equals(functionA1));
		assertTrue(functionA1.equals(functionA2));
		assertFalse(functionA1.equals(functionB));
	}

	public void testHashCode() throws Exception {
		assertTrue(functionA1.hashCode() == functionA1.hashCode());
		assertTrue(functionA1.hashCode() == functionA2.hashCode());
		assertFalse(functionB.hashCode() == functionA2.hashCode());
	}

	public void testVariableDependencyRemove() throws Exception {

		functionA1.addVariableDependency(new DiscreteInternalVariable<Color>(
				"v1", new ThreeColorsDomain()));
		functionA1.addVariableDependency(new DiscreteInternalVariable<Color>(
				"v2", new ThreeColorsDomain()));

		assertEquals(2, functionA1.getVariableDependencies().size());

		functionA1.removeVariableDependency("v1");

		assertEquals(1, functionA1.getVariableDependencies().size());
		assertEquals("v2", functionA1.getVariableDependencies().iterator()
				.next().getName());

	}

	public void testAddDependency() throws Exception {
		DiscreteInternalVariable<Color> va1 = new DiscreteInternalVariable<Color>(
				"va1", new ThreeColorsDomain());
		functionA1.addVariableDependency(va1);
		assertTrue(functionA1.getVariableDependencies().contains(va1));
		assertTrue(va1.getFunctionDependencies().contains(functionA1));
	}

}
