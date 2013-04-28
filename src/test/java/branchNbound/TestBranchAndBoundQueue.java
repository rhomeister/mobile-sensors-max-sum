package branchNbound;

import junit.framework.TestCase;

public class TestBranchAndBoundQueue extends TestCase {

	private BranchAndBoundQueue<DummyDecisionNode> queue;

	@Override
	protected void setUp() throws Exception {
		queue = new BranchAndBoundQueue<DummyDecisionNode>();
	}

	public void testHighLow() throws Exception {
		queue.enqueue(new DummyDecisionNode("a", 10, 30));
		queue.enqueue(new DummyDecisionNode("b", 2, 9));

		assertEquals(1, queue.size());
		assertEquals("a", queue.poll().getState().getName());
	}

	public void testSameTwice() throws Exception {
		queue.enqueue(new DummyDecisionNode("a", 1, 9));
		queue.enqueue(new DummyDecisionNode("b", 1, 9));

		assertEquals(2, queue.size());
	}

	public void testTightBounds() throws Exception {
		queue.enqueue(new DummyDecisionNode("a", 2, 2));
		DummyDecisionNode b = new DummyDecisionNode("b", 1, 5);
		queue.enqueue(b);
		DummyDecisionNode c = new DummyDecisionNode("c", 4, 4);
		queue.enqueue(c);

		assertEquals(2, queue.size());
		assertTrue(queue.contains(b));
		assertTrue(queue.contains(c));
	}

	public void testLowerEqualsUpper() throws Exception {
		queue.enqueue(new DummyDecisionNode("a", 1, 2));
		queue.enqueue(new DummyDecisionNode("b", 2, 3));

		assertEquals(1, queue.size());
		assertEquals("b", queue.poll().getState().getName());
	}

	public void testLowerEqualsUpperReverse() throws Exception {
		queue.enqueue(new DummyDecisionNode("b", 2, 3));
		queue.enqueue(new DummyDecisionNode("a", 1, 2));

		assertEquals(1, queue.size());
		assertEquals("b", queue.poll().getState().getName());
	}

	public void testLowHigh() throws Exception {
		queue.enqueue(new DummyDecisionNode("b", 2, 9));
		DummyDecisionNode dummyDecisionNode = new DummyDecisionNode("a", 10, 30);
		queue.enqueue(dummyDecisionNode);

		assertEquals(1, queue.size());
		assertEquals("a", queue.poll().getState().getName());
	}

	public void testMultipleLow() throws Exception {
		queue.enqueue(new DummyDecisionNode("a1", 1, 8));
		queue.enqueue(new DummyDecisionNode("a1", 1, 2));
		queue.enqueue(new DummyDecisionNode("a2", 1, 2));
		queue.enqueue(new DummyDecisionNode("a3", 1, 2));
		queue.enqueue(new DummyDecisionNode("b", 2, 3));
		assertEquals(2, queue.size());
		queue.enqueue(new DummyDecisionNode("a4", 1, 2));
		queue.enqueue(new DummyDecisionNode("a5", 1, 2));

		assertEquals(2, queue.size());
		assertEquals(2.0, queue.getMaxLowerBound());
		assertEquals(8.0, queue.getMaxUpperBound());
		assertEquals("a1", queue.poll().getState().getName());
		assertEquals("b", queue.poll().getState().getName());
	}

	public void testInfinityBounds() throws Exception {

		DummyDecisionNode a1 = new DummyDecisionNode("a1", 2, 4);
		DummyDecisionNode a2 = new DummyDecisionNode("a2",
				Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		DummyDecisionNode a3 = new DummyDecisionNode("a3",
				Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

		queue.enqueue(a1);
		queue.enqueue(a2);
		queue.enqueue(a3);
		assertEquals(3, queue.size());
	}

	public void testInfinityBoundsReverse() throws Exception {
		DummyDecisionNode a1 = new DummyDecisionNode("a1", 2, 2);
		DummyDecisionNode a2 = new DummyDecisionNode("a2",
				Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

		queue.enqueue(a2);
		queue.enqueue(a1);
		assertEquals(2, queue.size());
	}

	public void testSimpleScenario() throws Exception {
		DummyDecisionNode a1 = new DummyDecisionNode("a1", 2, 2);
		DummyDecisionNode a2 = new DummyDecisionNode("a2", 4, 4);

		DummyDecisionNode a = new DummyDecisionNode("a", 2, 4, a1, a2);

		DummyDecisionNode b1 = new DummyDecisionNode("b1", 6, 6);
		DummyDecisionNode b2 = new DummyDecisionNode("b2", 8, 8);
		DummyDecisionNode b = new DummyDecisionNode("b", 6, 8, b1, b2);

		DummyDecisionNode root = new DummyDecisionNode("root", 1, 9, a, b);

		queue.enqueue(root);
		assertEquals(root, queue.poll());

		queue.enqueue(b);
		queue.enqueue(a);
		assertEquals(1, queue.size());
		assertEquals(b, queue.poll());

		queue.enqueue(b1);
		queue.enqueue(b2);
		assertEquals(1, queue.size());
		assertEquals(b2, queue.poll());
	}

	public void testSimpleScenarioTight() throws Exception {
		DummyDecisionNode a1 = new DummyDecisionNode("a1", 2, 2);
		DummyDecisionNode a2 = new DummyDecisionNode("a2", 2, 2);

		DummyDecisionNode a = new DummyDecisionNode("a", 2, 2, a1, a2);

		DummyDecisionNode b1 = new DummyDecisionNode("b1", 2, 2);
		DummyDecisionNode b2 = new DummyDecisionNode("b2", 2, 2);
		DummyDecisionNode b = new DummyDecisionNode("b", 2, 2, b1, b2);

		DummyDecisionNode root = new DummyDecisionNode("root", 2, 2, a, b);

		queue.enqueue(root);
		assertEquals(root, queue.poll());

		queue.enqueue(b);
		queue.enqueue(a);
		assertEquals(1, queue.size());
		assertEquals(b, queue.poll());

		queue.enqueue(b1);
		queue.enqueue(b2);
		assertEquals(1, queue.size());
		assertEquals(b1, queue.poll());
	}

	public void testSimpleScenario2() throws Exception {
		DummyDecisionNode a1 = new DummyDecisionNode("a1", 0.1480224858332707,
				0.1480224858332707);
		DummyDecisionNode a2 = new DummyDecisionNode("a2", 0.1480224858332707,
				0.1480224858332707);
		DummyDecisionNode a3 = new DummyDecisionNode("a3", 0.1480224858332707,
				0.1480224858332707);
		DummyDecisionNode a = new DummyDecisionNode("a", 0.1480224858332707,
				0.1480224858332707, a1, a2, a3);

		DummyDecisionNode b1 = new DummyDecisionNode("b1", 0.1480224858332707,
				0.1480224858332707);
		DummyDecisionNode b2 = new DummyDecisionNode("b2", 0.1480224858332707,
				0.1480224858332707);
		DummyDecisionNode b3 = new DummyDecisionNode("b3", 0.1480224858332707,
				0.1480224858332707);
		DummyDecisionNode b = new DummyDecisionNode("b", 0.040854039931510266,
				0.1480224858332707, b1, b2, b3);

		DummyDecisionNode c1 = new DummyDecisionNode("c1",
				1.9993244268312615E-4, 1.9993244268312615E-4);
		DummyDecisionNode c2 = new DummyDecisionNode("c2", 0.04089013834883981,
				0.04089013834883981);
		DummyDecisionNode c3 = new DummyDecisionNode("c3", 0.1480224561497182,
				0.1480224561497182);
		DummyDecisionNode c = new DummyDecisionNode("c", 0.04089013834883981,
				0.14802245615974496, c1, c2, c3);

		DummyDecisionNode root = new DummyDecisionNode("root",
				0.04085403993151038, 0.1480224858332707, a, b, c);
		
		queue.enqueue(root);
		
		queue.poll();
		
		queue.enqueue(a);
		queue.enqueue(b);
		queue.enqueue(c);
		System.out.println(queue);
		
		queue.poll();
		queue.enqueue(a1);
		System.out.println(queue);
	}
}
