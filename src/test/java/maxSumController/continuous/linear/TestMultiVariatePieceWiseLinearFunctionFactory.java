package maxSumController.continuous.linear;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import maxSumController.continuous.LineSegment;
import maxSumController.continuous.PieceWiseLinearFunction;
import maxSumController.continuous.PieceWiseLinearFunctionImpl;

public class TestMultiVariatePieceWiseLinearFunctionFactory extends TestCase {

	public void testCreateMultiDimensionalGridFunction() throws Exception {
		List<PieceWiseLinearFunction> functions = new ArrayList<PieceWiseLinearFunction>();

		PieceWiseLinearFunctionImpl function1 = new PieceWiseLinearFunctionImpl();
		function1.addSegment(new LineSegment(0, 0, 1, 1));
		function1.addSegment(new LineSegment(1, 1, 2, 4));

		PieceWiseLinearFunctionImpl function2 = new PieceWiseLinearFunctionImpl();
		function2.addSegment(new LineSegment(0, 0, 3, 0));

		functions.add(function1);
		functions.add(function2);

		MultiVariatePieceWiseLinearFunction function = MultiVariatePieceWiseLinearFunctionUtilities
				.createMultiDimensionalGridFunction(functions);

		assertEquals(function.getDomain().getDomainStart(0), 0.0);
		assertEquals(function.getDomain().getDomainEnd(0), 2.0);
		assertEquals(function.getDomain().getDomainStart(1), 0.0);
		assertEquals(function.getDomain().getDomainEnd(1), 3.0);

		assertEquals(4, function.getPartitioning().getSimplices().size());

		for (double x2 = 0; x2 <= 3; x2 += 0.1) {
			assertEquals(0.0, function.evaluate(new NDimensionalPoint(0, x2)));
			assertEquals(0.2, function.evaluate(new NDimensionalPoint(0.2, x2)));
			assertEquals(0.5, function.evaluate(new NDimensionalPoint(0.5, x2)));
			assertEquals(1.0, function.evaluate(new NDimensionalPoint(1, x2)));
			assertEquals(2.5,
					function.evaluate(new NDimensionalPoint(1.5, x2)), 1e-10);
			assertEquals(4.0, function.evaluate(new NDimensionalPoint(2, x2)));
		}
	}

	public void test1DCreateZeroFunction() throws Exception {
		System.out.println("test");

		NCube domain = new NCube(new double[] { 0 }, new double[] { 1 });
		MultiVariatePieceWiseLinearFunction function = MultiVariatePieceWiseLinearFunctionUtilities
				.createZeroFunction(domain);

		System.out.println(function.getSimplexCount());
		System.out.println(function.getPartitioning().getSimplices());

		System.out.println("end test");

	}

	public void testCreateMultiDimensionalGridFunction2() throws Exception {
		NCube domain = new NCube(new double[] { 0, 0 }, new double[] { 1, 1 });
		MultiVariatePieceWiseLinearFunction function = MultiVariatePieceWiseLinearFunctionUtilities
				.createMultiDimensionalGridFunction(domain);
		assertEquals(2, function.getPartitioning().getSimplices().size());

		domain = new NCube(new double[] { 0, 0, 0 }, new double[] { 1, 1, 1 });
		function = MultiVariatePieceWiseLinearFunctionUtilities
				.createMultiDimensionalGridFunction(domain);
		assertEquals(6, function.getPartitioning().getSimplices().size());
	}

	public void testCreateMultiDimensionalGridFunction3() throws Exception {
		List<PieceWiseLinearFunction> functions = new ArrayList<PieceWiseLinearFunction>();

		PieceWiseLinearFunctionImpl function1 = new PieceWiseLinearFunctionImpl();
		function1.addSegment(new LineSegment(1, 0, 4, 5));
		function1.addSegment(new LineSegment(5, 2, 6, 1));

		PieceWiseLinearFunctionImpl function2 = new PieceWiseLinearFunctionImpl();
		function2.addSegment(new LineSegment(7, 0, 8, 0));

		functions.add(function1);
		functions.add(function2);

		MultiVariatePieceWiseLinearFunction function = MultiVariatePieceWiseLinearFunctionUtilities
				.createMultiDimensionalGridFunction(functions);

		assertEquals(1.0, function.getDomain().getDomainStart(0));
		assertEquals(6.0, function.getDomain().getDomainEnd(0));
		assertEquals(7.0, function.getDomain().getDomainStart(1));
		assertEquals(8.0, function.getDomain().getDomainEnd(1));
	}
}
