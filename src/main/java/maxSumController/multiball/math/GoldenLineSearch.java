package maxSumController.multiball.math;

import java.util.Arrays;

public class GoldenLineSearch {
	
	private static final double goldenRatioInverse = (1 / 1.6180339887498948482);

	public static double search(RealFunction f,  double[] searchIntervalPoints, double worthinessThreshold,
			int iterations){
		
		if(searchIntervalPoints == null) return Double.NaN;

		int n = searchIntervalPoints.length + 1;
		double[] xDivisions = new double[n+1];

		xDivisions[0] = searchIntervalPoints[0];
		xDivisions[n] = searchIntervalPoints[n-2];
		
		for(int i=0; i < n-1; i++){
			xDivisions[i+1] = searchIntervalPoints[i];
			if(xDivisions[i+1] < xDivisions[0]) xDivisions[0] = xDivisions[i+1];
			if(xDivisions[i+1] > xDivisions[n]) xDivisions[n] = xDivisions[i+1];
		}
		Arrays.sort(xDivisions);
		
		double avGap = (xDivisions[n] - xDivisions[0]) / n; 
		if (avGap == 0) avGap = 1.0;
		
		xDivisions[0] -= (goldenRatioInverse * avGap);
		xDivisions[n] += (goldenRatioInverse * avGap);

		double[] xDivWorthiness = new double[n+1];
		
		for(int i=0; i<=n; i++){
			xDivWorthiness[i] = f.evaluate(xDivisions[i]);
		}
		double bestWorth = xDivWorthiness[0];
		double worstWorth = xDivWorthiness[0];
		for(int i=1; i<=n; i++){
			if(bestWorth < xDivWorthiness[i]) bestWorth = xDivWorthiness[i];
			if(worstWorth > xDivWorthiness[i]) worstWorth = xDivWorthiness[i];
		}

		double worthThreshold = worstWorth + (worthinessThreshold * (
				bestWorth - worstWorth));
		
		double[] xVals = new double[4];
		double[] worthiness = new double[4];

		double result = 0;
		Double bestValue = null;
		
		for(int i=0; i < n; i++){
			if(Math.max(xDivWorthiness[i], xDivWorthiness[i+1]) < worthThreshold)
				continue;
			for(int j=0; j<=1; j++) {
				xVals[j] = xDivisions[i+j];
				worthiness[j] = xDivWorthiness[i+j];
			}
			double d = goldenRatioInverse*(xVals[1] - xVals[0]);
			xVals[2] = xVals[1] - d;
			xVals[3] = xVals[0] + d;
			worthiness[2] = f.evaluate(xVals[2]);
			worthiness[3] = f.evaluate(xVals[3]);

			for(int j=0; j<iterations; j++){
				int a, b;
				if(worthiness[0] > worthiness[1]) {
					a = 2;
					b = 3;
				} else {
					a = 3;
					b = 2;
				}
				arraysMove(b, b-2, xVals, worthiness);
				arraysMove(a, b, xVals, worthiness);
				
				xVals[a] = xVals[a-2] + (xVals[b-2] - xVals[b]);
				if(xVals[2] > xVals[3]){
					xVals[2] = xVals[0];
					xVals[3] = xVals[1];
					break;
				}
				worthiness[a] = f.evaluate(xVals[a]);
			}
			
			int index = (worthiness[1]>worthiness[2])?1:2;
			if((bestValue==null) || (bestValue.doubleValue() < worthiness[index])) {
				result = xVals[index];
				bestValue = new Double(worthiness[index]);
			}
		}
		return result;
	}
	
	private static void arraysMove(int origin, int destination, double[] array1, double[] array2){
		array1[destination] = array1[origin];
		array2[destination] = array2[origin];
	}
}
