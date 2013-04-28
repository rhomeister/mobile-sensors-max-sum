package boundedMaxSum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import boundedMaxSum.treeformation.NodeType;
import boundedMaxSum.treeformation.TreeFormationAgent;

import maxSumController.DiscreteInternalFunction;
import maxSumController.discrete.DiscreteInternalVariable;

@SuppressWarnings("unchecked")
public abstract class TreeFormationController<A extends TreeFormationAgent> implements TreeFormationAlgorithm {

	protected Map<String, A> agents;

	public abstract void execute() throws Exception;

	protected List<TreeFormationListener> listeners;

	public TreeFormationController() {
		agents = new HashMap<String, A>();
		listeners = new ArrayList<TreeFormationListener>();
	}

	public void addAgent(A agent) {
		agents.put(agent.getName(), agent);
	}

	public void addListener(TreeFormationListener listener) {
		listeners.add(listener);
	}

	public Collection<A> getAgents() {
		return agents.values();
	}

	public A getAgent(String id) {
		return agents.get(id);
	}

	public void removeAgent(String id) {
		agents.remove(id);
	}

	/**
	 * Utility method to print out edges in the resulting tree (edges marked as
	 * BRANCH)
	 */
	public void printEdges() {
		for (A node : agents.values()) {
			System.out.println("Edges from Node " + node.getID());
			System.out.println(node.getBranchesString());
			System.out.println();

		}
	}

	public void printAllEdges() {
		for (A node : agents.values()) {
			System.out.println("Edges from Node " + node.getID());
			System.out.println(node.getEdgesString());
			System.out.println();

		}
	}

	@Override
	/*
	 * public String toString() { String res = ""; for (GHSTreeFormingAgent node
	 * : agents.values()) { res = res + " Edges from Node " + node.nodeID; for
	 * (Edge edge : node.getEdgeMap().values()) { res = res + " " +
	 * edge.getEndpoint(); } res = res + "\n";
	 * 
	 * } return res; }
	 */
	public String toString() {
		String res = "";
		for (A node : agents.values()) {
			res = res + " Edges from Node " + node.getID();
			res = res + " " + node.getBranchesString();
			res = res + "\n";

		}
		return res;
	}

	public A getFunctionAgent(String name) {
		for (A agent : agents.values()) {
			if ((agent.getType() == NodeType.FUNCTION) && (name.equals(agent.getName()))) {
				return agent;
			}
		}
		return null;
	}

	public A getVariableAgent(String name) {
		for (A agent : agents.values()) {
			if ((agent.getType() == NodeType.VARIABLE) && (name.equals(agent.getName()))) {
				return agent;
			}
		}
		return null;
	}

	public boolean hasRejectBranch(DiscreteInternalFunction function) {
		A node = getFunctionAgent(function.getName());
		if (node != null) {
			return node.hasRejectBranch();
		} else {
			return false;
		}
	}

	public Set<DiscreteInternalVariable> getRejectedVariables(
			DiscreteInternalFunction function) {
		A node = getFunctionAgent(function.getName());
		return node.getRejectedVariables();
	}

	public double getWeight(DiscreteInternalFunction function,
			DiscreteInternalVariable var) {
		A node = getFunctionAgent(function.getName());
		A varNode = getVariableAgent(var.getName());
		return Math.abs(node.getWeightForEdgeTo(varNode));
	}

	public Set<DiscreteInternalVariable> getKeptVariables(
			DiscreteInternalFunction function) {
		A node = getFunctionAgent(function.getName());
		return node.getKeptVariables();
	}

	public abstract int getTotalMessages();

	public abstract int getStorageUsed();

}