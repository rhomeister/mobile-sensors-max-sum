package maxSumController;

import junit.framework.TestCase;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteMarginalValues;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.dummy.ThreeColorsDomain;
import maxSumController.io.Color;

public class TestMaxSumMessage extends TestCase {

	private VariableToFunctionMessage message11a;

	private VariableToFunctionMessage message11b;

	private VariableToFunctionMessage message12;

	private VariableToFunctionMessage message21;

	@Override
	protected void setUp() throws Exception {
		DiscreteVariable<Color> sender1 = new DiscreteInternalVariable<Color>(
				"v1", new ThreeColorsDomain());
		DiscreteVariable<Color> sender2 = new DiscreteInternalVariable<Color>(
				"v2", new ThreeColorsDomain());

		Function receiver1 = new DummyFunction("f1");
		Function receiver2 = new DummyFunction("f2");

		message11a = new VariableToFunctionMessage(sender1, receiver1,
				new DiscreteMarginalValues<Color>());
		message11b = new VariableToFunctionMessage(sender1, receiver1,
				new DiscreteMarginalValues<Color>());
		message12 = new VariableToFunctionMessage(sender1, receiver2,
				new DiscreteMarginalValues<Color>());
		message21 = new VariableToFunctionMessage(sender2, receiver1,
				new DiscreteMarginalValues<Color>());
	}

	public void testHashCode() throws Exception {
		assertTrue(message11a.hashCode() == message11b.hashCode());
		assertFalse(message11a.hashCode() == message21.hashCode());
		assertFalse(message11a.hashCode() == message12.hashCode());
	}

	public void testEquals() throws Exception {
		assertEquals(message11a, message11b);
		assertEquals(message11b, message11a);
		assertFalse(message11a.equals(message21));
		assertFalse(message11a.equals(message12));
	}
}
