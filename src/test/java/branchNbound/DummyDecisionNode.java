package branchNbound;

import java.util.ArrayList;
import java.util.Collection;

public class DummyDecisionNode implements DecisionNode {

	private Collection<DecisionNode> children = new ArrayList<DecisionNode>();

	private double upperBound;

	private double lowerBound;

	private DummyState state;

	private boolean visited;

	public DummyDecisionNode(String name, double lowerBound, double upperBound,
			DecisionNode... children) {
		for (DecisionNode state : children)
			this.children.add(state);

		this.state = new DummyState(name);
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	public Collection<DecisionNode> expand() {
		return children;
	}

	public double getLowerBound() {
		return lowerBound;
	}

	public DummyState getState() {
		return state;
	}

	public double getUpperBound() {
		return upperBound;
	}

	public boolean isTerminal() {
		return children.isEmpty();
	}

	public int compareTo(DecisionNode o) {
		return -Double.compare(upperBound, o.getUpperBound());
	}

	@Override
	public String toString() {
		return state.getName() + " " + lowerBound + " " + upperBound
				+ " Visited? " + isVisited();
	}

	@Override
	public boolean isVisited() {
		return visited;
	}

	@Override
	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	@Override
	public String debug() {
		return toString();
	}

	@Override
	public String debugRecursive() {
		return "";
	}
}