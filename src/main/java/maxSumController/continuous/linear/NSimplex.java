package maxSumController.continuous.linear;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.HashCodeBuilder;

import Jama.Matrix;

/**
 * An NSimplex represents an N dimensional simplex in a M dimensional space,
 * where M >= N. A NSimplex is defined by N+1 M-dimensional points
 * 
 * @author rs06r
 * 
 */
public class NSimplex implements NDimensionalSolid, Comparable<NSimplex> {

	private int currentPointCount;

	private NDimensionalPoint[] points;

	private int N;

	private int M;

	private NCube boundingBox;

	private Matrix normalVector;

	public static final double TOLERANCE = 1e-14;

	public NSimplex(int N) {
		this.N = N;
		this.points = new NDimensionalPoint[N + 1];
	}

	public NSimplex(NDimensionalPoint... points) {
		this(points.length - 1);

		for (NDimensionalPoint point : points) {
			addPoint(point);
		}
	}

	/**
	 * 0 if it is on a 0D boundary (point) 1 if it is on a 1D boundary 2 if it
	 * is on a 2D boundary 3 if it is on a 3D boundary -1 if it is not contained
	 * by this simplex
	 * 
	 * @param point
	 * @return
	 */
	public int getBoundaryNumber(NDimensionalPoint point) {
		double[] coefficients = getCoefficients(point);

		if (!contains(coefficients))
			return -1;

		int boundaryNumber = N;

		for (double d : coefficients)
			if (Math.abs(d) < TOLERANCE)
				boundaryNumber--;

		return boundaryNumber;
	}

	public void addPoint(NDimensionalPoint point) {
		Validate.isTrue(currentPointCount < points.length);
		if (currentPointCount > 0)
			Validate.isTrue(point.getDimensionCount() == M, "Expected " + M
					+ " dimensions, but got " + point.getDimensionCount());
		else {
			M = point.getDimensionCount();
			Validate.isTrue(N <= M);
		}

		points[currentPointCount++] = point;

		if (currentPointCount == N + 1) {
			checkForLinearDependence();
			createBoundingBox();
			// Arrays.sort(points);
		}
	}

	private void createBoundingBox() {

		boundingBox = new NCube(M);

		for (int i = 0; i < M; i++) {
			for (NDimensionalPoint point : points) {
				double lowerBound = Math.min(boundingBox.getDomainStart(i),
						point.getCoordinates()[i]);
				double upperBound = Math.max(boundingBox.getDomainEnd(i), point
						.getCoordinates()[i]);

				boundingBox.setBoundaries(i, lowerBound, upperBound);
			}
		}
	}

	/**
	 * Returns a M x N matrix with the N vectors of this simplex, where every
	 * column represents a vector
	 * 
	 * @return
	 */
	private Matrix getVectorMatrix() {
		Matrix vectorMatrix = new Matrix(M, N, 1.0);
		// create matrix where each column represents a vector
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < M; j++) {
				vectorMatrix.set(j, i, points[i + 1].getCoordinates()[j]
						- points[0].getCoordinates()[j]);
			}
		}

		return vectorMatrix;
	}

	private void checkForLinearDependence() {
		if (Math.abs(getVectorMatrix().cond()) > 1e10) {
			throw new IllegalArgumentException(
					"Simplex construction error: coordinates are "
							+ "not linearly independent: " + toString());
		}
	}

	public NDimensionalPoint[] getPoints() {
		return points;
	}

	public int getDimensionCount() {
		return N;
	}

	public int getM() {
		return M;
	}

	public int getN() {
		return N;
	}

	public double[] getCoefficients(NDimensionalPoint point) {
		Validate.isTrue(point.getDimensionCount() == getDimensionCount());

		if (!getBoundingBox().contains(point)) {
			double[] result = new double[N];
			result[0] = -1;
			return result;
		}

		Matrix coordinateMatrix = new Matrix(N + 1, N + 1, 1.0);

		// create matrix with each column being a coordinate augmented with 1
		for (int i = 0; i < points.length; i++) {
			for (int j = 0; j < N; j++) {
				coordinateMatrix.set(j, i, points[i].getCoordinates()[j]);
			}
		}

		Matrix pointMatrix = new Matrix(N + 1, 1, 1.0);
		for (int i = 0; i < N; i++) {
			pointMatrix.set(i, 0, point.getCoordinates()[i]);
		}
		try {
			Matrix matrix = coordinateMatrix.solve(pointMatrix);
			return matrix.getMatrix(0, N, 0, 0).getRowPackedCopy();
		} catch (RuntimeException e) {
			// singular matrix, precision problem in JAMA
			double[] result = new double[N];
			result[0] = -1;
			return result;
		}

	}

	public boolean contains(NDimensionalPoint point) {
		Validate.isTrue(currentPointCount == N + 1);

		if (!getBoundingBox().contains(point))
			return false;

		return contains(getCoefficients(point));
	}

	private boolean contains(double[] coefficients) {
		for (int i = 0; i < coefficients.length; i++) {
			// small rounding errors can cause problems, compensating by
			// comparing with very small negative number instead of 0
			if (coefficients[i] < -TOLERANCE)
				return false;
		}

		return true;
	}

	/**
	 * Computes intersections with surfaces of the simplex
	 * 
	 * @param line
	 * @return
	 */
	public NDimensionalPointSet getIntersections(NDimensionalLine line) {
		// Computing the intersections between a line and a simplex involves
		// computing the intersections between the line and all the surfaces of
		// the simplex
		// If the line is parameterised as [xs xe] * [lamdba1 lambda2], where
		// lambda1, lambda2 > 0 and lambda1 + lambda2 = 1, then the problem of
		// finding the intersections is reduced to solving n systems of linear
		// equations:
		// A * x = b, where
		// A = [xs xe a(1) .. a(i) a(i+2) .. a(n); 1 1 0 .. 0; 0 0 1 .. 1]
		// x = [lambda1 lambda2 -a(1) .. -a(i) -a(i+2) .. -a(n)]
		// b = [0 .. 0 1 -1]
		Validate.isTrue(line.getDimensionCount() == M);

		NDimensionalPointSet result = new NDimensionalPointSet();

		if (!getBoundingBox().overlaps(line.getBoundingBox()))
			return result;

		if (M == N) {
			// possibly infinite solutions, only interested in where line
			// intersects with surfaces of the simplex

			for (int i = 0; i < points.length; i++) {
				Matrix A = createCoordinateMatrix(i, line);
				result.addAll(solve(A, line));
			}

			return result;

		} else {
			// one solution where line intersects the simplex

			Matrix A = createCoordinateMatrix(line);

			return solve(A, line);
		}

	}

	private NDimensionalPointSet solve(Matrix A, NDimensionalLine line) {

		NDimensionalPointSet result = new NDimensionalPointSet();

		if (A.cond() > 1e10)
			return result;

		NDimensionalPoint start = line.getStart();
		NDimensionalPoint end = line.getEnd();

		Matrix b = new Matrix(M + 2, 1);
		b.set(M, 0, 1);
		b.set(M + 1, 0, -1);

		Matrix x;

		x = A.solve(b);

		// solution might be obtained by least squares. If so, reject
		if (A.times(x).minus(b).norm1() > TOLERANCE)
			return result;

		// check validity of solution, i.e. lambda's >= 0, a(i)'s <= 0
		double lambda = x.get(0, 0);

		if (lambda < -TOLERANCE || lambda > 1.0 + TOLERANCE)
			return result;

		boolean invalid = false;
		for (int j = 2; j < N + 2; j++) {
			if (x.get(j, 0) > TOLERANCE) {
				invalid = true;
			}
		}
		if (invalid)
			return result;

		NDimensionalPoint point = new NDimensionalPoint(N);

		for (int j = 0; j < N; j++) {
			point.setCoordinate(j, lambda * start.getCoordinates()[j]
					+ (1 - lambda) * end.getCoordinates()[j]);
		}

		result.add(point);

		return result;
	}

	public Collection<NDimensionalPoint> getIntersections(NSimplex simplex) {
		Set<NDimensionalPoint> result = new HashSet<NDimensionalPoint>();

		if (getBoundingBox().overlaps(simplex.getBoundingBox())) {
			for (NDimensionalLine line : simplex.getEdges()) {
				result.addAll(getIntersections(line));
			}
			for (NDimensionalLine line : getEdges()) {
				result.addAll(simplex.getIntersections(line));
			}
		}

		List<NDimensionalPoint> list = new ArrayList<NDimensionalPoint>();

		for (NDimensionalPoint point : result) {
			if (!list.contains(point)) {
				list.add(point);
			}
		}

		return new Vector<NDimensionalPoint>(list);
	}

	public NCube getBoundingBox() {
		return boundingBox;
	}

	/*
	 * A = [xs xe a(1) .. a(i) a(i+2) .. a(n); 1 1 0 .. 0; 0 0 1 .. 1]
	 */
	protected Matrix createCoordinateMatrix(int excludedIndex,
			NDimensionalLine line) {
		int[] columnIndexes = new int[N + 2];

		for (int i = 0; i < columnIndexes.length; i++)
			columnIndexes[i] = i >= (excludedIndex + 2) ? i + 1 : i;

		return createCoordinateMatrix(line).getMatrix(0, M + 1, columnIndexes);
	}

	protected Matrix createCoordinateMatrix(NDimensionalLine line) {
		Matrix A = new Matrix(M + 2, N + 3);

		for (int i = 0; i < M; i++) {
			A.set(i, 0, line.getStart().getCoordinates()[i]);
			A.set(i, 1, line.getEnd().getCoordinates()[i]);
		}

		A.set(M, 0, 1);
		A.set(M, 1, 1);

		for (int i = 0; i < M; i++) {
			for (int j = 0; j < N + 1; j++) {
				A.set(i, j + 2, points[j].getCoordinates()[i]);
			}
		}

		for (int i = 0; i < N + 1; i++) {
			A.set(M + 1, i + 2, 1);
		}

		return A;
	}

	/**
	 * Splits this simplex into at most N + 1 simplices around the point
	 * specified as parameter. The point should be inside the simplex
	 * 
	 * @param point
	 * @return
	 */
	public Set<NSimplex> split(NDimensionalPoint point) {
		Validate.isTrue(point.getDimensionCount() == getDimensionCount());
		Validate.isTrue(contains(point));

		Set<NSimplex> result = new HashSet<NSimplex>();

		for (int i = 0; i < points.length; i++) {
			try {
				NSimplex simplex = new NSimplex(N);
				simplex.addPoint(point);
				for (int j = 0; j < N; j++) {
					// skip point with index i
					simplex.addPoint(points[j >= i ? j + 1 : j]);
				}
				result.add(simplex);
			} catch (IllegalArgumentException e) {
				// coordinates are linearly dependent, skip
			}
		}

		return result;
	}

	public Set<NDimensionalLine> getEdges() {
		Set<NDimensionalLine> result = new HashSet<NDimensionalLine>();

		for (int i = 0; i < points.length; i++) {
			for (int j = i + 1; j < points.length; j++) {
				result.add(new NDimensionalLine(points[i], points[j]));
			}
		}

		return result;
	}

	@Override
	public String toString() {
		String result = "{";

		for (NDimensionalPoint point : points) {
			result += point + ", ";
		}
		result = result.substring(0, result.length() - 2);
		result += "}";
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NSimplex) {
			NSimplex simplex = (NSimplex) obj;

			if (simplex.getDimensionCount() != getDimensionCount())
				return false;

			if (simplex.currentPointCount != currentPointCount)
				return false;

			for (NDimensionalPoint point : points) {
				if (!ArrayUtils.contains(simplex.points, point))
					return false;
			}

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(points).toHashCode();
	}

	public int compareTo(NSimplex o) {
		for (int i = 0; i < points.length; i++) {
			if (points[i].compareTo(o.points[i]) > 0)
				return 1;

			if (points[i].compareTo(o.points[i]) < 0)
				return -1;
		}

		return 0;
	}

	public NSimplex createValueSimplex(Map<NDimensionalPoint, Double> values) {
		NSimplex result = new NSimplex(N);

		for (NDimensionalPoint point : points) {
			result.addPoint(new NDimensionalPoint(point.getCoordinates(),
					values.get(point)));
		}

		return result;
	}

	/**
	 * Returns the normal vector for this simplex (i.e. vector that is
	 * orthogonal to the simplex) by using full Singular Value Decomposition
	 * 
	 * @return
	 */
	public Matrix getNormalVector() {
		if (normalVector == null) {
			SingularValueDecompositionFull decomposition = new SingularValueDecompositionFull(
					getVectorMatrix());

			normalVector = decomposition.getU().getMatrix(0, M - 1, M - 1,
					M - 1);
		}

		return normalVector;
	}

	/**
	 * Returns true if the two simplices are parallel. Two simplices are
	 * considered parallel if the difference between the two normal vectors is
	 * smaller than some threshold
	 * 
	 * @param simplex
	 * @return
	 */
	public boolean isParallel(NSimplex simplex) {
		Validate.isTrue(getM() == simplex.getM());
		Validate.isTrue(getN() == simplex.getN());

		Matrix difference = getNormalVector().minus(simplex.getNormalVector());
		return difference.norm2() < 1e-8;
	}

	public boolean hasSharedSurface(NSimplex simplex) {
		if (equals(simplex))
			return false;
		else {
			// should have N points in common
			return CollectionUtils.intersection(Arrays.asList(getPoints()),
					Arrays.asList(simplex.getPoints())).size() == N;
		}

	}

	public static NSimplex merge(Collection<NSimplex> set,
			NDimensionalPoint point) {
		Validate.notNull(point);

		NSimplex mergedSimplex = new NSimplex(set.iterator().next().getN());

		NDimensionalPointSet points = new NDimensionalPointSet();

		for (NSimplex simplex : set)
			points.addAll(simplex.getPoints());

		points.remove(point);

		try {
			mergedSimplex.addPoints(points);
		} catch (IllegalArgumentException e) {
			return null;
		}

		// if(mergedSimplex.currentPointCount != mergedSimplex.N+1) {
		// System.out.println(set);
		// System.out.println(point);
		// System.out.println(mergedSimplex);
		// }

		Validate.isTrue(mergedSimplex.currentPointCount == mergedSimplex.N + 1);

		return mergedSimplex;
	}

	private void addPoints(NDimensionalPointSet points) {
		for (NDimensionalPoint point : points) {
			addPoint(point);
		}
	}
}
