package maxSumController.multiball.GP;

import java.util.Random;

import Jama.Matrix;
import uk.ac.soton.ecs.gp4j.gp.covariancefunctions.SquaredExponentialCovarianceFunction;
import junit.framework.TestCase;


public class TestGPMSCovarianceFunction extends TestCase {
	public void testNoise(){
		int n = 10;
		int[] itNums = new int[2*n];
		for(int i=0; i<n; i++) {
			itNums[2*i] = i;
			itNums[(2*i)+1] = i;
		}

		HybridGPMSCovarianceFunction cov = 
				new HybridGPMSCovarianceFunction(
				SquaredExponentialCovarianceFunction.getInstance(),
				HybridGPMSTransform.generate(itNums),
				new ExponentialNoise());

		double loghyper[] = {0, 0, 0, -10};
		
		Matrix trainX = new Matrix(2*n, 2);
		
		for(int i=0; i<(2*n); i++) {
			trainX.set(i, 0, 0);
			trainX.set(i, 1, itNums[i]);
		}
		
		Matrix trainCov = cov.calculateCovarianceMatrix(loghyper, trainX);
		
		Matrix expectedTrainCov = new Matrix(n,n);
		Matrix expectedTestCov = new Matrix(2*n,1);

		for(int i=0; i<n; i++) {
			expectedTrainCov.set(i, i, 2*Math.exp(-i));
			expectedTestCov.set(2*i, 0, Math.exp(-i));
			expectedTestCov.set((2*i)+1, 0, Math.exp(-i));
		}
		assertMatricesAlmostEqual(trainCov, expectedTrainCov);
		
		Matrix testX = new Matrix(3*n, 2);
		Random r = new Random();
		
		for(int i=0; i<(3*n); i++) {
			testX.set(i, 0, 0);
			testX.set(i, 1, r.nextInt());
		}
		
		Matrix ttCov = cov.calculateTrainTestCovarianceMatrix(loghyper, trainX, testX);
		
		Matrix expectedTTCov = new Matrix(n, (3*n));
		assertMatricesAlmostEqual(ttCov, expectedTTCov);
		
		Matrix testCov = cov.calculateTestCovarianceMatrix(loghyper, trainX);
		
		assertMatricesAlmostEqual(testCov, expectedTestCov);
	}

	
	private void assertMatricesAlmostEqual(Matrix m1, Matrix m2){
		assertTrue(m1.minus(m2).normF() < 0.000001);		
	}	
	

	
}
