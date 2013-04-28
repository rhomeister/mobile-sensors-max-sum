package maxSumController.continuous.linear;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.commons.lang.ArrayUtils;

import Jama.Matrix;

public class TestNSimplex extends TestCase {

	public void testCoefficients() throws Exception {
		NSimplex simplex = new NSimplex(2);

		simplex.addPoint(new NDimensionalPoint(0, 0));
		simplex.addPoint(new NDimensionalPoint(1, 0));
		simplex.addPoint(new NDimensionalPoint(0, 1));

		double[] coefficients;
		coefficients = simplex.getCoefficients(new NDimensionalPoint(0, 0));
		assertTrue(Arrays.equals(new double[] { 1, 0, 0 }, coefficients));

		coefficients = simplex.getCoefficients(new NDimensionalPoint(1, 0));
		assertTrue(ArrayUtils.toString(coefficients), Arrays.equals(
				new double[] { 0, 1, 0 }, coefficients));

		coefficients = simplex.getCoefficients(new NDimensionalPoint(0, 1));
		assertTrue(Arrays.equals(new double[] { 0, 0, 1 }, coefficients));

		coefficients = simplex.getCoefficients(new NDimensionalPoint(0.5, 0.5));
		assertTrue(Arrays.equals(new double[] { 0, 0.5, 0.5 }, coefficients));

		coefficients = simplex.getCoefficients(new NDimensionalPoint(0.0, 0.5));
		assertTrue(Arrays.equals(new double[] { 0.5, 0.0, 0.5 }, coefficients));
	}

	public void testLinearlyDependentSimplices() throws Exception {
		NSimplex simplex = new NSimplex(2);

		try {
			simplex.addPoint(new NDimensionalPoint(0, 0));
			simplex.addPoint(new NDimensionalPoint(1, 0));
			simplex.addPoint(new NDimensionalPoint(5, 0));
			fail();
		} catch (IllegalArgumentException e) {

		}

		simplex = new NSimplex(3);

		try {
			simplex.addPoint(new NDimensionalPoint(0, 0, 0));
			simplex.addPoint(new NDimensionalPoint(1, 0, 0));
			simplex.addPoint(new NDimensionalPoint(0, 1, 0));
			simplex.addPoint(new NDimensionalPoint(0.5, 0.5, 0));
			fail();
		} catch (IllegalArgumentException e) {

		}

	}

	public void testGetIntersections1() throws Exception {
		NSimplex simplex = new NSimplex(2);

		simplex.addPoint(new NDimensionalPoint(0, 0));
		simplex.addPoint(new NDimensionalPoint(1, 0));
		simplex.addPoint(new NDimensionalPoint(0, 1));

		NDimensionalPoint start = new NDimensionalPoint(0, 0);
		NDimensionalPoint end = new NDimensionalPoint(1, 1);
		NDimensionalLine line = new NDimensionalLine(start, end);

		NDimensionalPointSet intersections = simplex.getIntersections(line);

		assertEquals(2, intersections.size());
		assertTrue(intersections.contains(new NDimensionalPoint(0.0, 0.0)));
		assertTrue(intersections.contains(new NDimensionalPoint(0.5, 0.5)));

		end = new NDimensionalPoint(0, 1);
		line = new NDimensionalLine(start, end);

		intersections = simplex.getIntersections(line);
		assertEquals(2, intersections.size());
		assertTrue(intersections.contains(new NDimensionalPoint(0.0, 0.0)));
		assertTrue(intersections.contains(new NDimensionalPoint(0.0, 1.0)));

		start = new NDimensionalPoint(0, 0);
		end = new NDimensionalPoint(0, -1);
		line = new NDimensionalLine(start, end);
		intersections = simplex.getIntersections(line);
		assertEquals(1, intersections.size());
		assertTrue(intersections.contains(new NDimensionalPoint(0.0, 0.0)));

		start = new NDimensionalPoint(-1, 1);
		end = new NDimensionalPoint(1, 1);
		line = new NDimensionalLine(start, end);
		intersections = simplex.getIntersections(line);
		assertEquals(1, intersections.size());
		assertTrue(intersections.contains(new NDimensionalPoint(0.0, 1.0)));

		start = new NDimensionalPoint(-1, -11);
		end = new NDimensionalPoint(2, 1);
		line = new NDimensionalLine(start, end);
		intersections = simplex.getIntersections(line);
		assertEquals(0, intersections.size());

		simplex = new NSimplex(3);

		simplex.addPoint(new NDimensionalPoint(0, 0, 0));
		simplex.addPoint(new NDimensionalPoint(1, 0, 0));
		simplex.addPoint(new NDimensionalPoint(0, 1, 0));
		simplex.addPoint(new NDimensionalPoint(0, 0, 1));

		start = new NDimensionalPoint(0, 0, 0);
		end = new NDimensionalPoint(1, 1, 1);
		line = new NDimensionalLine(start, end);
		intersections = simplex.getIntersections(line);
		assertEquals(2, intersections.size());
		assertTrue(intersections.contains(new NDimensionalPoint(0.0, 0.0, 0.0)));
		assertTrue(intersections.contains(new NDimensionalPoint(
				0.33333333333333337, 0.33333333333333337, 0.33333333333333337)));
	}

	public void testSplit() throws Exception {
		NSimplex simplex = new NSimplex(2);

		simplex.addPoint(new NDimensionalPoint(0, 0));
		simplex.addPoint(new NDimensionalPoint(2, 0));
		simplex.addPoint(new NDimensionalPoint(0, 2));

		Set<NSimplex> simplices = simplex
				.split(new NDimensionalPoint(0.5, 0.5));
		assertEquals(3, simplices.size());

		simplices = simplex.split(new NDimensionalPoint(1, 1));
		assertEquals(2, simplices.size());

		simplices = simplex.split(new NDimensionalPoint(0, 0));
		assertEquals(1, simplices.size());
	}

	public void testSplitAndContains() throws Exception {
		NDimensionalPoint b = new NDimensionalPoint(1, 0);
		NDimensionalPoint d = new NDimensionalPoint(1, 1);
		NDimensionalPoint e = new NDimensionalPoint(0.2, 0.8);
		NDimensionalPoint q = new NDimensionalPoint(0.5, 0.5);

		NSimplex simplex = new NSimplex(b, d, e);

		assertTrue(simplex.contains(q));

		Set<NSimplex> split = simplex.split(d);
		assertEquals(1, split.size());
		NSimplex splitSimplex = split.iterator().next();
		assertTrue(simplex.equals(splitSimplex));
		assertTrue(splitSimplex.equals(simplex));

		assertTrue(splitSimplex.contains(q));
	}

	public void testGetIntersections2() throws Exception {
		// N simplices in N+1 dimensions
		NSimplex simplex1 = new NSimplex(2);

		simplex1.addPoint(new NDimensionalPoint(0, 0, 1));
		simplex1.addPoint(new NDimensionalPoint(1, 0, 0));
		simplex1.addPoint(new NDimensionalPoint(0, 1, 0));

		NSimplex simplex2 = new NSimplex(2);

		simplex2.addPoint(new NDimensionalPoint(0, 0, 0));
		simplex2.addPoint(new NDimensionalPoint(1, 0, 1));
		simplex2.addPoint(new NDimensionalPoint(0, 1, 1));

		NSimplex simplex3 = new NSimplex(2);

		simplex3.addPoint(new NDimensionalPoint(0, 0, 0));
		simplex3.addPoint(new NDimensionalPoint(1, 0, -1));
		simplex3.addPoint(new NDimensionalPoint(0, 1, -1));

		Collection<NDimensionalPoint> intersections = simplex1
				.getIntersections(simplex2);

		assertEquals(2, intersections.size());

		assertTrue(intersections.contains(new NDimensionalPoint(0.5, 0.0)));
		assertTrue(intersections.contains(new NDimensionalPoint(0.0, 0.5)));

		assertEquals(0, simplex1.getIntersections(simplex3).size());

		assertEquals(1, simplex2.getIntersections(simplex3).size());
	}

	public void testGetIntersections3() throws Exception {
		// N simplices in N+1 dimensions
		NSimplex simplex1 = new NSimplex(2);

		simplex1.addPoint(new NDimensionalPoint(0, 0, 0));
		simplex1.addPoint(new NDimensionalPoint(0, 1, 1));
		simplex1.addPoint(new NDimensionalPoint(1, 1, 1));

		NSimplex simplex2 = new NSimplex(2);

		simplex2.addPoint(new NDimensionalPoint(0, 0, 0));
		simplex2.addPoint(new NDimensionalPoint(1, 0, 0));
		simplex2.addPoint(new NDimensionalPoint(1, 1, 1));

		NSimplex simplex3 = new NSimplex(2);

		simplex3.addPoint(new NDimensionalPoint(0, 0, 0));
		simplex3.addPoint(new NDimensionalPoint(0, 1, 0));
		simplex3.addPoint(new NDimensionalPoint(1, 0, 1));

		NDimensionalPoint a = new NDimensionalPoint(0.0, 0.0);
		// NDimensionalPoint b = new NDimensionalPoint(1.0, 1.0);
		NDimensionalPoint c = new NDimensionalPoint(.5, .5);

		Collection<NDimensionalPoint> intersections = simplex1
				.getIntersections(simplex2);

		// only intersects on endpoints
		assertEquals(0, intersections.size());

		intersections = simplex2.getIntersections(simplex1);
		assertEquals(0, intersections.size());

		intersections = simplex1.getIntersections(simplex3);
		assertTrue(intersections.contains(a));
		assertTrue(intersections.contains(c));
		assertEquals(2, intersections.size());

		intersections = simplex3.getIntersections(simplex1);
		assertTrue(intersections.contains(a));
		assertTrue(intersections.contains(c));
		assertEquals(2, intersections.size());

		intersections = simplex2.getIntersections(simplex3);
		assertTrue(intersections.contains(a));
		assertTrue(intersections.contains(c));
		assertEquals(2, intersections.size());

		intersections = simplex3.getIntersections(simplex2);
		assertTrue(intersections.contains(a));
		assertTrue(intersections.contains(c));
		assertEquals(2, intersections.size());
	}

	public void testGetIntersections4() throws Exception {
		NSimplex simplex1 = new NSimplex(2);

		simplex1.addPoint(new NDimensionalPoint(0, 0, 1));
		simplex1.addPoint(new NDimensionalPoint(1, 0, 0));
		simplex1.addPoint(new NDimensionalPoint(0, 1, 0));

		NDimensionalLine line = new NDimensionalLine(new NDimensionalPoint(0,
				0, 0), new NDimensionalPoint(1, 1, 1));

		NDimensionalPointSet intersections = simplex1.getIntersections(line);

		assertEquals(1, intersections.size());

		NDimensionalPoint point = intersections.iterator().next();
		assertEquals(1.0 / 3.0, point.getCoordinates()[0], 1e-6);
		assertEquals(1.0 / 3.0, point.getCoordinates()[1], 1e-6);
	}

	public void testGetIntersectionsAlmostColinear() throws Exception {
		NSimplex simplex = new NSimplex(2);
		simplex.addPoint(new NDimensionalPoint(0, 0, 0));
		simplex.addPoint(new NDimensionalPoint(1, 0, 0));
		simplex.addPoint(new NDimensionalPoint(0, 1, 0));

		NDimensionalLine line = new NDimensionalLine(new NDimensionalPoint(-1,
				-1, -1.5e12), new NDimensionalPoint(1, 1, 1e12));

		assertTrue(simplex.getIntersections(line).isEmpty());

	}

	public void testIsParallel() throws Exception {
		NSimplex simplex1 = new NSimplex(new NDimensionalPoint(0, 0, 1),
				new NDimensionalPoint(1, 0, 0), new NDimensionalPoint(0, 1, 0));

		NSimplex simplex2 = new NSimplex(new NDimensionalPoint(1, 0, 0),
				new NDimensionalPoint(2, 0, -1),
				new NDimensionalPoint(1, 1, -1));

		assertTrue(simplex1.isParallel(simplex1));
		assertTrue(simplex1.isParallel(simplex2));
		assertTrue(simplex2.isParallel(simplex1));
		assertTrue(simplex2.isParallel(simplex2));
	}

	public void testGetNormalVector() throws Exception {
		NSimplex simplex = new NSimplex(new NDimensionalPoint(0, 0, 0),
				new NDimensionalPoint(0, 1, 0), new NDimensionalPoint(1, 1, 1));

		Matrix expected = new Matrix(new double[] { -1 / Math.sqrt(2), 0,
				1 / Math.sqrt(2) }, 3);

		assertTrue(simplex.getNormalVector().minus(expected).norm2() < 1e-10);
	}

	public void testSharesSurface() throws Exception {
		NSimplex simplex1 = new NSimplex(2);

		simplex1.addPoint(new NDimensionalPoint(0, 0));
		simplex1.addPoint(new NDimensionalPoint(1, 0));
		simplex1.addPoint(new NDimensionalPoint(0, 1));

		NSimplex simplex2 = new NSimplex(2);

		simplex2.addPoint(new NDimensionalPoint(1, 0));
		simplex2.addPoint(new NDimensionalPoint(0, 1));
		simplex2.addPoint(new NDimensionalPoint(1, 1));

		NSimplex simplex3 = new NSimplex(2);

		simplex3.addPoint(new NDimensionalPoint(1, 0));
		simplex3.addPoint(new NDimensionalPoint(1, 1));
		simplex3.addPoint(new NDimensionalPoint(2, 0));

		assertFalse(simplex1.hasSharedSurface(simplex1));
		assertFalse(simplex2.hasSharedSurface(simplex2));
		assertFalse(simplex3.hasSharedSurface(simplex3));

		assertTrue(simplex1.hasSharedSurface(simplex2));
		assertTrue(simplex2.hasSharedSurface(simplex1));

		assertTrue(simplex2.hasSharedSurface(simplex3));
		assertTrue(simplex3.hasSharedSurface(simplex2));

		assertFalse(simplex1.hasSharedSurface(simplex3));
		assertFalse(simplex3.hasSharedSurface(simplex1));
	}

	public void testGetBoundaryNumber() throws Exception {
		NSimplex simplex = new NSimplex(2);

		simplex.addPoint(new NDimensionalPoint(0, 0));
		simplex.addPoint(new NDimensionalPoint(1, 0));
		simplex.addPoint(new NDimensionalPoint(0, 1));

		assertEquals(2, simplex.getBoundaryNumber(new NDimensionalPoint(0.2,
				0.2)));
		assertEquals(1, simplex.getBoundaryNumber(new NDimensionalPoint(0.0,
				0.5)));
		assertEquals(0, simplex.getBoundaryNumber(new NDimensionalPoint(0.0,
				0.0)));

		simplex = new NSimplex(3);

		simplex.addPoint(new NDimensionalPoint(0, 0, 0));
		simplex.addPoint(new NDimensionalPoint(0, 0, 1));
		simplex.addPoint(new NDimensionalPoint(1, 0, 0));
		simplex.addPoint(new NDimensionalPoint(0, 1, 0));

		assertEquals(3, simplex.getBoundaryNumber(new NDimensionalPoint(0.2,
				0.2, 0.2)));
		assertEquals(3, simplex.getBoundaryNumber(new NDimensionalPoint(0.2,
				0.2, 0.2)));
		assertEquals(2, simplex.getBoundaryNumber(new NDimensionalPoint(
				1.0 / 3.0, 1.0 / 3.0, 1.0 / 3.0)));
		assertEquals(2, simplex.getBoundaryNumber(new NDimensionalPoint(0, 0.3,
				0.3)));
		assertEquals(1, simplex.getBoundaryNumber(new NDimensionalPoint(0.0,
				0.5, 0.0)));
		assertEquals(1, simplex.getBoundaryNumber(new NDimensionalPoint(0.5,
				0.5, 0.0)));
		assertEquals(0, simplex.getBoundaryNumber(new NDimensionalPoint(0.0,
				0.0, 0.0)));
		assertEquals(0, simplex.getBoundaryNumber(new NDimensionalPoint(0.0,
				0.0, 1.0)));
		assertEquals(0, simplex.getBoundaryNumber(new NDimensionalPoint(0.0,
				1.0, 0.0)));
	}
}
