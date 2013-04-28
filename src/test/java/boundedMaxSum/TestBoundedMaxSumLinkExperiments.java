package boundedMaxSum;

import graphManager.GraphManager;
import junit.framework.TestCase;
import boundedMaxSum.treeformation.oldghs.Edge;
import boundedMaxSum.treeformation.oldghs.GHSTreeFormingAgent;
import boundedMaxSum.treeformation.oldghs.GHSTreeFormingAgent.EdgeState;

public class TestBoundedMaxSumLinkExperiments extends TestCase {

	private BoundedMaxSumLinkExperiments exp;
	private GraphManager gm;

	@Override
	protected void setUp() throws Exception {

		exp = new BoundedMaxSumLinkExperiments();

		gm = new GraphManager();

		gm.readGraph("dataGraph-complete.dat");

	}

	public void testRandomGraph() throws Exception {
		for (double c = 1.5; c < 2.5; c++) {
			for (int n = 3; n < 15; n++) {
				System.out.println("running exp " + n + " " + c);
				BoundedMaxSumResult res = exp.runExperimentRandomGraph(2, n, c);

				System.out.println("printing results");
				if (!Double.isNaN(res.getActualUtility())) {
					System.out.println(res.toCSV());
					System.out.println(res.getActualUtility() + " "
							+ res.getBound() + " " + res.getOptimalUtility());
					assertTrue((res.getActualUtility() + res.getBound()) >= res
							.getOptimalUtility());
				}
			}
		}

	}

	public void testTreeFormation() throws Exception {
		BoundedMaxSumResult result = exp.runExperimentInstance(gm.getEdgeMap(),
				gm.getNumberOfColurs(), gm.getNumberOfNodes(), gm
						.getNumberOfEdges()
						/ gm.getNumberOfNodes());

		double maxWeight = -Double.MAX_VALUE;
		Edge bestEdge = null;
		System.out.println("treecontroller " + exp.treeController);

		for (GHSTreeFormingAgent agent : exp.treeController.agents.values()) {
			for (Edge edge : agent.edgeMap.values()) {
				System.out.println("weight " + edge.getWeight());
				System.out.println("maxWeight " + maxWeight);
				if (maxWeight < edge.getWeight()) {
					maxWeight = edge.getWeight();
					bestEdge = edge;
				}
			}
		}
		System.out.println("best edge " + bestEdge + " maxWeight" + maxWeight);

		assertEquals(EdgeState.REJECTED, bestEdge.state);

		System.out.println("Result = " + result.toCSV());

	}

}
