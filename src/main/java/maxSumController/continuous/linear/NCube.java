package maxSumController.continuous.linear;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;

public class NCube implements NDimensionalSolid {

	private double[] boundaryStart;

	private double[] boundaryEnd;

	private int N;

	public NCube(double[] boundaryStart, double[] boundaryEnd) {
		Validate.isTrue(boundaryEnd.length == boundaryStart.length);
		this.N = boundaryStart.length;
		this.boundaryEnd = boundaryEnd;
		this.boundaryStart = boundaryStart;
	}

	public NCube(int N) {
		this(new double[N], new double[N]);
	}

	public void setBoundaries(int index, double lowerBound, double upperBound) {
		Validate
				.isTrue(lowerBound <= upperBound, lowerBound + " " + upperBound);

		boundaryStart[index] = lowerBound;
		boundaryEnd[index] = upperBound;
	}

	public int getDimensionCount() {
		return N;
	}

	public boolean contains(NDimensionalPoint point) {
		Validate.isTrue(point.getDimensionCount() == N);

		for (int i = 0; i < N; i++) {
			if (point.getCoordinates()[i] < boundaryStart[i]
					- NSimplex.TOLERANCE
					|| point.getCoordinates()[i] > boundaryEnd[i]
							+ NSimplex.TOLERANCE)
				return false;
		}

		return true;
	}

	public double getVolume() {
		double volume = boundaryEnd[0] - boundaryStart[0];

		for (int i = 1; i < N; i++) {
			volume *= boundaryEnd[i] - boundaryStart[i];
		}

		return volume;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NCube) {
			NCube cube = (NCube) obj;

			if (cube.getDimensionCount() == getDimensionCount()) {
				for (int i = 0; i < cube.getDimensionCount(); i++) {
					if (Math.abs(cube.getDomainStart(i) - getDomainStart(i)) > 1e-8)
						return false;
					if (Math.abs(cube.getDomainEnd(i) - getDomainEnd(i)) > 1e-8)
						return false;
				}

				return true;
			}
		}

		return false;
	}

	public double getDomainStart(int i) {
		return boundaryStart[i];
	}

	public double getDomainEnd(int i) {
		return boundaryEnd[i];
	}

	public NDimensionalPoint getRandomCoordinate() {
		NDimensionalPoint point = new NDimensionalPoint(N);

		for (int i = 0; i < N; i++) {
			point.setCoordinate(i, Math.random()
					* (boundaryEnd[i] - boundaryStart[i]) + boundaryStart[i]);
		}

		Validate.isTrue(contains(point));

		return point;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NCube: ");

		for (int i = 0; i < N; i++) {
			builder.append("[" + boundaryStart[i] + ", " + boundaryEnd[i]
					+ "] ");
		}

		return builder.toString();
	}

	public Set<NDimensionalPoint> getVertices() {
		Set<NDimensionalPoint> result = new HashSet<NDimensionalPoint>();

		double[][] ds = MultiVariatePieceWiseLinearFunctionUtilities
				.createGrid(this);

		for (int i = 0; i < ds[0].length; i++) {
			NDimensionalPoint point = new NDimensionalPoint(N);
			for (int j = 0; j < ds.length; j++) {
				point.setCoordinate(j, ds[j][i]);
			}
			result.add(point);
		}

		return result;
	}

	public boolean overlaps(NCube boundingBox) {
		for (NDimensionalPoint point : getVertices()) {
			if (boundingBox.contains(point)) {
				return true;
			}
		}

		for (NDimensionalPoint point : boundingBox.getVertices()) {
			if (contains(point)) {
				return true;
			}
		}

		return false;
	}

}
