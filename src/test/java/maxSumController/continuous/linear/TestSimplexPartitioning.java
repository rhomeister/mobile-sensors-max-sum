package maxSumController.continuous.linear;

import junit.framework.TestCase;

public class TestSimplexPartitioning extends TestCase {

	private NCube domain = new NCube(new double[] { 0, 0 },
			new double[] { 1, 1 });

	NDimensionalPoint a = new NDimensionalPoint(0, 0);

	NDimensionalPoint b = new NDimensionalPoint(1, 0);

	NDimensionalPoint c = new NDimensionalPoint(0, 1);

	NDimensionalPoint d = new NDimensionalPoint(1, 1);

	NDimensionalPoint e = new NDimensionalPoint(0.2, 0.8);

	NDimensionalPoint f = new NDimensionalPoint(0.2, 0.5);

	NDimensionalPoint g = new NDimensionalPoint(0.8, 0.5);

	NDimensionalPoint q = new NDimensionalPoint(0.5, 0.5);

	private SimplexPartitioning partitioning1;

	@Override
	protected void setUp() throws Exception {
		partitioning1 = new SimplexPartitioning(domain);

		partitioning1.add(new NSimplex(a, b, d));
		partitioning1.add(new NSimplex(a, c, d));
	}

	public void testUnion1() throws Exception {

		SimplexPartitioning partitioning1 = new SimplexPartitioning(domain);
		SimplexPartitioning partitioning2 = new SimplexPartitioning(domain);

		partitioning1.add(new NSimplex(a, b, d));
		partitioning1.add(new NSimplex(a, c, d));

		partitioning2.add(new NSimplex(a, c, e));
		partitioning2.add(new NSimplex(a, b, e));
		partitioning2.add(new NSimplex(b, d, e));
		partitioning2.add(new NSimplex(c, e, d));

		check1(partitioning1.union(partitioning2));
		check1(partitioning2.union(partitioning1));
	}

	private void check1(SimplexPartitioning union) {
		assertEquals(6, union.getSimplices().size());
		assertEquals(6, union.getDefiningCoordinates().size());
		assertTrue(union.getDefiningCoordinates().contains(q));
		assertTrue(union.getSimplices().contains(new NSimplex(a, b, q)));
		assertTrue(union.getSimplices().contains(new NSimplex(a, e, q)));
		assertTrue(union.getSimplices().contains(new NSimplex(a, c, e)));
		assertTrue(union.getSimplices().contains(new NSimplex(b, d, q)));
		assertTrue(union.getSimplices().contains(new NSimplex(c, d, e)));
		assertTrue(union.getSimplices().contains(new NSimplex(d, e, q)));
	}

	public void testUnion2() throws Exception {

		SimplexPartitioning partitioning2 = new SimplexPartitioning(domain);

		partitioning2.add(new NSimplex(a, c, f));
		partitioning2.add(new NSimplex(a, b, f));
		partitioning2.add(new NSimplex(b, d, f));
		partitioning2.add(new NSimplex(c, f, d));
		partitioning2.split(g);
		partitioning2.split(new NDimensionalLine(a, g));
		partitioning2.split(new NDimensionalLine(c, g));

		SimplexPartitioning union = partitioning2.union(partitioning1);

		assertEquals(16, union.getSimplices().size());
	}

	public void testUnion3() throws Exception {
		SimplexPartitioning partitioning2 = new SimplexPartitioning(domain);

		partitioning2 = new SimplexPartitioning(domain);
		partitioning2.add(new NSimplex(a, b, c));
		partitioning2.add(new NSimplex(b, c, d));

		check2(partitioning2.union(partitioning1));
		check2(partitioning1.union(partitioning2));
	}

	public void testGetAdjacentSimplices() throws Exception {
		assertEquals(2, partitioning1.getAdjacentSimplices(a).size());
		assertEquals(1, partitioning1.getAdjacentSimplices(b).size());
		assertEquals(1, partitioning1.getAdjacentSimplices(c).size());
		assertEquals(2, partitioning1.getAdjacentSimplices(d).size());
	}

	private void check2(SimplexPartitioning union) {
		assertEquals(4, union.getSimplices().size());
		assertEquals(5, union.getDefiningCoordinates().size());
		assertTrue(union.getDefiningCoordinates().contains(q));
		assertTrue(union.getSimplices().contains(new NSimplex(a, b, q)));
		assertTrue(union.getSimplices().contains(new NSimplex(a, c, q)));
		assertTrue(union.getSimplices().contains(new NSimplex(b, d, q)));
		assertTrue(union.getSimplices().contains(new NSimplex(c, d, q)));
	}

	// public void testMerge() throws Exception {
	// SimplexPartitioning partitioning = new SimplexPartitioning(domain);
	//		
	// NSimplex partitionA = new NSimplex(a, b, q);
	// partitioning.add(partitionA);
	// NSimplex partitionB = new NSimplex(b, d, q);
	// partitioning.add(partitionB);
	// NSimplex partitionC = new NSimplex(d, c, q);
	// partitioning.add(partitionC);
	// NSimplex partitionD = new NSimplex(a, c, q);
	// partitioning.add(partitionD);
	//		
	// HashSet<NSimplex> set = new HashSet<NSimplex>();
	// set.add(partitionA);
	// set.add(partitionB);
	//		
	// partitioning.removeAll(set);
	// partitioning.add(NSimplex.merge(set, q));
	//		
	// System.out.println(partitioning.getDefiningCoordinates());
	// set = new HashSet<NSimplex>();
	// set.add(partitionC);
	// set.add(partitionD);
	// partitioning.removeAll(set);
	// partitioning.add(NSimplex.merge(set, q));
	//		
	// System.out.println(partitioning.getDefiningCoordinates());
	//		
	// System.out.println(partitioning);
	//		
	// new TwoDimensionalTriangulationGUI().draw(partitioning);
	//		
	// Thread.sleep(100000);
	// }
}
