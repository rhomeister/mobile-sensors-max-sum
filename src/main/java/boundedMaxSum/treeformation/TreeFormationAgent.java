package boundedMaxSum.treeformation;

import java.util.Set;

import maxSumController.discrete.DiscreteInternalVariable;

public abstract class TreeFormationAgent {
	
	int id;
	NodeType type;
	String name;
	
	public TreeFormationAgent(int id, NodeType type, String name) {
		this.id = id;
		this.type = type;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public NodeType getType() {
		return type;
	}
	
	public void setType(NodeType type) {
		this.type = type;
	}
	
	public int getID() {
		return id;
	}
	
	public abstract String getBranchesString();
	
	public abstract String getEdgesString();
	
	public abstract double getWeightForEdgeTo(TreeFormationAgent node);

	@SuppressWarnings("unchecked")
	public abstract Set<DiscreteInternalVariable> getKeptVariables();
	
	@SuppressWarnings("unchecked")
	public abstract Set<DiscreteInternalVariable> getRejectedVariables();
	
	/**
	 * @return true iff the node has rejected 1 or more branches.
	 */
	public abstract boolean hasRejectBranch();
}
