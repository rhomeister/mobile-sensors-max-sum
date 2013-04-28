package branchNbound;

import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;

import org.apache.commons.collections15.SortedBag;
import org.apache.commons.collections15.bag.TreeBag;
import org.apache.commons.lang.Validate;

public class BranchAndBoundQueue<T extends DecisionNode> {
	private boolean debug = true;

	private Comparator<T> upperBoundComparator = new Comparator<T>() {
		public int compare(T o1, T o2) {
			return Double.compare(o1.getUpperBound(), o2.getUpperBound());
		}
	};

	private int nodeCount = 0;

	// keeps track of all nodes currently in the queue, ordered by natural
	// preference (specified by specific compareTo() implementation of the node)
	private PriorityQueue<T> queue = new PriorityQueue<T>();

	// sorted bag that saves upper bounds in increasing order
	private SortedBag<Double> upperBoundsSorted = new TreeBag<Double>();

	// queue that saves elements in increasing order of upperBound
	private PriorityQueue<T> upperBoundSortedNodes = new PriorityQueue<T>(10,
			upperBoundComparator);

	private double maxLowerBound = Double.NEGATIVE_INFINITY;

	public void enqueue(T element) {
		if (enqueueInternal(element))
			update();
	}

	/**
	 * Returns true if an element was added to the queue
	 * 
	 * @param element
	 * @return
	 */
	private boolean enqueueInternal(T element) {
		nodeCount++;
		element.setVisited(true);

		// only enqueue if this element's upper bound is greater or equal to the
		// maximum
		// lower bound found so far
		if (element.getUpperBound() >= maxLowerBound - 1e-10) {
			queue.add(element);
			maxLowerBound = Math.max(maxLowerBound, element.getLowerBound());
			upperBoundsSorted.add(element.getUpperBound());
			upperBoundSortedNodes.add(element);

			validate();

			return true;
		}

		return false;
	}

	/**
	 * The number of nodes offered to this queue
	 * 
	 * @return
	 */
	public int getNodeCount() {
		return nodeCount;
	}

	public void enqueue(Collection<T> elements) {
		boolean updated = false;

		for (T node : elements)
			updated |= enqueueInternal(node);

		if (updated)
			update();
	}

	private void update() {
		// remove all nodes for which upper < lower
		// in order of lowest upper bound to highest upperbound
		while (queue.size() >= 2) {
			T element = upperBoundSortedNodes.peek();

			// if there exists a lowerbound that is higher than this node's
			// upper bound, prune and continue
			if (element.getUpperBound() <= maxLowerBound - 1e-10) {

				// if the element has upperBound == lowerBound, we need to find
				// out whether there exists a different element with a strictly
				// higher upperbound
				if (element.getUpperBound() == element.getLowerBound()
						&& element.getUpperBound() == maxLowerBound)
					break;

				upperBoundSortedNodes.remove();
				// horrible violation of contract: TreeBag.remove() removes all!
				upperBoundsSorted.remove(element.getUpperBound(), 1);
				Validate.isTrue(queue.remove(element));
			} else
				break;
		}

		validate();
	}

	private void validate() {
		if (!debug)
			return;

		Validate.isTrue(queue.size() == upperBoundSortedNodes.size(), queue
				+ " " + upperBoundSortedNodes);
		Validate.isTrue(queue.size() == upperBoundsSorted.size(), queue + " "
				+ upperBoundsSorted);
	}

	/**
	 * The current maximum upperbound in the queue
	 * 
	 * @return
	 */
	public double getMaxUpperBound() {
		if (upperBoundSortedNodes.isEmpty())
			return Double.POSITIVE_INFINITY;

		return upperBoundsSorted.last();
	}

	/**
	 * The maximum lowerbound that passed through this queue
	 * 
	 * @return
	 */
	public double getMaxLowerBound() {
		return maxLowerBound;
	}

	public T poll() {
		T current = queue.poll();
		Validate.notNull(current);

		upperBoundSortedNodes.remove(current);
		upperBoundsSorted.remove(current.getUpperBound(), 1);

		validate();

		return current;
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}

	@Override
	public String toString() {
		return queue.toString();
	}

	public int size() {
		return queue.size();
	}

	public boolean contains(T node) {
		return queue.contains(node);
	}

	public boolean isDone() {
		return maxLowerBound == getMaxUpperBound();

	}
}
