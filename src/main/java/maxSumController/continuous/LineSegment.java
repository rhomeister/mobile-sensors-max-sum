package maxSumController.continuous;

import org.apache.commons.lang.Validate;

public class LineSegment implements Comparable<LineSegment> {
	private double x1;

	private double y1;

	private double x2;

	private double y2;

	public LineSegment(double x1, double y1, double x2, double y2) {
		Validate.isTrue(x1 != x2, x1 + " " + x2);

		if (x1 < x2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		} else {
			this.x1 = x2;
			this.y1 = y2;
			this.x2 = x1;
			this.y2 = y1;
		}
	}

	public double getX1() {
		return x1;
	}

	public double getX2() {
		return x2;
	}

	public double getY1() {
		return y1;
	}

	public double getY2() {
		return y2;
	}

	/**
	 * Calculates the x coordinate of the intersection of this LineSegment with
	 * another
	 * 
	 * @param l
	 * @return the x coordinate of the intersection with the LineSegment given
	 *         as a parameter, or NaN if the segments do not intersect
	 */
	public double intersection(LineSegment l) {
		double rc1 = getSlope();
		double rc2 = l.getSlope();

		double c1 = y1 - rc1 * x1;
		double c2 = l.y1 - rc2 * l.x1;

		double x = (c2 - c1) / (rc1 - rc2);

		if (!isWithinDomain(x) || !l.isWithinDomain(x))
			return Double.NaN;

		return x;
	}

	public boolean isWithinDomain(double x) {
		return (x >= x1) && (x <= x2);
	}

	public double getSlope() {
		return (y2 - y1) / (x2 - x1);
	}

	/**
	 * Calculates the y coordinate given an x coordinate
	 * 
	 * @param x
	 * @return the y coordinate for the given x coordinate, or NaN if the x
	 *         coordinate is outside the range of this linesegment
	 */
	public double evaluate(double x) {
		if (!isWithinDomain(x))
			return Double.NaN;

		return (x - x1) / (x2 - x1) * (y2 - y1) + y1;
	}

	public int compareTo(LineSegment o) {
		return Double.compare(x1, o.x1);
	}

	@Override
	public String toString() {
		return "[(" + x1 + ", " + y1 + "), (" + x2 + ", " + y2 + ")]";
	}

	public boolean overlaps(LineSegment lineSegment) {
		if (lineSegment == null)
			return false;

		if (this.x1 == lineSegment.x1 && this.x2 == lineSegment.x2)
			return true;

		return isStrictlyWithinDomain(lineSegment.getX1())
				|| isStrictlyWithinDomain(lineSegment.getX2())
				|| lineSegment.isStrictlyWithinDomain(x1)
				|| lineSegment.isStrictlyWithinDomain(x2);
	}

	public boolean isStrictlyWithinDomain(double x) {
		return (x > x1) && (x < x2);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LineSegment) {
			LineSegment line = (LineSegment) obj;
			return x1 == line.x1 && x2 == line.x2 && y1 == line.y1
					&& y2 == line.y2;
		}
		return false;
	}

	/**
	 * Calculates the area beneath this linesegment
	 * 
	 * @return
	 */
	public double getArea() {
		return (y1 + y2) / 2 * getDomainLength();
	}

	/**
	 * Returns the length of the domain on which this segment is defined
	 * 
	 * @return x2 - x1;
	 */
	public double getDomainLength() {
		return x2 - x1;
	}

	public void translateY(double d) {
		y1 += d;
		y2 += d;
	}
}
