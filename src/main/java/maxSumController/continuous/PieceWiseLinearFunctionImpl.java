package maxSumController.continuous;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.Validate;

import Jama.Matrix;

public class PieceWiseLinearFunctionImpl implements PieceWiseLinearFunction {

	private SortedMap<Double, LineSegment> intervals = new TreeMap<Double, LineSegment>();

	public PieceWiseLinearFunctionImpl(LineSegment segment) {
		addSegment(segment);
	}

	public Collection<LineSegment> getLineSegments() {
		return intervals.values();
	}

	public PieceWiseLinearFunctionImpl() {
	}

	public SortedSet<Double> getIntervalEndpoints() {
		SortedSet<Double> endpoints = new TreeSet<Double>();

		for (LineSegment segment : intervals.values()) {
			endpoints.add(segment.getX1());
			endpoints.add(segment.getX2());
		}

		return endpoints;
	}

	public double evaluate(double x) {
		LineSegment segment = getLineSegment(x);

		if (segment == null)
			return Double.NaN;

		return segment.evaluate(x);
	}

	public void addSegment(LineSegment segment) {
		// check for overlap
		checkForOverlappingSegments(segment);

		intervals.put(segment.getX1(), segment);
	}

	private void checkForOverlappingSegments(LineSegment segment) {
		Validate.isTrue(!segment.overlaps(getLineSegment(segment.getX1())));
		Validate.isTrue(!segment.overlaps(getLineSegment(segment.getX2())));

		SortedMap<Double, LineSegment> tailMap = intervals.tailMap(segment
				.getX1());

		if (!tailMap.isEmpty())
			Validate.isTrue(!segment.overlaps(tailMap.get(tailMap.firstKey())));
	}

	public LineSegment getLineSegment(double x) {
		try {

			if (intervals.isEmpty() || x < intervals.firstKey())
				return null;

			LineSegment segment;

			if (intervals.containsKey(x))
				segment = intervals.get(x);
			else {
				// this if statement deals with roundoff errors. That is, when x
				// is epsilon smaller than the firstKey, it returns the first
				// linesegment
				if (intervals.headMap(x).isEmpty()) {
					if (Math.abs(intervals.firstKey() - x) < 1e-10) {
						return intervals.get(intervals.firstKey());
					}
				}

				segment = intervals.get(intervals.headMap(x).lastKey());
			}

			if (!segment.isWithinDomain(x))
				return null;

			return segment;
		} catch (NoSuchElementException e) {
			System.out.println(intervals.headMap(x));

			System.out.println(x + " " + intervals);
			throw e;
		}
	}

	public double getLastIntervalEndpoint() {
		if (intervals.isEmpty())
			return Double.NaN;
		else
			return intervals.get(intervals.lastKey()).getX2();
	}

	public double getFirstIntervalEndpoint() {
		if (intervals.isEmpty())
			return Double.NaN;
		else
			return intervals.firstKey();
	}

	@Override
	public String toString() {

		StringBuffer buffer = new StringBuffer();
		buffer.append("[");

		for (Double endpoint : getIntervalEndpoints()) {
			buffer.append(endpoint + " " + evaluate(endpoint) + "; ");
		}

		buffer.append("]");
		return buffer.toString() + " " + intervals.values();
	}

	/**
	 * Returns the line segment that intersects the vertical line x, unless x =
	 * x2, in which case null is returned
	 */
	public LineSegment getStrictLineSegment(double x) {
		LineSegment segment = getLineSegment(x);

		if (segment != null && segment.getX2() == x)
			segment = null;

		return segment;
	}

	public void simplify() {
		// take two consecutive segments, check their coefficients, check
		// whether their endpoints are the same, if so, remove and make one
		simplify(intervals);
	}

	private void simplify(SortedMap<Double, LineSegment> segments) {
		if (segments.size() <= 1)
			return;

		Iterator<LineSegment> iterator = segments.values().iterator();

		LineSegment segment1 = iterator.next();
		LineSegment segment2 = iterator.next();

		if (segment1.getX2() == segment2.getX1()
				&& Math.abs(segment1.getY2() - segment2.getY1()) < 1e-5) {

			Matrix matrix = new Matrix(2, 2);
			matrix.set(0, 0, segment2.getX2() - segment1.getX1());
			matrix.set(0, 1, segment1.getX2() - segment1.getX1());
			matrix.set(1, 0, segment2.getY2() - segment1.getY1());
			matrix.set(1, 1, segment1.getY2() - segment1.getY1());

			if (Math.abs(matrix.det()) < 1e-10) {
				segments.remove(segment1.getX1());
				segments.remove(segment2.getX1());
				addSegment(new LineSegment(segment1.getX1(), segment1.getY1(),
						segment2.getX2(), segment2.getY2()));
			}

		}

		// if (segment1.getX2() == segment2.getX1()
		// && segment1.getY2() == segment2.getY1()
		// && Math.abs(segment1.getSlope() - segment2.getSlope()) < 1e-3) {
		// segments.remove(segment1.getX1());
		// segments.remove(segment2.getX1());
		// addSegment(new LineSegment(segment1.getX1(), segment1.getY1(),
		// segment2.getX2(), segment2.getY2()));
		// }

		simplify(segments.tailMap(getLineSegment(segment2.getX1()).getX1()));
	}

	public double argMax() {
		double result = Double.NaN;
		double maxY = Double.NEGATIVE_INFINITY;

		for (LineSegment segment : intervals.values()) {
			if (segment.getY1() > maxY) {
				result = segment.getX1();
				maxY = segment.getY1();
			}
			if (segment.getY2() > maxY) {
				result = segment.getX2();
				maxY = segment.getY2();
			}
		}

		return result;
	}

	public PieceWiseLinearFunction max(PieceWiseLinearFunction other) {
		PieceWiseLinearFunction max = new PieceWiseLinearFunctionImpl();

		double currentEndpoint = Math.min(getFirstIntervalEndpoint(), other
				.getFirstIntervalEndpoint());

		double lastIntervalEndpoint = Math.max(getLastIntervalEndpoint(), other
				.getLastIntervalEndpoint());

		// sweep through the list from left to right
		while (currentEndpoint < lastIntervalEndpoint) {
			LineSegment segment1 = getStrictLineSegment(currentEndpoint);
			LineSegment segment2 = other.getStrictLineSegment(currentEndpoint);

			if (segment1 == null && segment2 == null) {
				// no intersections, continue with next point
				currentEndpoint = getNextIntervalEndpoint(this, other,
						currentEndpoint);
				continue;
			}

			double x1 = currentEndpoint;
			double y1;
			double x2;
			double y2;

			if (segment1 != null && segment2 != null) {
				// two intersections with vertical sweepline

				y1 = max(segment1.evaluate(currentEndpoint), segment2
						.evaluate(currentEndpoint));

				double intersectX = segment1.intersection(segment2);

				if (Double.isNaN(intersectX) || intersectX <= currentEndpoint) {
					// no intersection
					x2 = currentEndpoint = getNextIntervalEndpoint(this, other,
							currentEndpoint);
					y2 = max(segment1.evaluate(x2), segment2.evaluate(x2));
				} else {
					x2 = currentEndpoint = Math.min(getNextIntervalEndpoint(
							this, other, currentEndpoint), intersectX);
					y2 = segment1.evaluate(currentEndpoint);
				}
			} else {
				// intersection with one of the upper envelopes
				LineSegment intersectingSegment = segment1 == null ? segment2
						: segment1;

				y1 = intersectingSegment.evaluate(currentEndpoint);
				x2 = currentEndpoint = getNextIntervalEndpoint(this, other,
						currentEndpoint);
				y2 = max(this.evaluate(x2), other.evaluate(x2));
			}

			max.addSegment(new LineSegment(x1, y1, x2, y2));
		}

		max.simplify();

		return max;
	}

	public PieceWiseLinearFunction add(PieceWiseLinearFunction other) {
		PieceWiseLinearFunction sum = new PieceWiseLinearFunctionImpl();

		double currentEndpoint = Math.min(getFirstIntervalEndpoint(), other
				.getFirstIntervalEndpoint());

		double lastIntervalEndpoint = Math.max(getLastIntervalEndpoint(), other
				.getLastIntervalEndpoint());

		// sweep through the list from left to right
		while (currentEndpoint < lastIntervalEndpoint) {
			LineSegment segment1 = getStrictLineSegment(currentEndpoint);
			LineSegment segment2 = other.getStrictLineSegment(currentEndpoint);

			if (segment1 == null && segment2 == null) {
				// no intersections, continue with next point
				currentEndpoint = getNextIntervalEndpoint(this, other,
						currentEndpoint);
				continue;
			}

			double x1 = currentEndpoint;
			double y1;
			double x2;
			double y2;

			if (segment1 != null && segment2 != null) {
				// two intersections with vertical sweepline
				y1 = sum(segment1.evaluate(currentEndpoint), segment2
						.evaluate(currentEndpoint));

				x2 = currentEndpoint = getNextIntervalEndpoint(this, other,
						currentEndpoint);
				y2 = sum(segment1.evaluate(x2), segment2.evaluate(x2));
			} else {
				// intersection with one of the upper envelopes
				LineSegment intersectingSegment = segment1 == null ? segment2
						: segment1;

				y1 = intersectingSegment.evaluate(currentEndpoint);
				x2 = currentEndpoint = getNextIntervalEndpoint(this, other,
						currentEndpoint);

				segment2 = other.getStrictLineSegment(currentEndpoint);

				if (intersectingSegment.getX2() == x2) {
					y2 = intersectingSegment.getY2();
				} else {
					y2 = sum(intersectingSegment.evaluate(x2), segment2
							.evaluate(x2));
				}
			}

			sum.addSegment(new LineSegment(x1, y1, x2, y2));
		}

		sum.simplify();

		return sum;
	}

	private double getNextIntervalEndpoint(
			PieceWiseLinearFunction upperEnvelope1,
			PieceWiseLinearFunction upperEnvelope2, double currentEndpoint) {
		double next = Double.POSITIVE_INFINITY;

		SortedSet<Double> tailset = upperEnvelope1.getIntervalEndpoints()
				.tailSet(currentEndpoint);

		for (Double endpoint : tailset) {
			if (endpoint > currentEndpoint) {
				next = endpoint;
				break;
			}
		}

		tailset = upperEnvelope2.getIntervalEndpoints()
				.tailSet(currentEndpoint);
		for (Double endpoint : tailset) {
			if (endpoint > currentEndpoint) {
				next = Math.min(next, endpoint);
				break;
			}
		}
		return next;
	}

	private double max(double d1, double d2) {
		if (Double.isNaN(d1))
			return d2;
		if (Double.isNaN(d2))
			return d1;

		return Math.max(d1, d2);
	}

	private double sum(double d1, double d2) {
		if (Double.isNaN(d1))
			return d2;
		if (Double.isNaN(d2))
			return d1;

		return d1 + d2;
	}

	public double getArea() {
		double integral = 0.0;
		for (LineSegment segment : intervals.values()) {
			integral += segment.getArea();
		}

		return integral;
	}

	/**
	 * Gets the length of the part of the domain of the function on which it is
	 * defined. More specifically, it is the length of the domain on which
	 * evaluate(x) != NaN
	 * 
	 * @return
	 */
	public double getValidDomainLength() {
		double domainLength = 0.0;

		for (LineSegment segment : intervals.values()) {
			domainLength += segment.getDomainLength();
		}
		return domainLength;
	}

	public void normalise() {
		double scaleFactor = getArea() / getValidDomainLength();

		for (LineSegment segment : intervals.values()) {
			segment.translateY(-scaleFactor);
		}
	}

	public void addSegments(double[] x, double[] y) {
		for (int i = 0; i < x.length - 1; i++) {
			addSegment(new LineSegment(x[i], y[i], x[i + 1], y[i + 1]));
		}
	}
}
