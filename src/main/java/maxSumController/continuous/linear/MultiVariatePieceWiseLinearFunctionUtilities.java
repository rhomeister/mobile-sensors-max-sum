package maxSumController.continuous.linear;

import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;

import org.apache.commons.lang.Validate;

import maxSumController.continuous.PieceWiseLinearFunction;
import maxSumController.continuous.linear.delaunay.DelaunayClarkson;

public class MultiVariatePieceWiseLinearFunctionUtilities {

	private MultiVariatePieceWiseLinearFunctionUtilities() {

	}

	public static MultiVariatePieceWiseLinearFunction createMultiDimensionalGridFunction(
			List<PieceWiseLinearFunction> functions) {
		int dimensionality = functions.size();
		double[][] points = createGrid(functions);

		MultiVariatePieceWiseLinearFunction function = partitionMultiDimensionalGridFunction(points);

		for (NDimensionalPoint point : function.getPartitioning()
				.getDefiningCoordinates()) {
			double value = 0.0;

			for (int i = 0; i < dimensionality; i++) {
				value += functions.get(i).evaluate(point.getCoordinates()[i]);
			}

			function.setValue(point, value);
		}

		return function;
	}

	private static MultiVariatePieceWiseLinearFunction partitionMultiDimensionalGridFunction(
			double[][] points) {
		MultiVariatePieceWiseLinearFunction function = new MultiVariatePieceWiseLinearFunction(
				createDomain(points));

		// DelaynayClarkson can't handle 1 dimensional functions. These are
		// therefore handled as a special case.
		if (function.getDimensionCount() == 1) {
			for (int i = 0; i < points[0].length - 1; i++) {
				function
						.addPartition(new NSimplex(new NDimensionalPoint(
								points[0][i]), new NDimensionalPoint(
								points[0][i + 1])));
			}

		} else {
			DelaunayClarkson dc = new DelaunayClarkson(points);

			for (int i = 0; i < dc.Tri.length; i++) {
				NSimplex simplex = new NSimplex(points.length);

				for (int j = 0; j < dc.Tri[i].length; j++) {
					NDimensionalPoint point = createPoint(dc.Tri[i][j], points);
					simplex.addPoint(point);
				}

				function.addPartition(simplex);
			}

		}

		return function;

	}

	private static NCube createDomain(double[][] points) {
		NCube domain = new NCube(points.length);

		for (int i = 0; i < points.length; i++) {
			domain.setBoundaries(i, points[i][0],
					points[i][points[i].length - 1]);
		}

		return domain;
	}

	public static MultiVariatePieceWiseLinearFunction createMultiDimensionalGridFunction(
			NCube domain) {
		return partitionMultiDimensionalGridFunction(createGrid(getPoints(domain)));
	}

	private static double[][] getPoints(NCube domain) {
		double[][] points = new double[domain.getDimensionCount()][2];

		for (int i = 0; i < domain.getDimensionCount(); i++) {
			points[i][0] = domain.getDomainStart(i);
			points[i][1] = domain.getDomainEnd(i);
		}

		return points;
	}

	public static double[][] createGrid(NCube domain) {
		return createGrid(getPoints(domain));
	}

	private static double[][] createGrid(List<PieceWiseLinearFunction> functions) {
		double[][] intervalEndpoints = new double[functions.size()][];

		for (int i = 0; i < intervalEndpoints.length; i++) {
			SortedSet<Double> endpoints = functions.get(i)
					.getIntervalEndpoints();

			intervalEndpoints[i] = new double[endpoints.size()];

			int index = 0;

			for (Double d : endpoints) {
				intervalEndpoints[i][index++] = d;
			}
		}

		return createGrid(intervalEndpoints);
	}

	private static double[][] createGrid(double[][] points) {
		int totalCoordinateCount = getTotalCoordinateCount(points);

		double[][] gridPoints = new double[points.length][totalCoordinateCount];
		int sampleCount = 1;

		for (int dimensionIndex = 0; dimensionIndex < points.length; dimensionIndex++) {
			double[] intervalEndpoints = points[dimensionIndex];

			for (int i = 0; i < totalCoordinateCount; i++) {
				gridPoints[dimensionIndex][i] = intervalEndpoints[(i / sampleCount)
						% intervalEndpoints.length];
			}

			sampleCount *= intervalEndpoints.length;
		}

		return gridPoints;

	}

	private static int getTotalCoordinateCount(double[][] points) {
		int totalSampleCount = 1;

		for (int i = 0; i < points.length; i++)
			totalSampleCount *= points[i].length;

		return totalSampleCount;
	}

	private static NDimensionalPoint createPoint(int index, double[][] points) {
		NDimensionalPoint point = new NDimensionalPoint(points.length);

		for (int i = 0; i < points.length; i++)
			point.setCoordinate(i, points[i][index]);

		return point;
	}

	public static void main(String[] args) {
		// float[][] samples = new float[][] { { 0, 0, 0, 0, 1, 1, 1, 1 },
		// { 0, 0, 1, 1, 0, 0, 1, 1 }, { 0, 1, 0, 1, 0, 1, 0, 1 } };

		double[][] samples = new double[][] { { 0, 1, 2, 0, 1, 2 },
				{ 0, 0, 0, 3, 3, 3 } };

		DelaunayClarkson dc = new DelaunayClarkson(samples);

		System.out.println(Arrays.deepToString(dc.Tri));
	}

	public static MultiVariatePieceWiseLinearFunction createZeroFunction(
			NCube domain) {
		return createMultiDimensionalGridFunction(domain);
	}

	public static MultiVariatePieceWiseLinearFunction createConstantFunction(
			NCube domain, double value) {
		MultiVariatePieceWiseLinearFunction function = createZeroFunction(domain);

		for (NDimensionalPoint point : function.getPartitioning()
				.getDefiningCoordinates()) {
			function.setValue(point, value);
		}

		return function;
	}

	public static MultiVariatePieceWiseLinearFunction createUnivariateFunction(
			NCube domain, int variableIndex) {
		MultiVariatePieceWiseLinearFunction function = createZeroFunction(domain);

		for (NDimensionalPoint point : function.getPartitioning()
				.getDefiningCoordinates()) {
			function.setValue(point, point.getCoordinates()[variableIndex]);
		}

		return function;
	}

	public static void verifyAddition(
			MultiVariatePieceWiseLinearFunction function1,
			MultiVariatePieceWiseLinearFunction function2,
			MultiVariatePieceWiseLinearFunction sumFunction) {
		NCube domain = function1.getDomain();

		for (int i = 0; i < 1000; i++) {
			NDimensionalPoint randomCoordinate = domain.getRandomCoordinate();

			double sum = function1.evaluate(randomCoordinate)
					+ function2.evaluate(randomCoordinate);
			double sum1 = sumFunction.evaluate(randomCoordinate);

			if (Math.abs(sum - sum1) > 1e-5) {
				System.err.println("Error, summation incorrect. " + sum + " "
						+ sum1);

				// new TwoDimensionalTriangulationGUI("f1").draw(function1);
				// new TwoDimensionalTriangulationGUI("f2").draw(function2);
				// new TwoDimensionalTriangulationGUI("sum").draw(sumFunction);

				return;
			}
		}
	}

	public static void printFunctionEvaluation(
			MultiVariatePieceWiseLinearFunction function) {
		printFunctionEvaluation(function, 0.5);
	}

	public static void printFunctionEvaluation(
			MultiVariatePieceWiseLinearFunction function, double precision) {
		Validate.isTrue(function.getDimensionCount() == 2);

		double x1Start = function.getDomain().getDomainStart(0);
		double x1End = function.getDomain().getDomainEnd(0);

		double x2Start = function.getDomain().getDomainStart(1);
		double x2End = function.getDomain().getDomainEnd(1);

		for (double x1 = x1Start; x1 <= x1End; x1 += precision) {
			for (double x2 = x2Start; x2 <= x2End; x2 += precision) {
				System.out.println(x1 + " " + x2 + " "
						+ function.evaluate(x1, x2));
			}
		}
	}
}
