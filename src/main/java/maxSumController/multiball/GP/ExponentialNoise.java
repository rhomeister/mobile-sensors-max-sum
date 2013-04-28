package maxSumController.multiball.GP;

public class ExponentialNoise implements NoiseVariance {

	@Override
	public double calculateVariance(double[] logHyper, double T) {
		return Math.exp((2*logHyper[0]) - Math.exp(logHyper[1])*T);
	}

	@Override
	public int hyperParameterCount() {
		return 2;
	}

}
