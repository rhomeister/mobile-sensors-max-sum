package maxSumController.multiball.math;

import junit.framework.TestCase;


public class TestGoldenLineSearch extends TestCase {
	static class TestQuadratic implements RealFunction{

		@Override
		public double evaluate(double x) {
			return (100 - (x-4.5)*(x-4.5));
		}
		
	}
	static class TestFunc implements RealFunction{

		@Override
		public double evaluate(double x) {
			return (100*Math.cos(x - 4.5)) + (100 - (x-4.5)*(x-4.5));
		}
		
	}

	public void testLineSearch(){
		double[] searchIntervalPoints = {-10, -5, 7, 12};
		double opt1 = GoldenLineSearch.search(new TestQuadratic(), searchIntervalPoints, 0, 100);
		assertTrue(Math.abs(opt1 - 4.5) < 0.00001);
		double[] searchIntervalPoints2 = new double[30];
		for(int i=0; i<30; i++) searchIntervalPoints2[i] = (i*0.4) - 3.2;
		double opt2 = GoldenLineSearch.search(new TestFunc(), searchIntervalPoints2, 0, 100);
		assertTrue(Math.abs(opt2 - 4.5) < 0.00001);
	}
}
