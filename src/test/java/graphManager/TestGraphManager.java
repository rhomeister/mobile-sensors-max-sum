package graphManager;

import junit.framework.TestCase;

public class TestGraphManager extends TestCase {

	private GraphManager gm;

	@Override
	protected void setUp() throws Exception {
		gm = new GraphManager(10, 3);

	}

	public void testRandomGraphGeneration() throws Exception {
		int totEdges = 0;
		int repetitions = 1000;
		int expectedAvgconnPerAgent = 0;
		for (int i = 0; i < repetitions; i++) {
			gm.genGraph(false, expectedAvgconnPerAgent, 0);
			totEdges += gm.getNumberOfEdges();
			System.out.println(gm.getNumberOfEdges());
		}
		int avgConnPerAgent = (totEdges / (gm.getNumberOfNodes() * repetitions));
		assertEquals(expectedAvgconnPerAgent, avgConnPerAgent);
		System.out.println("" + avgConnPerAgent);
	}

	public void testRandomConnectedGraphGenerationZero() throws Exception {
		int totEdges = 0;
		int repetitions = 1000;
		int expectedAvgconnPerAgent = 0;
		for (int i = 0; i < repetitions; i++) {
			gm.genConnectedGraph(expectedAvgconnPerAgent);
			System.out.println(gm.getEdgeMap());
			assertEquals(gm.getNumberOfNodes() - 1, gm.getNumberOfEdges());
		}
		int avgConnPerAgent = (totEdges / (gm.getNumberOfNodes() * repetitions));
		System.out.println("" + avgConnPerAgent);
	}

	public void testRandomConnectedGraphGenerationTwo() throws Exception {
		int totEdges = 0;
		int repetitions = 1000;
		int expectedAvgconnPerAgent = 2;
		for (int i = 0; i < repetitions; i++) {
			gm.genConnectedGraph(expectedAvgconnPerAgent);
			totEdges += gm.getNumberOfEdges();
			System.out.println(gm.getEdgeMap());
		}
		int avgConnPerAgent = (totEdges / (gm.getNumberOfNodes() * repetitions));
		assertEquals(expectedAvgconnPerAgent, avgConnPerAgent);
		System.out.println("" + avgConnPerAgent);
	}

}
