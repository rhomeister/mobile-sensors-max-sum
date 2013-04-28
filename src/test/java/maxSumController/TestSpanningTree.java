package maxSumController;

import java.util.Collection;

public class TestSpanningTree extends SimpleFactorGraphTestCase {

	public void testStartSpanningTree() throws Exception {
		maxSumController.startSpanningTreeAlgorithm();

		assertTrue(receivedMessageFrom(fa1, va1));
		assertTrue(receivedMessageFrom(fa1, va2));
		assertEquals(2, getMessagesForNode(fa1).size());

		assertTrue(receivedMessageFrom(fa2, va1));
		assertTrue(receivedMessageFrom(fa2, va2));
		assertTrue(receivedMessageFrom(fa2, va3));
		assertEquals(3, getMessagesForNode(fa2).size());

		assertEquals(2, getMessagesForNode(fa3).size());
		assertEquals(0, getMessagesForNode(va1).size());
		assertEquals(0, getMessagesForNode(va2).size());

		assertTrue(maxSumController.getInternalNode(va1).isRoot());

		maxSumController.calculateNewOutgoingMessages();
		assertEquals(maxSumController.getInternalNode(fa1).getRoot(), va1);
		assertEquals(maxSumController.getInternalNode(fa1).getParent(), va1);
		assertEquals(maxSumController.getInternalNode(fa2).getRoot(), va1);
		assertEquals(maxSumController.getInternalNode(fa2).getParent(), va1);
		assertEquals(maxSumController.getInternalNode(fa3).getRoot(), va1);
		assertEquals(maxSumController.getInternalNode(fa3).getParent(), va3);

	}

	private boolean receivedMessageFrom(FactorGraphNode receiver,
			FactorGraphNode sender) {
		Collection<SpanningTreeMessage> messagesForNode = getMessagesForNode(receiver);

		for (SpanningTreeMessage spanningTreeMessage : messagesForNode) {
			assertTrue(receiver + " " + spanningTreeMessage.getReceiver(),
					spanningTreeMessage.getReceiver().equals(receiver));

			if (spanningTreeMessage.getSender().equals(sender)) {
				return true;
			}
		}

		return false;
	}

	private Collection<SpanningTreeMessage> getMessagesForNode(
			FactorGraphNode receiver) {
		return getMessagesForNode(receiver, SpanningTreeMessage.class);
	}

	public void testIteration() throws Exception {
		maxSumController.startSpanningTreeAlgorithm();

		for (int i = 0; i < 10; i++) {
			maxSumController.calculateNewOutgoingMessages();
		}

		for (InternalNodeRepresentation node : maxSumController
				.getInternalNodes()) {
			checkNode(node);
		}
	}

	private void checkNode(InternalNodeRepresentation node) {
		InternalNodeRepresentation current = node;
		FactorGraphNode parent = node.getParent();

		while (true) {
			if (parent == null) {
				// parent is null, this node should be root
				assertEquals(va1, current.getRepresentedNode());
				break;
			} else {
				// this node is not root, should not be va1
				assertFalse(current.getRepresentedNode().equals(va1));
			}

			// check whether all active neighbours have this node as their
			// parent
			for (FactorGraphNode neighbour : current.getActiveNeighbours()) {
				InternalNodeRepresentation neighbourNode = maxSumController
						.getInternalNode(neighbour);

				assertEquals(current.getRepresentedNode(), neighbourNode
						.getParent());
			}

			current = maxSumController.getInternalNode(parent);

			parent = current.getParent();
		}
	}

	private void print(InternalNodeRepresentation node) {
		System.out.println("NODE " + node.getRepresentedNode().getName());
		System.out.println("parent " + node.getParent());
		System.out.println("neighbours " + node.getActiveNeighbours());
	}
}
