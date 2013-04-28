package branchNbound;

import junit.framework.TestCase;

public class TestBranchAndBoundAlgorithm extends TestCase {

	private BranchAndBoundAlgorithm<DummyDecisionNode> algorithm;

	@Override
	protected void setUp() throws Exception {
				
//		{Variable SensorMovement0: null, Variable SensorMovement1: (8.0, 4.0), Variable SensorMovement2: null} LB: 2.4823169607992733E-4 UB: 0.006753592494638559 Visited? true
//				   {Variable SensorMovement0: (4.0, 0.0), Variable SensorMovement1: (8.0, 4.0), Variable SensorMovement2: null} LB: 3.6650462689300346E-4 UB: 3.689533817080355E-4 Visited? false
//				      {Variable SensorMovement0: (4.0, 0.0), Variable SensorMovement1: (8.0, 4.0), Variable SensorMovement2: (4.0, 0.0)} LB: 2.2685933825869877E-4 UB: 2.2685933825869877E-4 Visited? false
//				      {Variable SensorMovement0: (4.0, 0.0), Variable SensorMovement1: (8.0, 4.0), Variable SensorMovement2: (12.0, 0.0)} LB: 3.6650462689300346E-4 UB: 3.6650462689300346E-4 Visited? false
//				      {Variable SensorMovement0: (4.0, 0.0), Variable SensorMovement1: (8.0, 4.0), Variable SensorMovement2: (8.0, 4.0)} LB: 3.689533041711697E-4 UB: 3.689533041711697E-4 Visited? false
//				   {Variable SensorMovement0: (0.0, 4.0), Variable SensorMovement1: (8.0, 4.0), Variable SensorMovement2: null} LB: 0.006627067231929097 UB: 0.006635319563825483 Visited? false
//				      {Variable SensorMovement0: (0.0, 4.0), Variable SensorMovement1: (8.0, 4.0), Variable SensorMovement2: (4.0, 0.0)} LB: 0.006627052625769371 UB: 0.006627052625769371 Visited? false
//				      {Variable SensorMovement0: (0.0, 4.0), Variable SensorMovement1: (8.0, 4.0), Variable SensorMovement2: (12.0, 0.0)} LB: 0.006627067231928986 UB: 0.006627067231928986 Visited? false
//				      {Variable SensorMovement0: (0.0, 4.0), Variable SensorMovement1: (8.0, 4.0), Variable SensorMovement2: (8.0, 4.0)} LB: 0.006600340916970505 UB: 0.006600340916970505 Visited? false

		
		DummyDecisionNode a1 = new DummyDecisionNode("a1",
				2.2685933825869877E-4, 2.2685933825869877E-4);
		DummyDecisionNode a2 = new DummyDecisionNode("a2",
				3.6650462689300346E-4, 3.6650462689300346E-4);
		DummyDecisionNode a3 = new DummyDecisionNode("a3",
				3.689533041711697E-4, 3.689533041711697E-4);

		DummyDecisionNode a = new DummyDecisionNode("a", 3.6650462689300346E-4,
				3.689533817080355E-4, a1, a2, a3);

		DummyDecisionNode b1 = new DummyDecisionNode("b", 0.006627052625769371,
				0.006627052625769371);
		DummyDecisionNode b2 = new DummyDecisionNode("b2",
				0.006627067231928986, 0.006627067231928986);
		DummyDecisionNode b3 = new DummyDecisionNode("b3",
				0.006600340916970505, 0.006600340916970505);

		DummyDecisionNode b = new DummyDecisionNode("b", 0.006627067231929097,
				0.006635319563825483, b1, b2, b3);

		DummyDecisionNode root = new DummyDecisionNode("root",
				2.4823169607992733E-4, 0.006753592494638559, a, b);

		algorithm = new BranchAndBoundAlgorithm<DummyDecisionNode>(root);
	}

	public void testname() throws Exception {
		algorithm.run();
		
		algorithm.getIncumbent();
		
		algorithm.debug();
	}
}
