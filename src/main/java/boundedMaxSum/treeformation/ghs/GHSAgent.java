package boundedMaxSum.treeformation.ghs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import maxSumController.discrete.DiscreteInternalVariable;

import boundedMaxSum.treeformation.NodeType;
import boundedMaxSum.treeformation.TreeFormationAgent;
import boundedMaxSum.treeformation.ghs.messages.GHSAcceptMessage;
import boundedMaxSum.treeformation.ghs.messages.GHSChangeRootMessage;
import boundedMaxSum.treeformation.ghs.messages.GHSConnectMessage;
import boundedMaxSum.treeformation.ghs.messages.GHSInitiateMessage;
import boundedMaxSum.treeformation.ghs.messages.GHSRejectMessage;
import boundedMaxSum.treeformation.ghs.messages.GHSReportMessage;
import boundedMaxSum.treeformation.ghs.messages.GHSTestMessage;

public class GHSAgent extends TreeFormationAgent {
	
	protected boolean debug = false; // print your messages as you go along.

	/**
	 * Named consistently with the variables in the paper, for ease of reading.
	 */
	protected GHSNodeState SN;
	protected HashMap<GHSEdge, GHSEdgeState> SE;
	protected GHSFragmentID FN;
	protected int LN;
	protected GHSEdge bestEdge;
	protected GHSWeight bestWeight;
	protected GHSEdge testEdge;
	protected GHSEdge inBranch;
	protected int findCount;

	protected int lastMessagesSent = 0;
	ArrayList<GHSMessage> queue;
	protected HashMap<GHSAgent, GHSEdge> edgesByDest;

	public GHSAgent(int id, String name) {
		super(id, NodeType.NOTDEFINED, name);
		
		edgesByDest = new HashMap<GHSAgent, GHSEdge>();
		
		SN = GHSNodeState.SLEEPING;
		SE = new HashMap<GHSEdge, GHSEdgeState>();
		queue = new ArrayList<GHSMessage>();
	}
	
	public Set<GHSEdge> getEdges() {
		return SE.keySet();
	}
	
	public void setEdgeState(GHSEdge edge, GHSEdgeState state) {
		SE.put(edge, state);
	}
	
	public void wakeup() {
		lastMessagesSent = 0;
		
		GHSEdge m = null;
		GHSWeight minWeight = new GHSWeight(Double.POSITIVE_INFINITY);
		for (GHSEdge edge : SE.keySet()) {
			if (edge.getWeight().compareTo(minWeight) < 0) {
				m = edge;
				minWeight = edge.getWeight();
			}
		}
		if (m == null) {
			// we have no edges, so we don't need to wakeup
			return;
		}

		SE.put(m, GHSEdgeState.BRANCH);
		LN = 0;
		SN = GHSNodeState.FOUND;
		findCount = 0;

		send(new GHSConnectMessage(m, this, 0));
	}

	public boolean processNextMessage() {
		if (!queue.isEmpty()) {
			GHSMessage message = queue.get(0);
			queue.remove(message);
			
			if (debug) {
				System.out.println("Node " + getName() + " processing " + message);
			}
			
			processMessage(message);
			return true; // did have a message to process
		} else {
			return false; // did not have a message to process
		}
	}
	
	protected void processMessage(GHSMessage message) {
		switch (message.getType()) {
			case ACCEPT:
				processAcceptMessage((GHSAcceptMessage) message);
				break;
			case CHANGEROOT:
				processChangeRootMessage((GHSChangeRootMessage) message);
				break;
			case CONNECT:
				processConnectMessage((GHSConnectMessage) message);
				break;
			case INITIATE:
				processInitiateMessage((GHSInitiateMessage) message);
				break;
			case REJECT:
				processRejectMessage((GHSRejectMessage) message);
				break;
			case REPORT:
				processReportMessage((GHSReportMessage) message);
				break;
			case TEST:
				processTestMessage((GHSTestMessage) message);
				break;
			}
	}

	public void addToQueue(GHSMessage message) {
		queue.add(message);
	}

	protected void processConnectMessage(GHSConnectMessage message) {
		GHSEdge j = message.getEdge();
		if (SN == GHSNodeState.SLEEPING) {
			wakeup();
		}

		if (message.getLevel() < LN) {
			SE.put(j, GHSEdgeState.BRANCH);
			send (new GHSInitiateMessage(j, this, LN, FN, SN));
			if (SN == GHSNodeState.FIND) {
				findCount++;
			}
		} else if (SE.get(j) == GHSEdgeState.BASIC) {
			defer(message);
		} else {
			send (new GHSInitiateMessage(j, this, LN+1, j.getFragmentID(), GHSNodeState.FIND));
		}
	}

	protected void processInitiateMessage(GHSInitiateMessage message) {
		GHSEdge j = message.getEdge();
		LN = message.getLevel();
		FN = message.getFragmentID();
		SN = message.getState();
		inBranch = j;
		bestEdge = null;
		bestWeight = new GHSWeight(Double.POSITIVE_INFINITY);

		for (GHSEdge e : getEdgesOfType(GHSEdgeState.BRANCH)) {
			if (!e.equals(j)) {
				send (new GHSInitiateMessage(e, this, message.getLevel(), message.getFragmentID(), message.getState()));
				if (message.getState() == GHSNodeState.FIND) {
					findCount++;
				}
			}
		}

		if (message.getState() == GHSNodeState.FIND) {
			test();
		}
	}

	protected void test() {
		GHSEdge minEdge = null;
		GHSWeight minWeight = new GHSWeight(Double.POSITIVE_INFINITY);
		for (GHSEdge edge : getEdgesOfType(GHSEdgeState.BASIC)) {
			if (edge.getWeight().compareTo(minWeight) < 0) {
				minWeight = edge.getWeight();
				minEdge = edge;
			}
		}

		if (minEdge != null) {
			testEdge = minEdge;
			send (new GHSTestMessage(testEdge, this, LN, FN));
		} else {
			testEdge = null;
			report();
		}
	}

	protected void processTestMessage(GHSTestMessage message) {
		GHSEdge j = message.getEdge();
		if (SN == GHSNodeState.SLEEPING) {
			wakeup();
		} 
		if (message.getLevel() > LN) {
			defer(message);
		} else if (!message.getFragmentID().equals(FN)) {
			send (new GHSAcceptMessage(j, this));
		} else {
			if (SE.get(j) == GHSEdgeState.BASIC) {
				SE.put(j, GHSEdgeState.REJECTED);
			}
			if (testEdge != j) {
				send (new GHSRejectMessage(j, this));
			} else {
				test();
			}
		}
	}

	protected void processAcceptMessage(GHSAcceptMessage message) {
		GHSEdge j = message.getEdge();
		testEdge = null;
		// if j's weight is smaller than best weight
		if (j.getWeight().compareTo(bestWeight) < 0) {
			bestEdge = j;
			bestWeight = j.getWeight();
		}
		report();
	}

	protected void processRejectMessage(GHSRejectMessage message) {
		GHSEdge j = message.getEdge();
		if (SE.get(j) == GHSEdgeState.BASIC) {
			SE.put(j, GHSEdgeState.REJECTED);
		}
		test();
	}

	protected void report() {
		if ((findCount == 0) && (testEdge == null)) {
			SN = GHSNodeState.FOUND;
			send (new GHSReportMessage(inBranch, this, bestWeight));
		}
	}

	protected void processReportMessage(GHSReportMessage message) {
		GHSEdge j = message.getEdge();
		if (j != inBranch) {
			findCount--;
			if (message.getWeight().compareTo(bestWeight) < 0) {
				bestWeight = message.getWeight();
				bestEdge = j;
			}
			report();
		} else if (SN == GHSNodeState.FIND) {
			defer(message);
		} else if (message.getWeight().compareTo(bestWeight) > 0) {
			changeRoot();
		} else if ((message.getWeight().equals(bestWeight)) && (bestWeight.getWeight() == Double.POSITIVE_INFINITY)) {
			// halt
		}
	}

	protected void changeRoot() {
		if (SE.get(bestEdge) == GHSEdgeState.BRANCH) {
			send (new GHSChangeRootMessage(bestEdge, this));
		} else {
			send (new GHSConnectMessage(bestEdge, this, LN));
			SE.put(bestEdge, GHSEdgeState.BRANCH);
		}
	}

	protected void processChangeRootMessage(GHSChangeRootMessage message) {
		changeRoot();
	}


	protected void defer(GHSMessage message) {
		if (debug) {
			System.out.println("Deferring " + message);
		}
		message.incrementCounter();
		if (message.getCounter() > 100) {
			System.err.println("Something went wrong");
		} else {
			addToQueue(message);
		}
	}

	protected void send(GHSMessage message) {
		if (debug) {
			System.out.println(this.getName() + " to " + message.getEdge().getOtherEnd(this).getName() + ":" + message);
		}
		message.getEdge().getOtherEnd(this).addToQueue(message);
		lastMessagesSent++;
	}

	public ArrayList<GHSEdge> getEdgesOfType(GHSEdgeState state) {
		ArrayList<GHSEdge> toReturn = new ArrayList<GHSEdge>();
		for (GHSEdge edge : SE.keySet()) {
			if (SE.get(edge) == state) {
				toReturn.add(edge);
			}
		}
		return toReturn;
	}

	public void addEdge(GHSEdge edge) {
		SE.put(edge, GHSEdgeState.BASIC);
		edgesByDest.put(edge.getOtherEnd(this), edge);
	}

	public GHSNodeState getSN() {
		return SN;
	}

	public HashMap<GHSEdge, GHSEdgeState> getSE() {
		return SE;
	}

	public GHSFragmentID getFN() {
		return FN;
	}

	public int getLN() {
		return LN;
	}

	public GHSEdge getBestEdge() {
		return bestEdge;
	}

	public GHSWeight getBestWeight() {
		return bestWeight;
	}

	public GHSEdge getTestEdge() {
		return testEdge;
	}

	public GHSEdge getInBranch() {
		return inBranch;
	}

	public int getFindCount() {
		return findCount;
	}

	public ArrayList<GHSMessage> getQueue() {
		return queue;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public String getBranchesString() {
		String string = "";
		for (GHSEdge edge : getEdgesOfType(GHSEdgeState.BRANCH)) {
			string += edge;
		}
		return string;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<DiscreteInternalVariable> getKeptVariables() {
		Set<DiscreteInternalVariable> res = new HashSet<DiscreteInternalVariable>();
		for (GHSEdge edge : getEdgesOfType(GHSEdgeState.BRANCH)) {
			res.add(edge.getVariable());
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<DiscreteInternalVariable> getRejectedVariables() {
		Set<DiscreteInternalVariable> res = new HashSet<DiscreteInternalVariable>();
		for (GHSEdge edge : getEdgesOfType(GHSEdgeState.REJECTED)) {
			res.add(edge.getVariable());
		}
		return res;
	}

	@Override
	public double getWeightForEdgeTo(TreeFormationAgent node) {
		return edgesByDest.get(node).getWeight().getWeight();
	}

	@Override
	public boolean hasRejectBranch() {
		return getEdgesOfType(GHSEdgeState.REJECTED).size() > 0;
	}

	public int getLastMessagesSent() {
		return lastMessagesSent;
	}

	public void removeEdge(GHSEdge ledge) {
		SE.remove(ledge);
		HashMap<GHSAgent, GHSAgent> copy = (HashMap<GHSAgent, GHSAgent>) edgesByDest.clone();
		for (GHSAgent a : copy.keySet()) {
			if (edgesByDest.get(a) == ledge) {
				edgesByDest.remove(a);
			}
			
		}
	}

	@Override
	public String getEdgesString() {
		String string = "";
		for (GHSEdge edge : SE.keySet()) {
			string += edge + "=" + SE.get(edge);
		}
		return string;
	}
	
	public int getStorageUsed() {
		// SN, size of SE, FN, LN, bestEdge, bestWeight, testEdge, inBranch, findCount
		return SE.size() + 8;
	}
	
}
