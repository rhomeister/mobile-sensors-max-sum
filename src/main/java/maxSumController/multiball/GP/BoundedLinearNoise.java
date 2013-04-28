package maxSumController.multiball.GP;

public class BoundedLinearNoise implements NoiseVariance {

	@Override
	public double calculateVariance(double[] logHyper, double T) {
		return Math.exp(logHyper[2]) + (Math.exp(logHyper[1]) * 
				Math.max(0,Math.exp(logHyper[0]) - T));
	}

	@Override
	public int hyperParameterCount() {
		return 3;
	}

}
