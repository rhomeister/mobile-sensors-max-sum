package maxSumController.discrete.bb;

import java.util.ArrayList;
import java.util.Collection;

import maxSumController.continuous.linear.Interval;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import branchNbound.DecisionNode;

public class DiscreteMaxSumDecisionNode implements DecisionNode {

	private Log log = LogFactory.getLog(DiscreteMaxSumDecisionNode.class);

	private PartialJointVariableState state;

	private BBDiscreteInternalFunction function;

	private MessageValueFunction messageFunction;

	private double messageLowerBound;

	private double messageUpperBound;

	private boolean visited;

	// private Collection<? extends DecisionNode> children;

	public DiscreteMaxSumDecisionNode(PartialJointVariableState state,
			BBDiscreteInternalFunction function,
			MessageValueFunction messageFunction) {
		this.state = state;
		this.function = function;
		this.messageFunction = messageFunction;

		Interval valueInterval = messageFunction.getValueInterval(state);

		messageLowerBound = valueInterval.getLowerbound();
		messageUpperBound = valueInterval.getUpperbound();

		if (!(messageLowerBound <= messageUpperBound)) {
			System.out.println(messageLowerBound);
			System.out.println(messageUpperBound);
			System.out.println(messageFunction);
		}

		Validate.isTrue(messageLowerBound <= messageUpperBound);
	}

	public Collection<? extends DecisionNode> expand() {
		// if (children == null) {
		Collection<PartialJointVariableState> childrenStates = state
				.getChildren();
		Collection<DiscreteMaxSumDecisionNode> children = new ArrayList<DiscreteMaxSumDecisionNode>();

		for (PartialJointVariableState state : childrenStates) {
			children.add(new DiscreteMaxSumDecisionNode(state, function,
					messageFunction));
		}

		// this.children = children;
		// }

		return children;
	}

	public double getLowerBound() {
		double lowerBound = function.getLowerBound(state) + messageLowerBound;

		if (lowerBound > getUpperBound()) {
			log
					.warn("Lower bound > upper bound. Lower message: "
							+ messageLowerBound + " upper message "
							+ messageUpperBound);
			log.warn("Lower function bound : " + lowerBound
					+ " upper function bound " + getUpperBound());

			System.err.println(state);

			System.err.println(function.getClass());

			function.debug(state);

			throw new IllegalArgumentException();
			// return getUpperBound() * 0.99;
		}

		return lowerBound;
	}

	public PartialJointVariableState getState() {
		return state;
	}

	public double getUpperBound() {
		double upperBound = function.getUpperBound(state);

		Validate.isTrue(!Double.isNaN(upperBound));
		Validate.isTrue(!Double.isInfinite(upperBound));

		return upperBound + messageUpperBound;
	}

	public boolean isTerminal() {
		return state.isFullyDetermined();
	}

	public int compareTo(DecisionNode o) {
		// nodes with a higher upper bound are considered first
		return -Double.compare(getUpperBound(), o.getUpperBound());
	}

	@Override
	public String toString() {
		return function.getName() + " " + state.toString() + " LB: "
				+ getLowerBound() + " UB: " + getUpperBound() + " Visited? "
				+ isVisited();
	}

	@Override
	public String debug() {
		StringBuilder builder = new StringBuilder();

		builder.append(toString() + "\n");

		builder.append("Function bounds LB: " + function.getLowerBound(state)
				+ " UB " + function.getUpperBound(state) + "\n");
		builder.append("Message bounds LB: " + messageLowerBound + " UB "
				+ messageUpperBound + "\n");

		System.out.println("DEBUG");
		function.debug(state);
		System.out.println("/DEBUG");

		return builder.toString();
	}

	@Override
	public String debugRecursive() {
		System.out.println(debug());

		Collection<? extends DecisionNode> expand = expand();

		for (DecisionNode decisionNode : expand) {
			decisionNode.debug();
		}

		return "";

	}

	@Override
	public boolean isVisited() {
		return visited;
	}

	@Override
	public void setVisited(boolean visited) {
		this.visited = visited;
	}

}
