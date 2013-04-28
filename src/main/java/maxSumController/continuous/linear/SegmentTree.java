package maxSumController.continuous.linear;

import java.util.HashSet;
import java.util.Set;

public class SegmentTree<V extends Interval> {

	private SegmentTree<V> left;

	private SegmentTree<V> right;

	private double lowerBound;

	private double upperBound;

	private Set<V> intervals = new HashSet<V>();

	public Set<V> intersectors(double d) {
		Set<V> result = new HashSet<V>(intervals);

		if (left != null && d <= left.getUpperBound())
			result.addAll(left.intersectors(d));
		if (right != null && d >= right.getLowerBound())
			result.addAll(right.intersectors(d));

		return result;
	}

	public double getLowerBound() {
		return lowerBound;
	}

	public double getUpperBound() {
		return upperBound;
	}

	// public void insert(V interval) {
	// if (lowerBound >= interval.getUpperbound()
	// && upperBound <= interval.getLowerbound()) {
	// intervals.add(interval);
	// } else {
	// if()
	// }
	// }

}
