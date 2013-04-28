package maxSumController.continuous.linear;

import junit.framework.TestCase;

public class TestNDimensionalPoint extends TestCase {

	private NDimensionalPoint a = new NDimensionalPoint(2.563950248511419E-16,
			0.9999999999999998);

	private NDimensionalPoint b = new NDimensionalPoint(1.0, 1.0);

	private NDimensionalPoint c = new NDimensionalPoint(0.0, 1.0);

	public void testEquals() throws Exception {
		assertFalse(a.equals(b));
		assertFalse(b.equals(a));
		assertTrue(a.equals(a));
		assertTrue(b.equals(b));
		assertTrue(c.equals(c));
		assertTrue(a.equals(c));
		assertTrue(c.equals(a));

	}
}
