package boundedMaxSum.treeformation.ghs;

import java.util.HashMap;

import maxSumController.Function;
import maxSumController.discrete.DiscreteInternalVariable;
import boundedMaxSum.BoundedDiscreteMaxSumController;
import boundedMaxSum.BoundedInternalFunction;
import boundedMaxSum.LinkBoundedInternalFunction;
import boundedMaxSum.TreeFormationController;
import boundedMaxSum.treeformation.NodeType;

public class GHSController extends TreeFormationController<GHSAgent> {

	protected int agentid;

	protected HashMap<LinkBoundedInternalFunction, HashMap<DiscreteInternalVariable<?>, GHSLinkEdge>> ledgeMap;

	public GHSController() {
		// mainly for testing. do nothing
	}

	public GHSController(BoundedDiscreteMaxSumController controller) {
		ledgeMap = new HashMap<LinkBoundedInternalFunction, HashMap<DiscreteInternalVariable<?>,GHSLinkEdge>>();

		// add agents
		agentid = 1;
		for (DiscreteInternalVariable<?> var : controller
				.getInternalVariables()) {
			GHSAgent agent = new GHSAgent(agentid, var.getName());
			agent.setType(NodeType.VARIABLE);
			this.addAgent(agent);
			agentid++;
		}

		for (BoundedInternalFunction function : controller
				.getInternalFunctions()) {
			GHSAgent agent = new GHSAgent(agentid, function.getName());
			agent.setType(NodeType.FUNCTION);
			this.addAgent(agent);
			agentid++;
		}

		//add links
		for (DiscreteInternalVariable<?> var : controller
				.getInternalVariables()) {
			for (Function function : var.getFunctionDependencies()) {
				addLinkEdge(var, (LinkBoundedInternalFunction) function);
			}
		}
	}

	protected void addLinkEdge(DiscreteInternalVariable<?> var, LinkBoundedInternalFunction function) {
		GHSAgent one = getAgent(var.getName());
		GHSAgent two = getAgent(function.getName());
		GHSLinkEdge ledge = new GHSLinkEdge(one, two, function, var);
		if (ledgeMap.get(function) == null) {
			ledgeMap.put(function, new HashMap<DiscreteInternalVariable<?>, GHSLinkEdge>());
		}
		ledgeMap.get(function).put(var, ledge);
		one.addEdge(ledge);
		two.addEdge(ledge);
	}

	public void removeLinkEdge(DiscreteInternalVariable<?> var, LinkBoundedInternalFunction function) {
		GHSAgent one = getAgent(var.getName());
		GHSAgent two = getAgent(function.getName());
		GHSLinkEdge ledge = ledgeMap.get(function).get(var);
		one.removeEdge(ledge);
		two.removeEdge(ledge);
		ledgeMap.get(function).remove(var);
	}


	/**
	 * Tests the implementation 
	 * @param args
	 */
	public static void main(String[] args) {
		final GHSController controller = new GHSController();
		GHSAgent node1 = new GHSAgent(1, "1");
		GHSAgent node2 = new GHSAgent(2, "2");
		GHSAgent node3 = new GHSAgent(3, "3");
		GHSAgent node4 = new GHSAgent(4, "4");
		GHSAgent node5 = new GHSAgent(5, "5");
		controller.addAgent(node1);
		controller.addAgent(node2);
		controller.addAgent(node3);
		controller.addAgent(node4);
		controller.addAgent(node5);

		addEdge(node1, node2, -2.0);
		addEdge(node5, node1, -1.0);
		addEdge(node5, node2, -3.0);
		addEdge(node4, node2, -6.0);
		addEdge(node3, node2, -7.0);
		addEdge(node4, node3, -5.0);
		addEdge(node5, node4, -4.0);

		controller.execute();

		System.out.println(controller.toString());
	}

	private static void addEdge(GHSAgent one, GHSAgent two, double weight) {
		GHSEdge edge = new GHSEdge(one, two, new GHSWeight(new GHSAgent[]{one, two}, weight));
		one.addEdge(edge);
		two.addEdge(edge);
	}

	public void execute() {
		boolean dothis = true;
		for (GHSAgent agent : getAgents()) {
			if (dothis) {
				agent.wakeup();
				dothis = false;
			}

		}

		boolean allDone = false;
		while (!allDone) {
			allDone = true;
			for (GHSAgent agent : getAgents()) {
				allDone &= !agent.processNextMessage();
				allDone &= !agent.processNextMessage();
			}
		}
		
		// testing. if this prints, we have a problem.
		for (GHSAgent a : getAgents()) {
			if (!a.getEdgesOfType(GHSEdgeState.BASIC).isEmpty()) {
				//				System.out.println(a.getEdgesOfType(GHSEdgeState.BASIC));
			}
		}

	}

	@Override
	public int getTotalMessages() {
		int total = 0;
		for (GHSAgent agent : getAgents()) {
			total += agent.getLastMessagesSent();
		}
		return total;
	}
	
	@Override
	public int getStorageUsed() {
		int total = 0;
		for (GHSAgent agent : getAgents()) {
			total += agent.getStorageUsed();
		}
		return total;
	}

}
