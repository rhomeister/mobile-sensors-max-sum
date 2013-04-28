package maxSumController.continuous.linear;

import java.util.LinkedHashSet;
import java.util.Set;

public class IntervalTree<V extends Interval> {
	public IntervalTree(double l, double h) {
		low = l;
		high = h;
		mid = (l + h) / 2;
		hits = new LinkedHashSet<V>();
		left = null;
		right = null;
		everin = new LinkedHashSet<V>();
	}

	public void remove(V i) {
		switch (where(i)) {
		case -1:
			if (left != null)
				left.remove(i);
			break;
		case 0:
			hits.remove(i);
			break;
		case 1:
			if (right != null)
				right.remove(i);
			break;
		}
	}

	public void add(V i) {
		everin.add(i);
		switch (where(i)) {
		case -1:
			if (left == null)
				left = new IntervalTree<V>(low, mid);
			left.add(i);
			break;
		case 0:
			hits.add(i);
			break;
		case 1:
			if (right == null)
				right = new IntervalTree<V>(mid, high);
			right.add(i);
			break;
		}
	}

	public Set<V> intersectors(V i) {
		Set<V> s = new LinkedHashSet<V>();
		addIntersectors(i, s);
		s.remove(i);
		return s;
	}

	public boolean contains(Object x) {
		return hits.contains(x) || (left != null && left.contains(x))
				|| (right != null && right.contains(x));
	}

	public double lowerBound() {
		return low;
	}

	public double upperBound() {
		return high;
	}

	public Set<V> getIntervals() {
		Set<V> s = new LinkedHashSet<V>();
		s.addAll(hits);
		if (left != null)
			s.addAll(left.getIntervals());
		if (right != null)
			s.addAll(right.getIntervals());

		Set<V> out = new LinkedHashSet<V>();
		out.addAll(everin);
		out.retainAll(s);
		return out;
	}

	// Private data.

	private double low = 0;

	private double high = 0;

	private double mid = 0;

	private Set<V> hits = null;

	private IntervalTree<V> left = null;

	private IntervalTree<V> right = null;

	private LinkedHashSet<V> everin = null;

	private void addIntersectors(V x, Set<V> s) {
		switch (where(x)) {
		case -1:
			for (V mine : hits)
				if (mine.getLowerbound() < x.getUpperbound())
					s.add(mine);
			if (left != null)
				left.addIntersectors(x, s);
			break;
		case 0:
			for (V mine : hits)
				s.add(mine);
			if (left != null)
				left.addRightEndRightOf(x.getLowerbound(), s);
			if (right != null)
				right.addLeftEndLeftOf(x.getUpperbound(), s);
			break;
		case 1:
			for (V mine : hits)
				if (mine.getUpperbound() > x.getLowerbound())
					s.add(mine);
			if (right != null)
				right.addIntersectors(x, s);
			break;
		}
	}

	private void addRightEndRightOf(double p, Set<V> s) {
		switch (where(p)) {
		case -1:
			if (left != null)
				left.addRightEndRightOf(p, s);
		case 0:
			for (V mine : hits)
				s.add(mine);
			if (right != null)
				right.addAll(s);
			break;
		case 1:
			for (V mine : hits)
				if (mine.getUpperbound() > p)
					s.add(mine);
			if (right != null)
				right.addRightEndRightOf(p, s);
			break;
		}
	}

	private void addLeftEndLeftOf(double p, Set<V> s) {
		switch (where(p)) {
		case 1:
			if (right != null)
				right.addLeftEndLeftOf(p, s);
		case 0:
			for (V mine : hits)
				s.add(mine);
			if (left != null)
				left.addAll(s);
			break;
		case -1:
			for (V mine : hits)
				if (mine.getLowerbound() < p)
					s.add(mine);
			if (left != null)
				left.addLeftEndLeftOf(p, s);
			break;
		}
	}

	private void addAll(Set<V> s) {
		if (left != null)
			left.addAll(s);
		s.addAll(hits);
		if (right != null)
			right.addAll(s);
	}

	private int where(double p) {
		if (p < mid)
			return -1;
		if (p > mid)
			return 1;
		return 0;
	}

	private int where(V i) {
		if (i.getUpperbound() < mid)
			return -1;
		if (i.getLowerbound() > mid)
			return 1;
		return 0;
	}
}
