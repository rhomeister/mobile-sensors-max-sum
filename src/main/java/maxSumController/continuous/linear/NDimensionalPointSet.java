package maxSumController.continuous.linear;

import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

/**
 * Set of NDimensionalPoints that regards points that are very close together as
 * equal
 * 
 * @author rs06r
 * 
 */

public class NDimensionalPointSet extends Vector<NDimensionalPoint> {

	public NDimensionalPointSet(NDimensionalPointSet other) {
		this();
		addAll(other);
	}

	public NDimensionalPointSet() {
		super();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2097709868651498710L;

	@Override
	public synchronized boolean addAll(Collection<? extends NDimensionalPoint> c) {
		boolean result = false;

		for (NDimensionalPoint point : c) {
			result &= add(point);
		}

		return result;
	}

	@Override
	public synchronized boolean add(NDimensionalPoint e) {
		if (!contains(e))
			return super.add(e);

		return false;
	}

	public NDimensionalPoint getClosest(NDimensionalPoint point) {
		int indexOf = indexOf(point);

		if (indexOf == -1)
			return null;
		else
			return get(indexOf);
	}

	public void addAll(NDimensionalPoint[] points) {
		addAll(Arrays.asList(points));
	}
}
