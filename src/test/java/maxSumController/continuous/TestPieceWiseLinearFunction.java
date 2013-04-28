package maxSumController.continuous;

import junit.framework.TestCase;

public class TestPieceWiseLinearFunction extends TestCase {

	private static final double EPSILON = 1e-6;

	public void testEvaluateNoSegments() throws Exception {
		PieceWiseLinearFunctionImpl function = new PieceWiseLinearFunctionImpl();

		assertEquals(Double.NaN, function.evaluate(0));
	}

	public void testEvaluateSingleSegment() throws Exception {
		PieceWiseLinearFunctionImpl function = new PieceWiseLinearFunctionImpl(
				new LineSegment(0, 0, 1, 1));

		assertEquals(Double.NaN, function.evaluate(-1.0));
		assertEquals(0.0, function.evaluate(0));
		assertEquals(0.5, function.evaluate(0.5));
		assertEquals(1.0, function.evaluate(1.0));
		assertEquals(Double.NaN, function.evaluate(2.0));
	}

	public void testEvaluateTwoConsecutiveSegments() throws Exception {
		PieceWiseLinearFunctionImpl function = new PieceWiseLinearFunctionImpl(
				new LineSegment(0, 0, 1, 1));

		function.addSegment(new LineSegment(1, 1, 2, 3));

		assertEquals(Double.NaN, function.evaluate(-1.0));
		assertEquals(0.0, function.evaluate(0));
		assertEquals(0.5, function.evaluate(0.5));
		assertEquals(1.0, function.evaluate(1.0));
		assertEquals(2.0, function.evaluate(1.5));
		assertEquals(3.0, function.evaluate(2.0));
		assertEquals(Double.NaN, function.evaluate(3.0));
	}

	public void testEvaluateTwoDiscontinuousSegments() throws Exception {
		PieceWiseLinearFunctionImpl function = new PieceWiseLinearFunctionImpl(
				new LineSegment(0, 0, 1, 1));

		function.addSegment(new LineSegment(1, 0, 2, 3));

		assertEquals(Double.NaN, function.evaluate(-1.0));
		assertEquals(0.0, function.evaluate(0));
		assertEquals(0.5, function.evaluate(0.5));
		assertEquals(0.0, function.evaluate(1.0));
		assertEquals(1.5, function.evaluate(1.5));
		assertEquals(3.0, function.evaluate(2.0));
		assertEquals(Double.NaN, function.evaluate(3.0));
	}

	public void testEvaluateNonConsecutiveSegments() throws Exception {
		PieceWiseLinearFunctionImpl function = new PieceWiseLinearFunctionImpl(
				new LineSegment(0, 0, 1, 1));

		function.addSegment(new LineSegment(2, 0, 3, 2));

		assertEquals(Double.NaN, function.evaluate(-1.0));
		assertEquals(0.0, function.evaluate(0));
		assertEquals(0.5, function.evaluate(0.5));
		assertEquals(1.0, function.evaluate(1.0));
		assertEquals(Double.NaN, function.evaluate(1.5));
		assertEquals(0.0, function.evaluate(2.0));
		assertEquals(1.0, function.evaluate(2.5));
		assertEquals(2.0, function.evaluate(3.0));
		assertEquals(Double.NaN, function.evaluate(4.0));
	}

	public void testIllegalOverlappingSegments() throws Exception {
		LineSegment lineSegment = new LineSegment(0, 0, 2, 2);
		PieceWiseLinearFunctionImpl function = new PieceWiseLinearFunctionImpl(
				lineSegment);

		assertEquals(function.getLineSegment(0.0), lineSegment);
		assertEquals(function.getLineSegment(2.0), lineSegment);
		assertEquals(function.getLineSegment(0.5), lineSegment);
		assertEquals(function.getLineSegment(1.5), lineSegment);
		assertNull(function.getLineSegment(-1.0));
		assertNull(function.getLineSegment(3.0));

		assertTrue(lineSegment.overlaps(new LineSegment(0.5, 0, 1.5, 2)));

		try {
			function.addSegment(new LineSegment(1, 0, 3, 2));
			fail();
		} catch (IllegalArgumentException e) {
		}

		try {
			function.addSegment(new LineSegment(-1, 0, 1, 2));
			fail();
		} catch (IllegalArgumentException e) {
		}

		try {
			function.addSegment(new LineSegment(-1, 0, 3, 0));
			fail();
		} catch (IllegalArgumentException e) {
		}

		try {
			function.addSegment(new LineSegment(0.5, 0, 1.5, 2));
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	public void testSimplify() throws Exception {
		PieceWiseLinearFunction function = new PieceWiseLinearFunctionImpl();

		function.addSegment(new LineSegment(0, 0, 1, 1));
		function.addSegment(new LineSegment(1, 1, 2, 2));
		function.simplify();
		assertEquals(2, function.getIntervalEndpoints().size());

		function = new PieceWiseLinearFunctionImpl();

		function.addSegment(new LineSegment(0, 0, 1, 1));
		function.addSegment(new LineSegment(1, 1, 2, 2));
		function.addSegment(new LineSegment(2, 2, 3, 3));
		function.simplify();

		assertEquals(2, function.getIntervalEndpoints().size());

		function = new PieceWiseLinearFunctionImpl();

		function.addSegment(new LineSegment(0, 0, 1, 1));
		function.addSegment(new LineSegment(1, 1, 2, 2));
		function.addSegment(new LineSegment(3, 3, 4, 4));
		function.simplify();

		assertEquals(4, function.getIntervalEndpoints().size());
	}

	public void testSum() throws Exception {
		PieceWiseLinearFunction function1 = new PieceWiseLinearFunctionImpl();
		PieceWiseLinearFunction function2 = new PieceWiseLinearFunctionImpl();

		// coinciding functions
		function1.addSegment(new LineSegment(0, 0, 1, 1));
		function2.addSegment(new LineSegment(0, 1, 1, 0));

		PieceWiseLinearFunction sum = function1.add(function2);
		assertEquals(1.0, sum.evaluate(0.0));
		assertEquals(1.0, sum.evaluate(1.0));

		// connecting linesegments
		function1 = new PieceWiseLinearFunctionImpl();
		function2 = new PieceWiseLinearFunctionImpl();

		function1.addSegment(new LineSegment(0, 0, 1, 1));
		function2.addSegment(new LineSegment(1, 1, 2, 2));

		sum = function1.add(function2);
		assertEquals(0.0, sum.evaluate(0.0));
		assertEquals(1.0, sum.evaluate(1.0));
		assertEquals(2.0, sum.evaluate(2.0));

		// non overlapping
		function1 = new PieceWiseLinearFunctionImpl();
		function2 = new PieceWiseLinearFunctionImpl();

		function1.addSegment(new LineSegment(0, 0, 1, 1));
		function2.addSegment(new LineSegment(2, 2, 3, 3));

		sum = function1.add(function2);
		assertEquals(0.0, sum.evaluate(0.0));
		assertEquals(1.0, sum.evaluate(1.0));
		assertEquals(Double.NaN, sum.evaluate(1.5));
		assertEquals(2.0, sum.evaluate(2.0));
		assertEquals(3.0, sum.evaluate(3.0));

		// partially overlapping
		function1 = new PieceWiseLinearFunctionImpl();
		function2 = new PieceWiseLinearFunctionImpl();

		function1.addSegment(new LineSegment(0, 0, 2, 2));
		function2.addSegment(new LineSegment(1, 0, 3, 2));

		sum = function1.add(function2);
		assertEquals(0.0, sum.evaluate(0.0));
		assertEquals(1.0, sum.evaluate(1.0));
		assertEquals(3.0, sum.evaluate(2.0 - EPSILON), 2 * EPSILON);
		assertEquals(1.0, sum.evaluate(2.0));
		assertEquals(2.0, sum.evaluate(3.0));
	}

	public void testNormalise() throws Exception {
		PieceWiseLinearFunction function1 = new PieceWiseLinearFunctionImpl();

		function1.addSegment(new LineSegment(0, 0, 1, 1));
		assertEquals(0.5, function1.getArea());
		function1.normalise();
		assertEquals(0.0, function1.getArea());

		function1 = new PieceWiseLinearFunctionImpl();
		function1.addSegment(new LineSegment(0, 0, 1, 1));
		function1.addSegment(new LineSegment(1, 0, 2, 1));

		assertEquals(1.0, function1.getArea());
		function1.normalise();
		assertEquals(0.0, function1.getArea());

		function1 = new PieceWiseLinearFunctionImpl();
		function1.addSegment(new LineSegment(0, 0, 1, 1));
		function1.addSegment(new LineSegment(1, 0, 2, 1));
		function1.addSegment(new LineSegment(6, 4, 10, 2));
		assertEquals(13.0, function1.getArea());
		function1.normalise();

		assertEquals(0.0, function1.getArea(), 1e-6);
	}
}
