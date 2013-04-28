package maxSumController.continuous;

import junit.framework.TestCase;

public class TestLineSegment extends TestCase {

	public void testSingleIntersection() throws Exception {
		LineSegment ls1 = new LineSegment(0, 0, 1, 1);
		LineSegment ls2 = new LineSegment(0, 1, 1, 0);

		assertEquals(0.5, ls1.intersection(ls2));
		assertTrue(ls1.overlaps(ls2));

		ls1 = new LineSegment(0, 0, 1, 2);

		assertEquals(1 / 3., ls1.intersection(ls2));
		assertTrue(ls1.overlaps(ls2));
	}

	public void testSingleIntersectionOnBoundary() throws Exception {
		LineSegment ls1 = new LineSegment(0, 0, 1, 1);
		LineSegment ls2 = new LineSegment(0, 1, 1, 1);

		assertEquals(1.0, ls1.intersection(ls2));
		assertTrue(ls1.overlaps(ls2));

		ls1 = new LineSegment(0, 0, 1, 1);
		ls2 = new LineSegment(1, 1, 2, 1);

		assertEquals(1.0, ls1.intersection(ls2));
		assertFalse(ls1.overlaps(ls2));
	}

	public void testNoIntersection() throws Exception {
		LineSegment ls1 = new LineSegment(0, 3, 1, 1);
		LineSegment ls2 = new LineSegment(0, 1, 1, 0);

		assertEquals(Double.NaN, ls1.intersection(ls2));
		assertTrue(ls1.overlaps(ls2));
	}

	public void testNoIntersectionParallelLines() throws Exception {
		LineSegment ls1 = new LineSegment(0, 1, 1, 2);
		LineSegment ls2 = new LineSegment(0, 0, 1, 1);

		assertEquals(Double.NaN, ls1.intersection(ls2));
		assertTrue(ls1.overlaps(ls2));
	}

	public void testInfiniteIntersections() throws Exception {
		LineSegment ls1 = new LineSegment(0, 0, 2, 2);
		LineSegment ls2 = new LineSegment(0, 0, 1, 1);

		assertEquals(Double.NaN, ls1.intersection(ls2));
		assertTrue(ls1.overlaps(ls2));
	}

	public void testArea() throws Exception {
		LineSegment segment = new LineSegment(0, 3, 4, 2);

		assertEquals(10.0, segment.getArea());
		assertEquals(4.0, segment.getDomainLength());
	}
}
