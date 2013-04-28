package branchNbound;

import java.util.Collection;

public interface DecisionNode extends Comparable<DecisionNode> {

	double getLowerBound();

	double getUpperBound();

	Collection<? extends DecisionNode> expand();

	boolean isTerminal();

	void setVisited(boolean visited);

	boolean isVisited();

	String debug();

	String debugRecursive();

}
