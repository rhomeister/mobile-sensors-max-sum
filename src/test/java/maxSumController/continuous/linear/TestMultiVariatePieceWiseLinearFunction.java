package maxSumController.continuous.linear;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;
import maxSumController.continuous.LineSegment;

public class TestMultiVariatePieceWiseLinearFunction extends TestCase {

	NDimensionalPoint a = new NDimensionalPoint(0, 0);

	NDimensionalPoint b = new NDimensionalPoint(1, 0);

	NDimensionalPoint c = new NDimensionalPoint(0, 1);

	NDimensionalPoint d = new NDimensionalPoint(1, 1);

	NDimensionalPoint z = new NDimensionalPoint(0.5, 0.5);

	NDimensionalPoint x = new NDimensionalPoint(0, 0.5);

	NDimensionalPoint y = new NDimensionalPoint(1, 0.5);

	NCube domain = new NCube(new double[] { 0.0, 0.0 },
			new double[] { 1.0, 1.0 });

	MultiVariatePieceWiseLinearFunction function = new MultiVariatePieceWiseLinearFunction(
			domain);

	MultiVariatePieceWiseLinearFunction function1 = new MultiVariatePieceWiseLinearFunction(
			domain);

	MultiVariatePieceWiseLinearFunction function2 = new MultiVariatePieceWiseLinearFunction(
			domain);

	MultiVariatePieceWiseLinearFunction function3 = new MultiVariatePieceWiseLinearFunction(
			domain);

	MultiVariatePieceWiseLinearFunction function4 = new MultiVariatePieceWiseLinearFunction(
			domain);

	MultiVariatePieceWiseLinearFunction max12;

	MultiVariatePieceWiseLinearFunction max14;

	MultiVariatePieceWiseLinearFunction max21;

	MultiVariatePieceWiseLinearFunction max41;

	List<MultiVariatePieceWiseLinearFunction> maxFunctions;

	MultiVariatePieceWiseLinearFunction min12;

	MultiVariatePieceWiseLinearFunction min14;

	MultiVariatePieceWiseLinearFunction min21;

	MultiVariatePieceWiseLinearFunction min41;

	List<MultiVariatePieceWiseLinearFunction> minFunctions;

	@Override
	protected void setUp() throws Exception {
		function.setValue(a, 3);
		function.setValue(b, 4);
		function.setValue(c, 2);
		function.setValue(d, 0);

		function.addPartition(new NSimplex(a, b, c));
		function.addPartition(new NSimplex(b, c, d));

		// f1(x1, x2) = x1
		function1.setValue(a, 0.0);
		function1.setValue(b, 1.0);
		function1.setValue(c, 0.0);
		function1.setValue(d, 1.0);

		// f2(x1, x2) = x2
		function2.setValue(a, 0.0);
		function2.setValue(b, 0.0);
		function2.setValue(c, 1.0);
		function2.setValue(d, 1.0);

		// f3(x1, x2) = x1
		function3.setValue(a, 0.0);
		function3.setValue(b, 1.0);
		function3.setValue(c, 0.0);
		function3.setValue(d, 1.0);

		// f4(x1, x2) = x2
		function4.setValue(a, 0.0);
		function4.setValue(b, 0.0);
		function4.setValue(c, 1.0);
		function4.setValue(d, 1.0);

		// function 1 is partitioned by line x1 = x2
		function1.addPartition(new NSimplex(a, b, d));
		function1.addPartition(new NSimplex(a, c, d));

		// function 2 is partitioned by line x1 = x2
		function2.addPartition(new NSimplex(a, b, d));
		function2.addPartition(new NSimplex(a, c, d));

		// function 3 is partitioned by line x1 = 1 - x2
		function3.addPartition(new NSimplex(a, b, c));
		function3.addPartition(new NSimplex(b, c, d));

		// function 4 is partitioned by line x1 = 1 - x2
		function4.addPartition(new NSimplex(a, b, c));
		function4.addPartition(new NSimplex(b, c, d));

		max12 = function1.max(function2);
		max14 = function1.max(function4);
		max21 = function2.max(function1);
		max41 = function4.max(function1);
		maxFunctions = Arrays.asList(max12, max14, max21, max41);

		min12 = function1.min(function2);
		min14 = function1.min(function4);
		min21 = function2.min(function1);
		min41 = function4.min(function1);
		minFunctions = Arrays.asList(min12, min14, min21, min41);
	}

	public void testIntersections() throws Exception {
		NDimensionalPointSet intersections = function1
				.getIntersections(function4);

		for (NDimensionalPoint point : intersections) {
			assertEquals(function1.evaluate(point), function4.evaluate(point),
					1e-6);
		}
	}

	// public void testAdd2D() throws Exception {
	// MultiVariatePieceWiseLinearFunction result = function1.add(function4);
	//		
	// new TwoDimensionalTriangulationGUI().draw(result.getPartitioning());
	// Thread.sleep(10000);
	// }

	public void testEvaluate() throws Exception {
		assertEquals(3.0, function.evaluate(a));
		assertEquals(4.0, function.evaluate(b));
		assertEquals(2.0, function.evaluate(c));
		assertEquals(0.0, function.evaluate(d));
		assertEquals(3.0, function.evaluate(new NDimensionalPoint(0.5, 0.5)));
	}

	public void testProject() throws Exception {
		List<LineSegment> segments = function.project(0);
		assertTrue(segments.contains(new LineSegment(0, 2, 1, 0)));
		assertTrue(segments.contains(new LineSegment(0, 2, 1, 4)));
		assertTrue(segments.contains(new LineSegment(0, 3, 1, 4)));

		segments = function.project(1);
		assertTrue(segments.contains(new LineSegment(0, 3, 1, 2)));
		assertTrue(segments.contains(new LineSegment(0, 4, 1, 2)));
		assertTrue(segments.contains(new LineSegment(0, 4, 1, 0)));
	}

	// public void testAddRandom4DFunctions() throws Exception {
	// NCube domain = new NCube(new double[] { 0.0, 0.0, 0.0, 0.0 },
	// new double[] { 1.0, 2.0, 3.0, 4.0 });
	//
	// MultiVariatePieceWiseLinearFunction function1 =
	// createRandomFunction(domain);
	// MultiVariatePieceWiseLinearFunction function2 =
	// createRandomFunction(domain);
	//
	// MultiVariatePieceWiseLinearFunction sum1 = function1.add(function2);
	// MultiVariatePieceWiseLinearFunction sum2 = function2.add(function1);
	//
	// for (int i = 0; i < 50; i++) {
	// NDimensionalPoint randomCoordinate = domain.getRandomCoordinate();
	//
	// double sum = function1.evaluate(randomCoordinate)
	// + function2.evaluate(randomCoordinate);
	//
	// assertEquals(sum, sum1.evaluate(randomCoordinate), 1e-7);
	// assertEquals(sum, sum2.evaluate(randomCoordinate), 1e-7);
	// }
	// }

	// public void testAddRandom2DFunctions() throws Exception {
	// NCube domain = new NCube(new double[] { 0.0, 0.0 }, new double[] { 8.0,
	// 7.0 });
	//
	// MultiVariatePieceWiseLinearFunction function1 =
	// createRandomFunction(domain);
	// MultiVariatePieceWiseLinearFunction function2 =
	// createRandomFunction(domain);
	//
	// MultiVariatePieceWiseLinearFunction sum1 = function1.add(function2);
	// MultiVariatePieceWiseLinearFunction sum2 = function2.add(function1);
	//
	// for (int i = 0; i < 100; i++) {
	// NDimensionalPoint randomCoordinate = domain.getRandomCoordinate();
	//
	// double sum = function1.evaluate(randomCoordinate)
	// + function2.evaluate(randomCoordinate);
	//
	// assertEquals(sum, sum1.evaluate(randomCoordinate), 1e-7);
	// assertEquals(sum, sum2.evaluate(randomCoordinate), 1e-7);
	// }
	// }

	// public void testMaxRandom2DFunctions() throws Exception {
	//
	// NCube domain = new NCube(new double[] { 0.0, 0.0 }, new double[] { 8.0,
	// 7.0 });
	//
	// MultiVariatePieceWiseLinearFunction function1 =
	// createRandomFunction(domain);
	// MultiVariatePieceWiseLinearFunction function2 =
	// createRandomFunction(domain);
	//
	// MultiVariatePieceWiseLinearFunction max1 = function1.max(function2);
	// MultiVariatePieceWiseLinearFunction max2 = function2.max(function1);
	//
	// for (int i = 0; i < 100; i++) {
	// NDimensionalPoint randomCoordinate = domain.getRandomCoordinate();
	//
	// double max = Math.max(function1.evaluate(randomCoordinate),
	// function2.evaluate(randomCoordinate));
	//
	// double actual1 = max1.evaluate(randomCoordinate);
	// double actual2 = max2.evaluate(randomCoordinate);
	//
	// if (Math.abs(max - actual1) > 1e-7) {
	// System.out.println(randomCoordinate);
	//
	// NSimplex enclosingSimplex = max1.getPartitioning()
	// .getEnclosingSimplex(randomCoordinate);
	//
	// System.out.println("simplex");
	// System.out.println(enclosingSimplex);
	// System.out.println("coefficients");
	// System.out.println(Arrays.toString(enclosingSimplex
	// .getCoefficients(randomCoordinate)));
	// System.out.println("Values");
	// for (NDimensionalPoint point : enclosingSimplex.getPoints()) {
	// System.out.println(point + " " + max1.getValue(point));
	// }
	// }
	//
	// if (Math.abs(max - actual2) > 1e-7) {
	// System.out.println(randomCoordinate);
	//
	// NSimplex enclosingSimplex = max1.getPartitioning()
	// .getEnclosingSimplex(randomCoordinate);
	//
	// System.out.println("simplex");
	// System.out.println(enclosingSimplex);
	// System.out.println("coefficients");
	// System.out.println(Arrays.toString(enclosingSimplex
	// .getCoefficients(randomCoordinate)));
	// System.out.println("Values");
	// for (NDimensionalPoint point : enclosingSimplex.getPoints()) {
	// System.out.println(point + " " + max1.getValue(point));
	// }
	// }
	//
	// assertEquals(max, actual1, 1e-7);
	// assertEquals(max, actual2, 1e-7);
	// }
	// }

	public void testMaxMin() throws Exception {

		for (int i = 0; i < 100; i++) {
			double x1 = Math.random();
			double x2 = Math.random();

			for (MultiVariatePieceWiseLinearFunction max : maxFunctions) {
				assertEquals(Math.max(x1, x2), max.evaluate(x1, x2), 1e-5);
			}

			for (MultiVariatePieceWiseLinearFunction min : minFunctions) {
				assertEquals(x1 + " " + x2, Math.min(x1, x2), min.evaluate(x1,
						x2), 1e-5);
			}
		}
	}

	public void testMax2D() throws Exception {
		NCube domain = new NCube(new double[] { 0, 0 }, new double[] { 2, 2 });

		MultiVariatePieceWiseLinearFunction functionX1 = MultiVariatePieceWiseLinearFunctionUtilities
				.createUnivariateFunction(domain, 0).add(-1);

		MultiVariatePieceWiseLinearFunction functionX2 = MultiVariatePieceWiseLinearFunctionUtilities
				.createUnivariateFunction(domain, 1);

		MultiVariatePieceWiseLinearFunction maxFunction = functionX1
				.max(functionX2);

		MultiVariatePieceWiseLinearFunction function1 = MultiVariatePieceWiseLinearFunctionUtilities
				.createZeroFunction(domain).max(maxFunction);

		MultiVariatePieceWiseLinearFunction function2 = maxFunction
				.max(MultiVariatePieceWiseLinearFunctionUtilities
						.createZeroFunction(domain));

		for (int i = 0; i < 100; i++) {
			NDimensionalPoint coordinate = domain.getRandomCoordinate();

			double expected = Math.max(0, Math.max(
					coordinate.getCoordinates()[0] - 1, coordinate
							.getCoordinates()[1]));

			assertEquals(expected, function1.evaluate(coordinate), 1e-6);
			assertEquals(expected, function2.evaluate(coordinate), 1e-6);

		}
	}

	public void testMax3D() throws Exception {
		NCube domain = new NCube(new double[] { 0, 0, 0 }, new double[] { 1, 1,
				1 });

		MultiVariatePieceWiseLinearFunction functionX1 = MultiVariatePieceWiseLinearFunctionUtilities
				.createUnivariateFunction(domain, 0);

		MultiVariatePieceWiseLinearFunction functionX2 = MultiVariatePieceWiseLinearFunctionUtilities
				.createUnivariateFunction(domain, 1);

		MultiVariatePieceWiseLinearFunction functionX3 = MultiVariatePieceWiseLinearFunctionUtilities
				.createUnivariateFunction(domain, 2);

		MultiVariatePieceWiseLinearFunction max = functionX1.max(functionX2)
				.max(functionX3);

		for (int i = 0; i < 100; i++) {
			NDimensionalPoint coordinate = domain.getRandomCoordinate();

			double expected = Math.max(coordinate.getCoordinates()[0], Math
					.max(coordinate.getCoordinates()[1], coordinate
							.getCoordinates()[2]));

			assertEquals(expected, max.evaluate(coordinate), 1e-6);
		}
	}

	public void testAdd3D() throws Exception {
		NCube domain = new NCube(new double[] { 0, 0, 0 }, new double[] { 1, 1,
				1 });

		MultiVariatePieceWiseLinearFunction functionX1 = MultiVariatePieceWiseLinearFunctionUtilities
				.createUnivariateFunction(domain, 0);

		MultiVariatePieceWiseLinearFunction functionX2 = MultiVariatePieceWiseLinearFunctionUtilities
				.createUnivariateFunction(domain, 1);

		MultiVariatePieceWiseLinearFunction functionX3 = MultiVariatePieceWiseLinearFunctionUtilities
				.createUnivariateFunction(domain, 2);

		MultiVariatePieceWiseLinearFunction sum = functionX1.add(functionX2)
				.add(functionX3);

		for (int i = 0; i < 100; i++) {
			NDimensionalPoint coordinate = domain.getRandomCoordinate();

			double expected = coordinate.getCoordinates()[0]
					+ coordinate.getCoordinates()[1]
					+ coordinate.getCoordinates()[2];

			assertEquals(expected, sum.evaluate(coordinate), 1e-6);
		}
	}

	private static MultiVariatePieceWiseLinearFunction createRandomFunction(
			NCube domain) {
		MultiVariatePieceWiseLinearFunction function = MultiVariatePieceWiseLinearFunctionUtilities
				.createMultiDimensionalGridFunction(domain);

		for (int i = 0; i < 3; i++) {
			function.getPartitioning().split(domain.getRandomCoordinate());
		}

		for (NDimensionalPoint coordinate : function.getPartitioning()
				.getDefiningCoordinates()) {
			function.setValue(coordinate, Math.random());
		}

		return function;
	}

	public void testGetAdjacentParallelSimplexGroups() throws Exception {
		List<Set<NSimplex>> groups;

		groups = function.getAdjacentParallelSimplexGroups(a);
		assertEquals(1, groups.size());
		groups = function.getAdjacentParallelSimplexGroups(b);
		assertEquals(2, groups.size());
		groups = function.getAdjacentParallelSimplexGroups(c);
		assertEquals(2, groups.size());
		groups = function.getAdjacentParallelSimplexGroups(d);
		assertEquals(1, groups.size());

		groups = function1.getAdjacentParallelSimplexGroups(a);
		assertEquals(1, groups.size());
		assertEquals(2, groups.get(0).size());
		groups = function1.getAdjacentParallelSimplexGroups(b);
		assertEquals(1, groups.size());
		groups = function1.getAdjacentParallelSimplexGroups(c);
		assertEquals(1, groups.size());
		groups = function1.getAdjacentParallelSimplexGroups(d);
		assertEquals(1, groups.size());
		assertEquals(2, groups.get(0).size());
	}

	public void testGetAdjacentParallelSimplexGroups1() throws Exception {
		NCube domain = new NCube(new double[] { 0, 0 }, new double[] { 1, 1 });

		MultiVariatePieceWiseLinearFunction function = new MultiVariatePieceWiseLinearFunction(
				domain);

		function.addPartition(new NSimplex(a, b, z));
		function.addPartition(new NSimplex(a, x, z));
		function.addPartition(new NSimplex(b, y, z));
		function.addPartition(new NSimplex(d, y, z));
		function.addPartition(new NSimplex(c, d, z));
		function.addPartition(new NSimplex(c, x, z));

		List<Set<NSimplex>> groups = function
				.getAdjacentParallelSimplexGroups(z);

		assertEquals(1, groups.size());
		assertEquals(6, groups.get(0).size());

		function.setValue(x, 1);
		function.setValue(y, 1);

		groups = function.getAdjacentParallelSimplexGroups(z);

		assertEquals(6, groups.size());

		for (Set<NSimplex> list : groups) {
			assertEquals(1, list.size());
		}

		function = new MultiVariatePieceWiseLinearFunction(domain);

		function.addPartition(new NSimplex(a, b, z));
		function.addPartition(new NSimplex(a, x, z));
		function.addPartition(new NSimplex(b, d, z));
		function.addPartition(new NSimplex(c, d, z));
		function.addPartition(new NSimplex(c, x, z));

		function.setValue(x, 1);

		groups = function.getAdjacentParallelSimplexGroups(z);

		assertEquals(3, groups.size());

		for (Set<NSimplex> list : groups) {
			assertTrue(list.size() == 3 || list.size() == 1);
		}
	}

	public void testPartitionIntoConnectedSimplices() throws Exception {
		NSimplex simplex1 = new NSimplex(a, b, z);
		NSimplex simplex2 = new NSimplex(c, d, z);
		NSimplex simplex3 = new NSimplex(b, d, z);
		assertFalse(simplex1.hasSharedSurface(simplex2));

		List<Set<NSimplex>> partitionIntoConnectedSimplices = function
				.partitionIntoConnectedSimplices(Arrays.asList(simplex1,
						simplex2));
		assertEquals(2, partitionIntoConnectedSimplices.size());

		partitionIntoConnectedSimplices = function
				.partitionIntoConnectedSimplices(Arrays.asList(simplex2,
						simplex3));
		assertEquals(1, partitionIntoConnectedSimplices.size());

		partitionIntoConnectedSimplices = function
				.partitionIntoConnectedSimplices(Arrays.asList(simplex1,
						simplex2, simplex3));

		assertEquals(1, partitionIntoConnectedSimplices.size());
		assertEquals(3, partitionIntoConnectedSimplices.get(0).size());

	}

	public void test3DFunction() throws Exception {
		NCube domain = new NCube(new double[] { 0, 0, 0 }, new double[] { 1, 1,
				1 });

		MultiVariatePieceWiseLinearFunction x1 = MultiVariatePieceWiseLinearFunctionUtilities
				.createUnivariateFunction(domain, 0);

		MultiVariatePieceWiseLinearFunction x2 = MultiVariatePieceWiseLinearFunctionUtilities
				.createUnivariateFunction(domain, 1);

		MultiVariatePieceWiseLinearFunction x3 = MultiVariatePieceWiseLinearFunctionUtilities
				.createUnivariateFunction(domain, 2);

		MultiVariatePieceWiseLinearFunction max12 = x1.max(x2);
		MultiVariatePieceWiseLinearFunction max23 = x2.max(x3);

		MultiVariatePieceWiseLinearFunction max123 = max12.max(max23);

		for (int i = 0; i < 100; i++) {
			NDimensionalPoint coordinate = domain.getRandomCoordinate();

			double expected12 = Math.max(coordinate.getCoordinates()[0],
					coordinate.getCoordinates()[1]);
			double expected23 = Math.max(coordinate.getCoordinates()[1],
					coordinate.getCoordinates()[2]);

			double expected123 = Math.max(expected12, expected23);

			assertEquals(expected12, max12.evaluate(coordinate), 1e-6);
			assertEquals(expected23, max23.evaluate(coordinate), 1e-6);
			assertEquals(expected123, max123.evaluate(coordinate), 1e-6);
		}
	}

	public void testSimplify2D() throws Exception {
		MultiVariatePieceWiseLinearFunction function = new MultiVariatePieceWiseLinearFunction(
				domain);

		function.addPartition(new NSimplex(a, b, c));
		function.addPartition(new NSimplex(b, c, d));
		NDimensionalPoint point = new NDimensionalPoint(0.2, 0.2);
		function.getPartitioning().split(point);
		function.setValue(point, 0.0);

		function.simplify(true);

		assertEquals(2, function.getPartitioning().getSimplices().size());
		assertEquals(4, function.getPartitioning().getDefiningCoordinates()
				.size());

		function = new MultiVariatePieceWiseLinearFunction(domain);
		function.addPartition(new NSimplex(a, b, c));
		function.addPartition(new NSimplex(b, c, d));

		point = new NDimensionalPoint(0.5, 0.5);
		function.getPartitioning().split(point);
		function.setValue(point, 0.0);

		assertEquals(4, function.getPartitioning().getSimplices().size());
		assertEquals(5, function.getPartitioning().getDefiningCoordinates()
				.size());

		function.simplify(true);

		assertEquals(2, function.getPartitioning().getSimplices().size());
		assertEquals(4, function.getPartitioning().getDefiningCoordinates()
				.size());
	}

	public void testSimplify3D() throws Exception {
		NCube domain = new NCube(new double[] { 0.0, 0.0, 0.0 }, new double[] {
				1.0, 1.0, 1.0 });

		MultiVariatePieceWiseLinearFunction function = MultiVariatePieceWiseLinearFunctionUtilities
				.createZeroFunction(domain);

		int originalSimplexCount = function.getSimplexCount();

		NSimplex simplex = function.getPartitioning().getSimplices().get(0);

		NDimensionalPoint[] points = simplex.getPoints();

		// split on line of tetrahedron
		NDimensionalPoint splitPoint = points[0].add(points[1]).times(0.5);
		function.getPartitioning().split(splitPoint);
		function.setValue(splitPoint, 0.0);
		assertEquals(originalSimplexCount + 1, function.getSimplexCount());
		function.simplify(true);
		assertEquals(originalSimplexCount, function.getSimplexCount());

		// split on surface of tetrahedron
		splitPoint = points[0].add(points[1]).add(points[2]).times(1.0 / 3.0);
		function.getPartitioning().split(splitPoint);
		function.setValue(splitPoint, 0.0);
		assertEquals(originalSimplexCount + 2, function.getSimplexCount());
		function.simplify(true);
		assertEquals(originalSimplexCount, function.getSimplexCount());

		// split inside tetrahedron
		splitPoint = points[0].add(points[1]).add(points[2]).add(points[3])
				.times(1.0 / 4.0);
		function.getPartitioning().split(splitPoint);
		function.setValue(splitPoint, 0.0);
		assertEquals(originalSimplexCount + 3, function.getSimplexCount());
		function.simplify(true);
		assertEquals(originalSimplexCount, function.getSimplexCount());

	}

	// public void testIntersectionsComplexFunction() throws Exception {
	// NCube domain = new NCube(new double[] { 0, 0 }, new double[] { 8, 7 });
	//
	// MultiVariatePieceWiseLinearFunction zero =
	// MultiVariatePieceWiseLinearFunctionFactory
	// .createZeroFunction(domain);
	//
	// MultiVariatePieceWiseLinearFunction x1 =
	// MultiVariatePieceWiseLinearFunctionFactory
	// .createUnivariateFunction(domain, 0);
	//
	// MultiVariatePieceWiseLinearFunction x2 =
	// MultiVariatePieceWiseLinearFunctionFactory
	// .createUnivariateFunction(domain, 1);
	//
	// MultiVariatePieceWiseLinearFunction x1Plusl1 = x1.add(2.0);
	//
	// MultiVariatePieceWiseLinearFunction x2Plusl2 = x2.add(3.0);
	//
	// MultiVariatePieceWiseLinearFunction minEnd = x1Plusl1.min(x2Plusl2);
	// MultiVariatePieceWiseLinearFunction maxStart = x1.max(x2);
	//
	// MultiVariatePieceWiseLinearFunction function = minEnd
	// .subtract(maxStart);
	//
	// // Set<NDimensionalPoint> intersections =
	// // function.getIntersections(zero);
	// //
	// // for (NDimensionalPoint point : intersections) {
	// // assertEquals(function.evaluate(point), zero.evaluate(point),
	// // 1e-6);
	// //
	// // System.out.println(point + " " + function.evaluate(point));
	// // }
	//
	// // NSimplex simplex = function.getPartitioning().getEnclosingSimplex(
	// // new NDimensionalPoint(1, 5));
	// //
	// // simplex = simplex.createValueSimplex(function.getValues());
	//
	// // System.out.println(simplex);
	//
	// // NSimplex simplex2 = zero.getPartitioning().getEnclosingSimplex(
	// // new NDimensionalPoint(1, 5));
	// // simplex2 = simplex2.createValueSimplex(zero.getValues());
	//
	// // System.out.println(simplex2);
	//
	// // System.out.println(simplex.getIntersections(simplex2));
	//
	// // Set<NDimensionalLine> edges = simplex.getEdges();
	// // System.out.println(simplex);
	// //
	// // NDimensionalLine line = null;
	// //
	// // for (NDimensionalLine line2 : edges) {
	// // double ratio = line2.getEnd().getCoordinates()[0] / 0.875;
	// //
	// // if(line2.getStart().getCoordinates()[0] == 0.0 && ratio < 1.01 &&
	// // ratio > 0.99) {
	// // line = line2;
	// // break;
	// // }
	// // }
	//
	// // System.out.println(line);
	// //
	// // System.out.println(simplex2.getIntersections(line));
	// // System.out.println(simplex2);
	// //
	//
	// //function = zero.max(function); //.max(zero);// .add(zero);
	//		
	// function = function.max(zero);
	//		
	//
	// //
	// SimplexPartitioning partitioning = function.getPartitioning();
	//
	// // SimplexPartitioning partitioning =
	// // zero.getPartitioning().union(function.getPartitioning(), true);
	//
	// // System.out.println(partitioning.getDefiningCoordinates().size());
	// // System.out.println(partitioning.getSimplices().size());
	//
	// // NSimplex simplex1 = function.getPartitioning().getEnclosingSimplex(
	// // new NDimensionalPoint(4, 1.5));
	// // simplex1 = simplex1.createValueSimplex(function.getValues());
	// //
	// // // System.out.println(simplex1);
	// //
	// // NSimplex simplex2 = zero.getPartitioning().getEnclosingSimplex(
	// // new NDimensionalPoint(4, 1.5));
	// // simplex2 = simplex2.createValueSimplex(zero.getValues());
	// // System.out.println(simplex2);
	// //
	// // System.out.println(simplex2.getIntersections(simplex1));
	//
	// // System.out.println(function.getIntersections(zero).size());
	// // System.out.println(zero.getIntersections(function).size());
	// // System.out.println(simplex2);
	//
	// // 10-9
	// // 33
	// // 26
	//
	// // 32
	// // 24
	//
	// // SimplexPartitioning partitioning =
	// // function.getPartitioning().union(zero.getPartitioning());
	//
	// // partitioning.split(new NDimensionalLine(new NDimensionalPoint(8,0),
	// // new NDimensionalPoint(0,7)));
	// //
	// //
	// //
	// TwoDimensionalTriangulationGUI gui = new
	// TwoDimensionalTriangulationGUI();
	// gui.draw(partitioning);
	// // //
	// // // for (NDimensionalPoint point : function.getIntersections(zero))
	// // // gui.draw(point);
	// //
	// Thread.sleep(100000);
	// //
	// //
	// //
	// // //new TwoDimensionalTriangulationGUI().draw(Arrays.asList(simplex,
	// // simplex2), domain);
	// //
	// }

	public void testFinePartitioning() throws Exception {

		MultiVariatePieceWiseLinearFunction function = MultiVariatePieceWiseLinearFunctionUtilities
				.createZeroFunction(domain);

		NDimensionalPoint a = new NDimensionalPoint(0.5, 0.1);
		NDimensionalPoint b = new NDimensionalPoint(0.5, 0.10001);

		function.setValue(a, 1.0);
		function.setValue(b, 0.0);

		function.getPartitioning().split(a);
		function.getPartitioning().split(b);

		double e = function.evaluate(0.5, 0.100005);
		System.out.println(e);

	}

	public static void main(String[] args) {

		// // [5.850326097126796, 3.1550042754097074]
		// // simplex
		// // {[5.247791330635618, 5.734677997834186], [5.18955334809626,
		// // 5.140151032573314], [6.345984917793688, 1.587701073234756]}
		// // coefficients
		// // [0.057775880085395366, 0.3737443105066576, 0.5684798094079471]
		// // Values
		// // [5.247791330635618, 5.734677997834186] 0.5889256984100878
		// // [5.18955334809626, 5.140151032573314] 0.5832149889119114
		// // [6.345984917793688, 1.587701073234756] 0.6443441006328171
		// //
		// //
		//
		// NDimensionalPoint xx = new NDimensionalPoint(5.247791330635618,
		// 5.734677997834186);
		// NDimensionalPoint xy = new NDimensionalPoint(5.18955334809626,
		// 5.140151032573314);
		// NDimensionalPoint xz = new NDimensionalPoint(6.345984917793688,
		// 1.587701073234756);
		//
		// NSimplex simplex = new NSimplex(xx, xy, xz);
		// NCube domain = new NCube(new double[] { 0.0, 0.0 }, new double[] {
		// 8.0,
		// 7.0 });
		// TwoDimensionalTriangulationGUI gui = new
		// TwoDimensionalTriangulationGUI();
		// gui.draw(domain, simplex);
		//
		// NDimensionalPoint point = new NDimensionalPoint(5.850326097126796,
		// 3.1550042754097074);
		// gui.draw(point);
		//
		// System.out.println(Arrays.toString(simplex.getCoefficients(point)));

		NCube domain = new NCube(new double[] { 0.0, 0.0 }, new double[] { 1.0,
				1.0 });

		for (int j = 0; j < 1000; j++) {
			System.out.println(j);

			MultiVariatePieceWiseLinearFunction function1 = createRandomFunction(domain);
			MultiVariatePieceWiseLinearFunction function2 = createRandomFunction(domain);

			MultiVariatePieceWiseLinearFunction sum1 = function1.add(function2);

			for (int i = 0; i < 100; i++) {
				NDimensionalPoint randomCoordinate = domain
						.getRandomCoordinate();

				double actual = function1.evaluate(randomCoordinate)
						+ function2.evaluate(randomCoordinate);
				double expected = sum1.evaluate(randomCoordinate);

				if (Math.abs(actual - expected) > 1e-7) {
					TwoDimensionalTriangulationGUI gui1 = new TwoDimensionalTriangulationGUI();
					gui1.draw(function1);

					System.out.println(randomCoordinate);

					TwoDimensionalTriangulationGUI gui2 = new TwoDimensionalTriangulationGUI();
					TwoDimensionalTriangulationGUI guiSum = new TwoDimensionalTriangulationGUI();

					gui2.draw(function2);
					guiSum.draw(sum1);
					guiSum.draw(randomCoordinate, Color.red);
				}

				assertEquals(actual, expected, 1e-7);
			}
		}
	}
}
