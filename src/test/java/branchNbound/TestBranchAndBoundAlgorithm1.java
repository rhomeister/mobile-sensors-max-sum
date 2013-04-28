package branchNbound;

import junit.framework.TestCase;

public class TestBranchAndBoundAlgorithm1 extends TestCase {

	private BranchAndBoundAlgorithm<DummyDecisionNode> algorithm;

	@Override
	protected void setUp() throws Exception {

		// {Variable SensorMovement0: null, Variable SensorMovement1: (8.0,
		// 4.0), Variable SensorMovement2: null} LB: 2.4823169607992733E-4 UB:
		// 0.006753592494638559 Visited? true
		// {Variable SensorMovement0: (4.0, 0.0), Variable SensorMovement1:
		// (8.0, 4.0), Variable SensorMovement2: null} LB: 3.6650462689300346E-4
		// UB: 3.689533817080355E-4 Visited? false
		// {Variable SensorMovement0: (4.0, 0.0), Variable SensorMovement1:
		// (8.0, 4.0), Variable SensorMovement2: (4.0, 0.0)} LB:
		// 2.2685933825869877E-4 UB: 2.2685933825869877E-4 Visited? false
		// {Variable SensorMovement0: (4.0, 0.0), Variable SensorMovement1:
		// (8.0, 4.0), Variable SensorMovement2: (12.0, 0.0)} LB:
		// 3.6650462689300346E-4 UB: 3.6650462689300346E-4 Visited? false
		// {Variable SensorMovement0: (4.0, 0.0), Variable SensorMovement1:
		// (8.0, 4.0), Variable SensorMovement2: (8.0, 4.0)} LB:
		// 3.689533041711697E-4 UB: 3.689533041711697E-4 Visited? false
		// {Variable SensorMovement0: (0.0, 4.0), Variable SensorMovement1:
		// (8.0, 4.0), Variable SensorMovement2: null} LB: 0.006627067231929097
		// UB: 0.006635319563825483 Visited? false
		// {Variable SensorMovement0: (0.0, 4.0), Variable SensorMovement1:
		// (8.0, 4.0), Variable SensorMovement2: (4.0, 0.0)} LB:
		// 0.006627052625769371 UB: 0.006627052625769371 Visited? false
		// {Variable SensorMovement0: (0.0, 4.0), Variable SensorMovement1:
		// (8.0, 4.0), Variable SensorMovement2: (12.0, 0.0)} LB:
		// 0.006627067231928986 UB: 0.006627067231928986 Visited? false
		// {Variable SensorMovement0: (0.0, 4.0), Variable SensorMovement1:
		// (8.0, 4.0), Variable SensorMovement2: (8.0, 4.0)} LB:
		// 0.006600340916970505 UB: 0.006600340916970505 Visited? false

		DummyDecisionNode a1 = new DummyDecisionNode("a1", -0.1081021152814722,
				-0.1081021152814722);
		DummyDecisionNode a2 = new DummyDecisionNode("a2",
				-0.06860675276461092, -0.06860675276461092);
		DummyDecisionNode a3 = new DummyDecisionNode("a3", 0.25358732387155314,
				0.25358732387155314);
		DummyDecisionNode a = new DummyDecisionNode("a", -0.06860675276461092,
				0.2535873238815799, a1, a2, a3);

		DummyDecisionNode b1 = new DummyDecisionNode("b1",
				-0.06764668388787357, -0.06764668388787357);
		DummyDecisionNode b2 = new DummyDecisionNode("b2",
				-0.10953173318332565, -0.10953173318332565);
		DummyDecisionNode b3 = new DummyDecisionNode("b3", 0.2533525493589951,
				0.2533525493589951);
		DummyDecisionNode b = new DummyDecisionNode("b", -0.06884152727716897,
				0.25335254936902185, b1, b2, b3);

		DummyDecisionNode c1 = new DummyDecisionNode("c1",
				0.004366730346856779, 0.004366730346856779);
		DummyDecisionNode c2 = new DummyDecisionNode("c2",
				0.003171886957561376, 0.003171886957561376);
		DummyDecisionNode c3 = new DummyDecisionNode("c3", 0.2181836964551312,
				0.2181836964551312);
		DummyDecisionNode c = new DummyDecisionNode("c", 0.003171886957561376,
				0.2182336457928606, c1, c2, c3);

		DummyDecisionNode root = new DummyDecisionNode("root",
				-0.10953173318332565, 0.3660561695099089, a, b, c);

		algorithm = new BranchAndBoundAlgorithm<DummyDecisionNode>(root);
	}

	public void testname() throws Exception {
		algorithm.run();

		algorithm.getIncumbent();

		algorithm.debug();
	}
}
