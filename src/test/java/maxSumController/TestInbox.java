package maxSumController;

import java.util.Collection;

import junit.framework.TestCase;
import maxSumController.communication.Inbox;
import maxSumController.communication.MaxSumMessage;
import maxSumController.communication.Message;
import maxSumController.communication.RetentionPolicy;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteMarginalValues;
import maxSumController.io.Color;
import maxSumController.io.ColorDomain;

public class TestInbox extends TestCase {

	private Inbox inbox;
	private InternalFunction function;
	private DiscreteInternalVariable<Color> variable;

	@Override
	protected void setUp() throws Exception {
		inbox = new Inbox();
		inbox.setRetentionPolicy(MaxSumMessage.class, RetentionPolicy.REPLACE);

		function = new DummyFunction("dummy_f");
		variable = new DiscreteInternalVariable<Color>("dummy_v",
				new ColorDomain());
	}

	public void testReplaceRetentionPolicy() throws Exception {
		DiscreteMarginalValues<Color> content = new DiscreteMarginalValues<Color>();
		content.getValues().put(Color.RED, 1.0);

		Message message1 = new FunctionToVariableMessage(function, variable,
				content);
		Message message2 = new FunctionToVariableMessage(function, variable,
				new DiscreteMarginalValues<DiscreteVariableState>());

		inbox.deliver(message1);

		Collection<MaxSumMessage> messages = inbox
				.getMessages(MaxSumMessage.class);

		assertEquals(1, messages.size());
		assertSame(message1, messages.iterator().next());

		inbox.deliver(message2);
		messages = inbox.getMessages(MaxSumMessage.class);
		assertEquals(1, messages.size());
		assertSame(message2, messages.iterator().next());
	}

	public void testDefaulRetentionPolicy() throws Exception {
		Message message1 = new SpanningTreeMessage(function, variable, variable);
		Message message2 = new SpanningTreeMessage(function, variable, null);

		inbox.deliver(message1);

		Collection<SpanningTreeMessage> messages = inbox
				.getMessages(SpanningTreeMessage.class);

		assertEquals(1, messages.size());
		assertSame(message1, messages.iterator().next());

		inbox.deliver(message2);
		messages = inbox.getMessages(SpanningTreeMessage.class);
		assertEquals(2, messages.size());
	}
}
