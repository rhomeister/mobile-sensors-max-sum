package maxSumController.continuous.linear;

import java.util.Arrays;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class NDimensionalPoint implements NDimensionalObject,
		Comparable<NDimensionalPoint> {

	private static final double TOLERANCE = 1e-12;

	private double[] coordinates;

	public NDimensionalPoint(double... coordinates) {
		this.coordinates = coordinates;
		validate();
	}

	private void validate() {
		for (double d : coordinates) {
			Validate.isTrue(!Double.isNaN(d));
		}
	}

	public NDimensionalPoint(int dimensions) {
		coordinates = new double[dimensions];
	}

	/**
	 * Utility constructor for creating a point whose first n-1 coordinates are
	 * given by the first parameter, and the n^th coordinate is given by the
	 * second
	 * 
	 * @param coordinates
	 * @param coordinate
	 */
	public NDimensionalPoint(double[] coordinates, double coordinate) {
		this.coordinates = new double[coordinates.length + 1];

		System.arraycopy(coordinates, 0, this.coordinates, 0,
				coordinates.length);
		this.coordinates[coordinates.length] = coordinate;

		validate();
	}

	public double[] getCoordinates() {
		return coordinates;
	}

	public void setCoordinate(int index, double coordinate) {
		coordinates[index] = coordinate;
		// validate();
	}

	public int getDimensionCount() {
		return coordinates.length;
	}

	@Override
	public String toString() {
		return Arrays.toString(coordinates);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NDimensionalPoint) {
			NDimensionalPoint point = (NDimensionalPoint) obj;

			if (point.getDimensionCount() == getDimensionCount()) {
				return getSquaredDistance(point) < TOLERANCE;
			}

		}

		return false;
	}

	public double getSquaredDistance(NDimensionalPoint point) {
		Validate.isTrue(point.getDimensionCount() == getDimensionCount());

		double sumOfSquaredDifference = 0.0;

		for (int i = 0; i < getDimensionCount(); i++) {
			double difference = coordinates[i] - point.coordinates[i];
			sumOfSquaredDifference += difference * difference;
		}

		return sumOfSquaredDifference;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(coordinates).toHashCode();
	}

	public int compareTo(NDimensionalPoint o) {
		for (int i = 0; i < coordinates.length; i++) {
			if (coordinates[i] > o.coordinates[i])
				return 1;
			if (coordinates[i] < o.coordinates[i])
				return -1;
		}

		return 0;
	}

	public NDimensionalPoint add(NDimensionalPoint point) {
		Validate.isTrue(point.getDimensionCount() == getDimensionCount());

		NDimensionalPoint result = new NDimensionalPoint(getDimensionCount());

		for (int i = 0; i < coordinates.length; i++) {
			result.setCoordinate(i, coordinates[i] + point.coordinates[i]);
		}

		return result;
	}

	public NDimensionalPoint times(double d) {
		NDimensionalPoint result = new NDimensionalPoint(getDimensionCount());

		for (int i = 0; i < coordinates.length; i++) {
			result.setCoordinate(i, coordinates[i] * d);
		}

		return result;
	}
}
