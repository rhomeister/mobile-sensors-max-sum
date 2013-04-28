package maxSumController.continuous.linear;

import junit.framework.TestCase;

public class TestNDimensionalPointSet extends TestCase {

	NDimensionalPoint point1 = new NDimensionalPoint(0, 0);

	NDimensionalPoint point2 = new NDimensionalPoint(1e-10, 1e-10);

	NDimensionalPoint point3 = new NDimensionalPoint(1, 1);

	NDimensionalPoint point4 = new NDimensionalPoint(1, 1.000000000000001);

	private NDimensionalPointSet set;

	@Override
	protected void setUp() throws Exception {
		set = new NDimensionalPointSet();

		set.add(point1);
		set.add(point2);
		set.add(point3);
		set.add(point4);
	}

	public void testAdd() throws Exception {
		assertEquals(2, set.size());

		assertTrue(set.contains(point1));
		assertTrue(set.contains(point2));
		assertTrue(set.contains(point3));
		assertTrue(set.contains(point4));
	}

	public void testGetClosest() throws Exception {
		NDimensionalPoint closest = set.getClosest(point2);
		assertTrue(closest.getCoordinates()[0] == point1.getCoordinates()[0]);
		assertTrue(closest.getCoordinates()[1] == point1.getCoordinates()[1]);

		closest = set.getClosest(point4);
		assertTrue(closest.getCoordinates()[0] == point3.getCoordinates()[0]);
		assertTrue(closest.getCoordinates()[1] == point3.getCoordinates()[1]);
	}
}
