package senseSleep;

import java.util.List;
import java.util.Vector;

import maxSumController.continuous.LineSegment;
import maxSumController.continuous.PieceWiseLinearFunction;
import maxSumController.continuous.PieceWiseLinearFunctionImpl;
import maxSumController.continuous.linear.MultiVariatePieceWiseLinearFunction;
import maxSumController.continuous.linear.MultiVariatePieceWiseLinearFunctionUtilities;
import maxSumController.continuous.linear.TwoDimensionalTriangulationGUI;

public class SimpleScenario {

	public static void main(String[] args) {
		double l = 10;

		MultiVariatePieceWiseLinearFunction function = SenseSleepUtilityFunctionWrapAroundFactory
				.createFunction(new double[] { 4.321321, 2.3235 }, l, 1.0);

		new TwoDimensionalTriangulationGUI().draw(function);

		List<PieceWiseLinearFunction> marginalFunctions = new Vector<PieceWiseLinearFunction>();

		marginalFunctions.add(new PieceWiseLinearFunctionImpl(new LineSegment(
				0.0, 0.0, l, 0.0)));
		PieceWiseLinearFunctionImpl linear = new PieceWiseLinearFunctionImpl();

		double[] x = { 0.0, 1.0, 1.1, 2.0, 3.0, 4.0, 5.0, 6.0, 8.0, 9.1, 9.2, 9.4, 9.5, 9.8, 10.0 };
		double[] y = { 1.0, 0.4, 5.0, 2.0, 1.0, 3.0, 1.0, 3.0, 4.0, 1.0, 2.0, 4.0, 2.0, 1.0, 4.0 };

		linear.addSegments(x, y);

		// marginalFunctions.add(linear);
		//		
		// MultiVariatePieceWiseLinearFunction function2 =
		// MultiVariatePieceWiseLinearFunctionUtilities
		// .createMultiDimensionalGridFunction(marginalFunctions);
		//				
		// MultiVariatePieceWiseLinearFunctionUtilities
		// .printFunctionEvaluation(function2);
		// //
		// new TwoDimensionalTriangulationGUI().draw(function2);
		// new TwoDimensionalTriangulationGUI().draw(function);

		// System.out.println(function2.evaluate(10.0, 10.0));

		// System.out.println(function2.getPartitioning().getDefiningCoordinates());

		MultiVariatePieceWiseLinearFunction sum = function
				.addUnivariateFunction(linear, 1);
		//
		new TwoDimensionalTriangulationGUI().draw(sum);
		//
		 System.out.println("test");
		MultiVariatePieceWiseLinearFunctionUtilities
				.printFunctionEvaluation(sum, 0.25);
		
//	 PieceWiseLinearFunction function2 = UpperEnvelopeAlgorithm.calculateUpperEnvelope(sum.project(0));
//
//	 System.out.println(function2);
	 
	}
}
