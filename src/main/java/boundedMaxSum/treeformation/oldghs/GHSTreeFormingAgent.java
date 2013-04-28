package boundedMaxSum.treeformation.oldghs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import maxSumController.discrete.DiscreteInternalVariable;

import org.apache.commons.lang.Validate;

import boundedMaxSum.treeformation.NodeType;
import boundedMaxSum.treeformation.TreeFormationAgent;

/**
 * Represents an agent capable of forming a minimum spanning tree using the
 * algorithm from; Gallager, R. G., Humblet, P. A. and Spira, P. M. "A
 * distributed algorithm for minimum weight spanning trees" in ACM Transactions
 * on Programming Languages and Systems Vol 5 No 1 pp. 66-77, 1983.
 * 
 * The only changes from the algorithm as presented in the paper are as follows:
 * - All agents run from a single thread i.e. synchronously - Messages are sent
 * via the GHSTreeFormationController, which maintains a global message queue,
 * rather than each agent having their own message queue. This is required to
 * prevent deadlock in a single thread - Two extra messages (COMPLETE and
 * BOUND). COMPLETE messages are propagated through the tree (initiated by the
 * core node with the smallest ID) to inform them that the tree formation has
 * completed. This message also allows them to record their parent (the sender
 * of the message). In response to a COMPLETE message, each node sends a BOUND
 * message to its parent, informing them of the bounds of the function on any
 * deleted edge (REJECTED state). The endpoint of a deleted edge with the
 * smallest ID only takes into account that edge. Bounds are summed at internal
 * nodes and propagated back to the root, who sends out the final bound to the
 * rest of the tree.
 * 
 * @author mw08v
 * 
 */
public class GHSTreeFormingAgent extends TreeFormationAgent {

	public enum NodeState {
		SLEEPING, FIND, FOUND
	};

	public enum EdgeState {
		BASIC, BRANCH, REJECTED
	};

	private boolean debug = false;

	/*
	 * The state of this node.
	 */
	protected NodeState state;

	/*
	 * The outgoing edges from the node.
	 */
	public Map<GHSTreeFormingAgent, Edge> edgeMap;

	protected double fragmentID;

	protected int level;

	protected Edge bestEdge;

	protected double bestWeight;

	protected Edge testEdge;

	protected Edge inBranch;

	protected int findCount;

	protected boolean done = false;

	protected GHSTreeFormationController controller;

	// Used to count outstanding bound messages from children
	protected int boundCount = 0;

	// Used to calculate the bound
	// protected double minBound = 0;

	// protected double maxBound = 0;

	/**
	 * Parent node on algorithm completion.
	 */
	protected GHSTreeFormingAgent parent = null;

	public GHSTreeFormingAgent getParent() {
		return parent;
	}

	public void setParent(GHSTreeFormingAgent parent) {
		this.parent = parent;
	}

	public GHSTreeFormingAgent(int id, GHSTreeFormationController controller,
			String name) {
		super(id, NodeType.NOTDEFINED, name);
		this.controller = controller;
		edgeMap = new HashMap<GHSTreeFormingAgent, Edge>();
		state = NodeState.SLEEPING;
	}

	public void addEdge(Edge edge) {
		edge.setState(EdgeState.BASIC);
		edgeMap.put(edge.endpoint, edge);
	}

	public void wakeup() {
		state = NodeState.FOUND;
		Edge minEdge = null;
		for (Edge edge : edgeMap.values()) {
			if (minEdge == null || edge.getWeight() < minEdge.getWeight()) {
				minEdge = edge;
			}
		}
		// TODO Check correct!
		if (minEdge == null) {
			return;
		}

		Validate.notNull(minEdge);
		minEdge.setState(EdgeState.BRANCH);
		level = 0;

		findCount = 0;
		controller.send(new GHSTreeFormationMessage(
				GHSTreeFormationMessage.MessageType.CONNECT, fragmentID, level,
				state, this, minEdge.getEndpoint()));

	}

	/**
	 * This method is called by GHSTreeFormationController to deliver a message.
	 * 
	 * @param msg
	 * @throws Exception
	 */
	public void processMessage(GHSTreeFormationMessage msg) {
		if (!msg.printed && debug) {
			msg.printed = true;
			msg.setReceiver(this);
			System.out.println(msg);
		}

		switch (msg.type) {
		case ACCEPT:
			processAcceptMessage(msg);
			break;
		case BOUND:
			processBoundMessage(msg);
			break;
		case CHANGE_ROOT:
			changeRoot();
			break;
		case COMPLETE:
			processCompleteMessage(msg);
			break;
		case CONNECT:
			processConnectMessage(msg);
			break;
		case INITIATE:
			processInitiateMessage(msg);
			break;
		case REJECT:
			processRejectMessage(msg);
			break;
		case REPORT:
			processReportMessage(msg);
			break;
		case TEST:
			processTestMessage(msg);
			break;
		}
	}

	private void processBoundMessage(GHSTreeFormationMessage msg) {
		if (msg.sender == parent) {
			// Bound notification from root
			// minBound = msg.minBound;
			// maxBound = msg.maxBound;

			// Propagate to children
			for (Edge edge : edgeMap.values()) {
				if (edge.getState() == EdgeState.BRANCH
						&& edge.endpoint != parent) {
					// controller.send(new GHSTreeFormationMessage(this,
					// edge.endpoint, minBound, maxBound));
					controller.send(new GHSTreeFormationMessage(this,
							edge.endpoint, 0., 0.));
				}
			}
		} else {
			// Bound from child
			// minBound += msg.minBound;
			// maxBound += msg.maxBound;
			boundCount--;
			if (boundCount == 0) {
				if (parent == null) {
					// Root has received all bound messages from child -
					// sent out notifications
					for (Edge edge : edgeMap.values()) {
						if (edge.getState() == EdgeState.BRANCH) {
							// controller
							// .send(new GHSTreeFormationMessage(this,
							// edge.endpoint, minBound,
							// maxBound));
							controller.send(new GHSTreeFormationMessage(this,
									edge.endpoint, 0., 0.));
						}
					}

				} else {
					// Send bound back to parent
					// controller.send(new GHSTreeFormationMessage(this,
					// parent, minBound, maxBound));
					controller.send(new GHSTreeFormationMessage(this, parent,
							0., 0.));
				}
			}
		}
	}

	private void processCompleteMessage(GHSTreeFormationMessage msg) {

		parent = msg.sender;
		done = true;
		for (Edge edge : edgeMap.values()) {
			if (edge.getState() == EdgeState.BRANCH && parent != null
					&& edge.getEndpoint() != parent) {
				controller.send(new GHSTreeFormationMessage(this, edge
						.getEndpoint()));
				boundCount++;
			} else if (edge.getState() == EdgeState.REJECTED
					&& this.getID() < edge.getEndpoint().getID()) {

				// minBound += edge.function.getMinimumBound();
				// maxBound += edge.function.getMaximumBound();
			}
		}

		if (boundCount == 0) {
			// Leaf
			// controller.send(new GHSTreeFormationMessage(this, parent,
			// minBound, maxBound));
			controller.send(new GHSTreeFormationMessage(this, parent, 0., 0.));
		}

	}

	private void processReportMessage(GHSTreeFormationMessage msg) {
		if (edgeMap.get(msg.getSender()) != inBranch) {
			findCount--;
			if (msg.getBestWeight() < bestWeight) {
				bestEdge = edgeMap.get(msg.getSender());
				bestWeight = msg.getBestWeight();
			}
			report();
		} else if (state == NodeState.FIND) {
			controller.send(msg);
		} else if (msg.getBestWeight() > bestWeight) {
			changeRoot();
		} else if (Double.isInfinite(msg.getBestWeight())
				&& Double.isInfinite(bestWeight)) {
			done = true;
			// Work out if we are the root

			if (getID() > msg.getSender().getID()) {
				// We are the root
				for (Edge edge : edgeMap.values()) {
					if (edge.getState() == EdgeState.BRANCH) {
						controller.send(new GHSTreeFormationMessage(this, edge
								.getEndpoint()));
						boundCount++;
					} else if (edge.getState() == EdgeState.REJECTED
							&& this.getID() < edge.getEndpoint().getID()) {
						// minBound += edge.function.getMinimumBound();
						// maxBound += edge.function.getMaximumBound();
					}
				}
			}
		}
	}

	private void processRejectMessage(GHSTreeFormationMessage msg) {
		if (edgeMap.get(msg.getSender()).getState() == EdgeState.BASIC) {
			edgeMap.get(msg.getSender()).setState(EdgeState.REJECTED);
		}

		test();
	}

	private void processAcceptMessage(GHSTreeFormationMessage msg) {
		testEdge = null;
		if (edgeMap.get(msg.getSender()).getWeight() < bestWeight) {
			bestEdge = edgeMap.get(msg.getSender());
			bestWeight = bestEdge.getWeight();
		}
		report();
	}

	private void processTestMessage(GHSTreeFormationMessage msg) {
		if (state == NodeState.SLEEPING)
			wakeup();
		if (msg.getLevel() > level) {

			controller.send(msg);

		} else if (msg.getFragmentID() != fragmentID) {
			controller.send(new GHSTreeFormationMessage(
					GHSTreeFormationMessage.MessageType.ACCEPT, fragmentID,
					level, state, this, msg.getSender()));
		} else {
			if (edgeMap.get(msg.getSender()).getState() == EdgeState.BASIC) {
				edgeMap.get(msg.getSender()).setState(EdgeState.REJECTED);
			}
			if (testEdge != edgeMap.get(msg.getSender())) {
				controller.send(new GHSTreeFormationMessage(
						GHSTreeFormationMessage.MessageType.REJECT, fragmentID,
						level, state, this, msg.getSender()));
			} else {
				test();
			}
		}

	}

	private void processInitiateMessage(GHSTreeFormationMessage msg) {
		level = msg.getLevel();
		fragmentID = msg.getFragmentID();
		state = msg.getNodeState();
		inBranch = edgeMap.get(msg.getSender());
		bestEdge = null;
		bestWeight = Double.POSITIVE_INFINITY;
		for (Edge edge : edgeMap.values()) {
			if (edge.state != EdgeState.BRANCH
					|| edge.getEndpoint() == msg.getSender()) {
				continue;
			}
			controller.send(new GHSTreeFormationMessage(
					GHSTreeFormationMessage.MessageType.INITIATE, fragmentID,
					level, state, this, edge.getEndpoint()));
			if (state == NodeState.FIND) {
				findCount++;
			}
		}
		if (state == NodeState.FIND) {
			test();
		}
	}

	protected void test() {
		Edge minEdge = null;
		for (Edge edge : edgeMap.values()) {
			if (edge.getState() == EdgeState.BASIC
					&& (minEdge == null || edge.getWeight() < minEdge
							.getWeight())) {
				minEdge = edge;
			}
		}
		if (minEdge != null) {
			testEdge = minEdge;
			controller.send(new GHSTreeFormationMessage(
					GHSTreeFormationMessage.MessageType.TEST, fragmentID,
					level, state, this, testEdge.getEndpoint()));
		} else {
			testEdge = null;
			report();
		}
	}

	protected void report() {
		if (findCount == 0 && testEdge == null) {
			state = NodeState.FOUND;
			controller.send(new GHSTreeFormationMessage(this,
					inBranch.endpoint, bestWeight));
		}
	}

	private void processConnectMessage(GHSTreeFormationMessage msg) {
		if (state == NodeState.SLEEPING) {
			wakeup();
		}
		if (msg.level < level) {
			edgeMap.get(msg.getSender()).setState(EdgeState.BRANCH);
			controller.send(new GHSTreeFormationMessage(
					GHSTreeFormationMessage.MessageType.INITIATE, fragmentID,
					level, state, this, msg.getSender()));
			if (state == NodeState.FIND) {
				findCount++;
			}
		} else if (edgeMap.get(msg.getSender()).getState() == EdgeState.BASIC) {
			controller.send(msg);

		} else {
			controller.send(new GHSTreeFormationMessage(
					GHSTreeFormationMessage.MessageType.INITIATE, edgeMap.get(
							msg.getSender()).getWeight(), level + 1,
					NodeState.FIND, this, msg.getSender()));
		}

	}

	protected void changeRoot() {
		if (bestEdge.getState() == EdgeState.BRANCH) {
			controller.send(new GHSTreeFormationMessage(
					GHSTreeFormationMessage.MessageType.CHANGE_ROOT,
					fragmentID, level, state, this, bestEdge.getEndpoint()));
		} else {
			controller.send(new GHSTreeFormationMessage(
					GHSTreeFormationMessage.MessageType.CONNECT, fragmentID,
					level, state, this, bestEdge.getEndpoint()));
			bestEdge.setState(EdgeState.BRANCH);
		}
	}

	public String toString() {
		return "Node " + getName() + (done ? " Done" : "") + " State " + state
				+ " nodeID " + getID();
	}

	public Map<GHSTreeFormingAgent, Edge> getEdgeMap() {
		return edgeMap;
	}

	public void setEdgeMap(Map<GHSTreeFormingAgent, Edge> edgeMap) {
		this.edgeMap = edgeMap;
	}

	public boolean hasRejectBranch() {
		for (Edge edge : edgeMap.values()) {
			if (edge.getState() == GHSTreeFormingAgent.EdgeState.REJECTED) {
				return true;
			}
		}
		return false;
	}

	public Set<DiscreteInternalVariable> getRejectedVariables() {
		Set<DiscreteInternalVariable> res = new HashSet<DiscreteInternalVariable>();
		for (Edge edge : edgeMap.values()) {
			LinkEdge ledge = (LinkEdge) edge;
			if (edge.getState() == GHSTreeFormingAgent.EdgeState.REJECTED) {
				res.add(ledge.variable);
			}
		}
		return res;
	}

	public Set<DiscreteInternalVariable> getKeptVariables() {
		Set<DiscreteInternalVariable> res = new HashSet<DiscreteInternalVariable>();
		for (Edge edge : edgeMap.values()) {
			LinkEdge ledge = (LinkEdge) edge;
			if (edge.getState() == GHSTreeFormingAgent.EdgeState.BRANCH) {
				res.add(ledge.variable);
			}
		}
		return res;
	}

	@Override
	public String getBranchesString() {
		String string = "";
		for (Edge edge : edgeMap.values()) {
			if (edge.getState() == EdgeState.BRANCH) {
				string += edge;
			}
		}
		return string;
	}

	@Override
	public double getWeightForEdgeTo(TreeFormationAgent node) {
		return edgeMap.get(node).getWeight();
	}

	@Override
	public String getEdgesString() {
		// TODO Auto-generated method stub
		return null;
	}

}
