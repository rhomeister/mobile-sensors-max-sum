package maxSumController.multiball.math;

public interface InterpolationFunction {

	public void setDataPoints(double[] stateValues,	
			double[] functionValues);

	public double evaluate(double state);
	
	public double evaluateDerivative(double state);
	
	public double evaluateSecondDerivative(double state);
	
	public InterpolationFunction copy();

}
