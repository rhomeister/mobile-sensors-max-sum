package maxSumController.continuous.linear;

import java.util.Set;

import junit.framework.TestCase;

public class TestNCube extends TestCase {

	private NCube cube1;

	private NCube cube2;

	private NCube cube3;

	private NCube cube4;

	private NCube cube5;

	@Override
	protected void setUp() throws Exception {
		cube1 = new NCube(new double[] { 2 }, new double[] { 3 });
		cube2 = new NCube(new double[] { 0, 2 }, new double[] { 3, 5 });
		cube3 = new NCube(new double[] { 0, 6 }, new double[] { 3, 9 });
		cube4 = new NCube(new double[] { 2, 7 }, new double[] { 4, 10 });
		cube5 = new NCube(new double[] { 1, 3 }, new double[] { 2, 4 });
	}

	public void testGetVertices() throws Exception {
		Set<NDimensionalPoint> vertices = cube1.getVertices();
		assertEquals(2, vertices.size());

		assertTrue(vertices.contains(new NDimensionalPoint(2.0)));
		assertTrue(vertices.contains(new NDimensionalPoint(3.0)));
	}

	public void testGetVertices2() throws Exception {
		Set<NDimensionalPoint> vertices = cube2.getVertices();
		assertEquals(4, vertices.size());
		assertTrue(vertices.contains(new NDimensionalPoint(0.0, 2.0)));
		assertTrue(vertices.contains(new NDimensionalPoint(0.0, 5.0)));
		assertTrue(vertices.contains(new NDimensionalPoint(3.0, 2.0)));
		assertTrue(vertices.contains(new NDimensionalPoint(3.0, 5.0)));

		cube2.contains(new NDimensionalPoint(1.0, 7.0));
	}

	public void testOverlap() throws Exception {
		assertFalse(cube2.overlaps(cube3));
		assertFalse(cube3.overlaps(cube2));

		assertTrue(cube3.overlaps(cube4));
		assertTrue(cube4.overlaps(cube3));

		assertTrue(cube2.overlaps(cube5));
		assertTrue(cube5.overlaps(cube2));
	}
}
