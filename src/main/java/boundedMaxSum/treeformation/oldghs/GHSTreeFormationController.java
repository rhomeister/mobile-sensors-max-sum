package boundedMaxSum.treeformation.oldghs;

import java.util.ArrayDeque;
import java.util.Deque;

import maxSumController.Function;
import maxSumController.Variable;
import maxSumController.discrete.DiscreteInternalVariable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import boundedMaxSum.BoundedDiscreteMaxSumController;
import boundedMaxSum.BoundedInternalFunction;
import boundedMaxSum.LinkBoundedInternalFunction;
import boundedMaxSum.TreeFormationController;
import boundedMaxSum.TreeFormationListener;
import boundedMaxSum.treeformation.NodeType;

/**
 * Controller for agents forming a tree using the GHS algorithm. Used to
 * facilitate message sending using a global message queue. Register a listener
 * with an instance of this class to be notified upon completion.
 * 
 * @author mw08v
 * 
 */
public class GHSTreeFormationController extends TreeFormationController<GHSTreeFormingAgent> {

	protected Log log = LogFactory.getLog(GHSTreeFormationController.class);

	protected Deque<GHSTreeFormationMessage> messageQueue;

	protected int agentsPending;
	
	int totalMessages = 0;

	public GHSTreeFormationController() {
		messageQueue = new ArrayDeque<GHSTreeFormationMessage>();

	}

	public GHSTreeFormationController(BoundedDiscreteMaxSumController controller) {
		messageQueue = new ArrayDeque<GHSTreeFormationMessage>();

		// adding agents
		int agentid = 1;
		for (DiscreteInternalVariable<?> var : controller
				.getInternalVariables()) {
			GHSTreeFormingAgent node = new GHSTreeFormingAgent(agentid, this,
					var.getName());
			node.setType(NodeType.VARIABLE);
			this.addAgent(node);
			agentid++;
		}

		for (BoundedInternalFunction function : controller
				.getInternalFunctions()) {
			GHSTreeFormingAgent fAgent = new GHSTreeFormingAgent(agentid, this,
					function.getName());
			fAgent.setType(NodeType.FUNCTION);
			this.addAgent(fAgent);
			agentid++;
		}

		// adding links

		for (DiscreteInternalVariable<?> var : controller
				.getInternalVariables()) {
			GHSTreeFormingAgent node = getAgent(var.getName());
			for (Function function : var.getFunctionDependencies()) {
				GHSTreeFormingAgent secondAgent = getAgent(function.getName());
				LinkBoundedInternalFunction linkFunction = (LinkBoundedInternalFunction) function;
				node
						.addEdge(new LinkEdge(secondAgent, node, linkFunction,
								var));
			}
		}

		for (Function function : controller.getInternalFunctions()) {
			GHSTreeFormingAgent node = getAgent(function.getName());
			LinkBoundedInternalFunction linkFunction = (LinkBoundedInternalFunction) function;
			for (Variable<?,?> var : linkFunction.getVariableDependencies()) {
				GHSTreeFormingAgent secondAgent = getAgent(var.getName());
				node.addEdge(new LinkEdge(secondAgent, node, linkFunction,
						(DiscreteInternalVariable<?>) var));
			}
		}

	}

	@Override
	public void execute() throws Exception {

		// Wake everyone up
		for (GHSTreeFormingAgent agent : agents.values()) {
			agent.wakeup();
		}
		totalMessages = 0;
		// Process all the messages while there still are any
		while (!messageQueue.isEmpty()) {
			GHSTreeFormationMessage msg = messageQueue.removeFirst();
			msg.counter++;
			if (!msg.printed)
				totalMessages++;
			if (msg.counter > 100) {
				throw new Exception("Message cycling");
			}
			msg.receiver.processMessage(msg);
		}

		log.debug("Total messages to form tree and communicate bounds: "
				+ totalMessages);

		for (TreeFormationListener listener : listeners) {
			listener.treeFormationComplete();
		}

		log.info("Tree formation complete");

	}

	public static void main(String[] args) {
		final GHSTreeFormationController controller = new GHSTreeFormationController();
		GHSTreeFormingAgent node1 = new GHSTreeFormingAgent(1, controller,
				"" + 1);
		GHSTreeFormingAgent node2 = new GHSTreeFormingAgent(2, controller,
				"" + 2);
		GHSTreeFormingAgent node3 = new GHSTreeFormingAgent(3, controller,
				"" + 3);
		GHSTreeFormingAgent node4 = new GHSTreeFormingAgent(4, controller,
				"" + 4);
		GHSTreeFormingAgent node5 = new GHSTreeFormingAgent(5, controller,
				"" + 5);
		controller.addAgent(node1);
		controller.addAgent(node2);
		controller.addAgent(node3);
		controller.addAgent(node4);
		controller.addAgent(node5);

		node1.addEdge(new Edge(node2, node1, -2));
		node1.addEdge(new Edge(node5, node1, -1));
		node2.addEdge(new Edge(node1, node2, -2));
		node2.addEdge(new Edge(node5, node2, -3));
		node2.addEdge(new Edge(node4, node2, -7));
		node2.addEdge(new Edge(node3, node2, -6));
		node3.addEdge(new Edge(node2, node3, -6));
		node3.addEdge(new Edge(node4, node3, -5));
		node4.addEdge(new Edge(node3, node4, -5));
		node4.addEdge(new Edge(node2, node4, -7));
		node4.addEdge(new Edge(node5, node4, -4));
		node5.addEdge(new Edge(node4, node5, -4));
		node5.addEdge(new Edge(node2, node5, -3));
		node5.addEdge(new Edge(node1, node5, -1));

		controller.addListener(new TreeFormationListener() {

			@Override
			public void treeFormationComplete() {
				controller.printEdges();

			}

		});

		try {
			controller.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void send(GHSTreeFormationMessage msg) {
		messageQueue.addLast(msg);
	}

	@Override
	public int getTotalMessages() {
		return totalMessages;
	}

	@Override
	public int getStorageUsed() {
		// TODO Auto-generated method stub
		return 0;
	}
}