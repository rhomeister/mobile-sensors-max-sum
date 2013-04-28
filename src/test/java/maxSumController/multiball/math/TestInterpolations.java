package maxSumController.multiball.math;

import java.util.Random;
import junit.framework.TestCase;


public class TestInterpolations extends TestCase {

	private void swap(double[] array, int i, int j) {
		double val = array[i];
		array[i] = array[j];
		array[j] = val;
	}
	
	private void continuity(InterpolationFunction function, double x) {
		double eps1 = 0.00000000001;
		double eps2 = 0.000001;
		assertTrue(Math.abs(function.evaluate(x + eps1) - function.evaluate(x)) < eps2);
		assertTrue(Math.abs(function.evaluate(x - eps1) - function.evaluate(x)) < eps2);
		assertTrue(Math.abs(function.evaluateDerivative(x - eps1) 
				- function.evaluateDerivative(x)) < eps2);
		assertTrue(Math.abs(function.evaluateDerivative(x + eps1) 
				- function.evaluateDerivative(x)) < eps2);
		assertTrue(Math.abs(function.evaluateSecondDerivative(x - eps1) 
				- function.evaluateSecondDerivative(x)) < eps2);
		assertTrue(Math.abs(function.evaluateSecondDerivative(x + eps1) 
				- function.evaluateSecondDerivative(x)) < eps2);
	}
	
	public void testInterpolations() {
		Random rand = new Random(1234);
		int i;
		for(i=0; i<100; i++) {
			runTest(true, rand);
			runTest(false, rand);
		}
	}
	
	private void runTest(boolean polynomial, Random rand){
		int i;
		int numPoints = (rand.nextInt(10) + 3);
		
		double[] x = new double[numPoints];
		double[] y = new double[numPoints];
		
		double a1 = rand.nextDouble();
		double a2 = rand.nextDouble();
		double a3 = rand.nextDouble();
		double a4 = rand.nextDouble();
		double a5 = rand.nextDouble();
		
		x[0] = rand.nextDouble();
		for(i=1; i<numPoints; i++) {
			x[i] =x[i-1] + rand.nextDouble();
		}
		for(i=0; i<numPoints; i++) {
			y[i] = Math.sin(a1 * x[i]) * a4;
			y[i] += Math.cos(a2 * x[i]);
			y[i] += Math.log(1 + (a3 * x[i])) * a5;
		}
		
		double[] jumbledx = new double[numPoints];
		double[] jumbledy = new double[numPoints];
		for(i=0; i<numPoints; i++) {
			jumbledy[i] = y[i];
			jumbledx[i] = x[i]; 
		}

		
		int j, k;
		for(i=0; i<100; i++) {
			j = rand.nextInt(numPoints);
			k = rand.nextInt(numPoints);
			swap(jumbledx, j, k);
			swap(jumbledy, j, k);
		}

		InterpolationFunction IF = null;
		
		if(polynomial){
			IF = new PolynomialInterpolation();			
		} else {
			IF = new SplineInterpolation();
		}

		IF.setDataPoints(jumbledx, jumbledy);

		double xpoint;
/*		
		for(i=0; i<numPoints; i++) System.out.println(x[i] + "	" + y[i]);
		System.out.println();
		
		for(xpoint = -1; xpoint < 10; xpoint += 0.1) {
			System.out.println(xpoint + "	" + 
					IF.evaluate(xpoint));
		}
		System.out.println();
	*/	
		 
		for(i=0; i<numPoints; i++) {
			continuity(IF, x[i]);
			assertTrue(Math.abs(IF.evaluate(x[i]) - y[i]) < 0.0000001);
		}

		for(xpoint = -1; xpoint < 10; xpoint += 0.01) {
			continuity(IF, xpoint);
		}
		
	}
}
