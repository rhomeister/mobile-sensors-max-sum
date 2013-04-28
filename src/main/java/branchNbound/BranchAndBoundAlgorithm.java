package branchNbound;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BranchAndBoundAlgorithm<T extends DecisionNode> {

	private static Log log = LogFactory.getLog(BranchAndBoundAlgorithm.class);

	private T incumbent;

	private BranchAndBoundQueue<T> queue = new BranchAndBoundQueue<T>();

	private T root;

	public BranchAndBoundAlgorithm(T root) {
		this.root = root;
		checkBounds(root);
		queue.enqueue(root);
	}

	/**
	 * Expands the entire tree in a depth first manner, and prints values
	 */
	public void debug(boolean printLeafsOnly) {
		debug(root, 0, printLeafsOnly);
	}

	private void debug(T node, int indentation, boolean printLeafsOnly) {
		if (!printLeafsOnly || (printLeafsOnly && node.isTerminal()))
			System.out.println(StringUtils.repeat("   ", indentation)
					+ node.toString());

		double lowerBound = node.getLowerBound();

		boolean existChildWithHigherUpperBound = false;

		if (!node.isTerminal()) {
			for (T child : (Collection<T>) node.expand()) {
				debug(child, indentation + 1, printLeafsOnly);

				if (child.getUpperBound() >= lowerBound) {
					existChildWithHigherUpperBound = true;
				}
			}

			if (!existChildWithHigherUpperBound) {
				System.out
						.println("ERROR No child with upper bound > parent's lowerbound exists ");
				node.debugRecursive();
			}
		}
	}

	/**
	 * Returns true when algorithm is finished
	 * 
	 * @return
	 */
	public boolean step() {
		T current = queue.poll();

		if (current.isTerminal()) {
			incumbent = current;
		} else {
			Collection<T> children = (Collection<T>) current.expand();

			boolean existChildWithHigherUpperBound = false;
			double lowerBound = current.getLowerBound();

			for (T child : children) {
				checkBounds(child);
				checkBounds(child, current);

				if (child.getUpperBound() >= lowerBound)
					existChildWithHigherUpperBound = true;
			}

			if (!existChildWithHigherUpperBound) {
				debug();

				throw new IllegalArgumentException(
						"No child with upper bound > parent's lowerbound exists ");
			}

			queue.enqueue(children);
		}

		return queue.isEmpty();
	}

	private void checkBounds(T node) {
		if (node.isTerminal()) {
			if (node.getUpperBound() - node.getLowerBound() > 1e-5) {

				throw new IllegalArgumentException(
						"Node is terminal, but upper bound != lower bound "
								+ node.debug());
			}
		} else {
			if (node.getLowerBound() > node.getUpperBound()) {
				log.warn("Node's lower bound is greater than its upper bound "
						+ node.debug());
			}
		}
	}

	private void checkBounds(T child, T parent) {
		if (child.getUpperBound() - parent.getUpperBound() > 1e-10) {
			debug();

			log.warn("Child's upper bound is greater "
					+ "than parent's upper bound: Child: " + child
					+ ". Parent:" + parent + ".");
			log.warn("Child's upper bound is greater "
					+ "than parent's upper bound. Child "
					+ child.getUpperBound() + " Parent "
					+ parent.getUpperBound());
		}
	}

	public double getMaxUpperBound() {
		return queue.getMaxUpperBound();
	}

	public T getIncumbent() {
		if (incumbent == null) {
			debug();

			throw new IllegalArgumentException(
					"Algorithm has not been run, or bounds "
							+ "on the function are incorrect");
		}

		checkBounds(incumbent);

		return incumbent;
	}

	public void run() {
		while (!step())
			;
		// {debug();System.out.println(queue);}

		// System.out.println(queue.getNodeCount());
	}

	public int getExpandedNodes() {
		return queue.getNodeCount();
	}

	public void debug() {
		debug(false);
	}

}
