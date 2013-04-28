package maxSumController.multiball.math;

public class PolynomialInterpolation implements InterpolationFunction {

	private double[] stateValues;
	private double[] functionValues;
	private double[] coefficients;
	
	public double evaluate(double state) {
		int i;
		for(i=0; i < stateValues.length; i++)
			if (stateValues[i] == state) return functionValues[i];
		double val = coefficients[0];
		double statepower = 1;
	   
		for (i = 1; i < stateValues.length; i++) {
			statepower *= state;
			val += statepower * coefficients[i];
		}
		return val;
	}
	
	public PolynomialInterpolation() {
	}
	
	@Override
	public InterpolationFunction copy() {
		return new PolynomialInterpolation();
	}
	
	@Override
	public double evaluateDerivative(double state) {
		int i;
		if(stateValues.length < 2) return 0;
		double val = coefficients[1];
		double statepower = 1;
	   
		for (i = 2; i < stateValues.length; i++) {
			statepower *= state;
			val += statepower * i * coefficients[i];
		}
		return val;
	}

	@Override
	public double evaluateSecondDerivative(double state) {
		int i;
		if(stateValues.length < 3) return 0;
		double val = 2*coefficients[2];
		double statepower = 1;
	   
		for (i = 3; i < stateValues.length; i++) {
			statepower *= state;
			val += i * (i-1) * statepower * coefficients[i];
		}
		return val;
	}

	@Override
	public void setDataPoints(double[] stateValues, double[] functionValues) {
		this.stateValues = stateValues;
		this.functionValues = functionValues;
		int n = stateValues.length;
		double[][] polys = new double[n][n];

		int i, j, k;
		
		for(i=0; i<n; i++) {
			polys[i][0] = functionValues[i];
			for(j=1; j<n; j++){
				polys[i][j] = 0;
			}
		}
		
		double[] nevilleUpdate = new double[n]; 
		
		//Use Neville's algorithm to calculate
		//coefficients.
		for(i=0; i<(n-1); i++) {
			//At this point, for a = 0...(n-1-i)
			//polys[a][] contains coefficients 
			//for P_{a,a+i}() of Neville's algorithm

			for(j=0; j<(n-(i+1)); j++){
				//For j = 0...(n-2-i) we calculate
				//P_{j,j+i+1}(x) =
				//((x - x[j+i+1])*P_{j,j+i}(x) +
				//(x[j] - x)*P_{j+1,j+i+1}(x))/
				//(x[j] - x[j+i+1])
				
				nevilleUpdate[i+1] = 0;
				for(k=0; k<=i; k++)
					nevilleUpdate[k] = (stateValues[j]*polys[j+1][k])
					- (stateValues[j+i+1]*polys[j][k]);
				for(k=0; k<=i; k++)
					nevilleUpdate[k+1] += polys[j][k] - polys[j+1][k];
				double h = 1.0/(stateValues[j] - stateValues[j+i+1]);
				for(k=0; k<=(i+1); k++) {
					polys[j][k] = nevilleUpdate[k] * h;
				}
			}
		}
		//Now polys[0][] contains coefficients 
		//for P_{0,n-1}() of Neville's algorithm
		//which is our desired function.
		
		coefficients = new double[n];
		
		for(i=0; i<n; i++) coefficients[i] = polys[0][i];
		
	}

}
