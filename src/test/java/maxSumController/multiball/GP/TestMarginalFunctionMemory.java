package maxSumController.multiball.GP;

import java.util.Arrays;

import Jama.Matrix;

import junit.framework.TestCase;


public class TestMarginalFunctionMemory extends TestCase {

	public void testMemory(){
		MarginalFunctionMemory memory = new MarginalFunctionMemory(10);
		double[] xVals = new double[2];
		double[] yVals = new double[2];
		double[] tVals = new double[2];
		int j=0;
		for(int i=0; i<10; i++){
			xVals[0] = j;
			xVals[1] = j+1;
			yVals[0] = (j*j);
			yVals[1] = ((j+1)*(j+1));
			tVals[0] = (i%2);
			tVals[1] = (i%2);			
			memory.add(copy(xVals), copy(yVals), copy(tVals));
			if( (i%2) == 1){
				j++;
				memory.setLastValuable();
			}
		}
		
		double[][] expectedX = { {0, 1, 0, 1, 1, 2, 1, 2, 2, 3, 2, 3, 3, 4, 3, 4, 4, 5, 4, 5}, 
			{0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1}};
		double[][] expectedY = {{0, 1, 0, 1, 1, 4, 1, 4, 4, 9, 4, 9, 9, 16, 9, 16, 16, 25, 16, 25}};
		
		assertTrue(memory.X.transpose().minus(new Matrix(expectedX)).normF() < 0.00001);
		assertTrue(memory.Y.transpose().minus(new Matrix(expectedY)).normF() < 0.00001);
		
		int[] expectedItNum = {0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4};
		for(int i=0; i<memory.iterationNum.length; i++) {
			assertEquals(memory.iterationNum[i], expectedItNum[i]);
		}
	
	}
	
	private double[] copy(double[] vals){
		return Arrays.copyOf(vals, vals.length);
	}
	
}
