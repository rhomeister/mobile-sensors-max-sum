package maxSumController.multiball.math;

import java.util.Arrays;

public class SplineInterpolation implements InterpolationFunction {

	private double[] x;
	private double[] y;
	private double[] z;
	private double[] h;
	private double[] c;
	private double[] d;
	private int n;
	
	
	public SplineInterpolation() { }

	public double evaluate(double state) {
		int i = whichSpline(state);
		if(state == x[i]) return y[i];
		double lx = (state - x[i]);
		double rx = (x[i+1] - state);
		return (rx*(((z[i]/(h[i]*6.0)) * rx*rx)+d[i])) +
				(lx*(((z[i+1]/(h[i]*6.0)) * lx*lx)+c[i]));
	}
	

	@Override
	public double evaluateDerivative(double state) {
		int i = whichSpline(state);
		double lx = (state - x[i]);
		double rx = (x[i+1] - state);
		return (c[i] - d[i]) + ((z[i+1]/(h[i]*2.0)) * lx*lx)
				- ((z[i]/(h[i]*2.0)) * rx*rx);
	}

	@Override
	public double evaluateSecondDerivative(double state) {
		int i = whichSpline(state);
		double lx = (state - x[i]);
		double rx = (x[i+1] - state);
		return ((z[i+1]/h[i]) * lx)
				+ ((z[i]/h[i]) * rx);
	}

	public int whichSpline(double state) {
		if(state < x[0]) return 0;
		int j;
		for(j=0; (j < n) ; j++) {
			if(state < x[j]) return j-1;
		}
		return (n-1);
	}
	
	
	@Override
	public void setDataPoints(double[] stateValues, double[] functionValues) {
		n = stateValues.length-1;
		ComparableArrayIndex[] stateIndex = 
			new ComparableArrayIndex[n+1];  
		int i;
		for(i=0; i<=n; i++) {
			stateIndex[i] = new ComparableArrayIndex(stateValues, i);
		}
		Arrays.sort(stateIndex);

		this.x = new double[n+1];
		this.y = new double[n+1];
		for(i=0; i<=n; i++) {
			this.x[i] = stateValues[stateIndex[i].index];
			this.y[i] = functionValues[stateIndex[i].index];
		}
		double[] b = new double[n];
		h = new double[n];
		for(i=0; i<n; i++) {
			h[i] = x[i+1] - x[i];
			b[i] = (y[i+1]-y[i])/h[i];
		}
		double[] u = new double[n];
		double[] v = new double[n];
		u[1] = 2*(h[0] + h[1]);
		v[1] = 6*(b[1] - b[0]);
		for(i=2; i<n; i++) {
			u[i] = (2*(h[i-1] + h[i])) - (h[i-1]*h[i-1]/u[i-1]);
			v[i] = (6*(b[i] - b[i-1])) - (h[i-1]*v[i-1]/u[i-1]);
		}
		z = new double[n+1];
		z[n] = 0;
		for(i=(n-1); i>0; i--){
			z[i] = (v[i] - (h[i]*z[i+1]))/u[i];
		}
		z[0] = 0;
		
		c = new double[n];
		d = new double[n];		
		for(i=0; i<n; i++) {
			c[i] = (y[i+1]/h[i]) - (h[i] * z[i+1]/6.0);
			d[i] = (y[i]/h[i]) - (h[i] * z[i]/6.0);
		}
	}

	@Override
	public InterpolationFunction copy() {
		return new SplineInterpolation();
	}

}
