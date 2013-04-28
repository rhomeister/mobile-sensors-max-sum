package maxSumController.multiball.GP;

public class PreConvergenceNoise implements NoiseVariance {

	@Override
	public double calculateVariance(double[] logHyper, double T) {
		if(T < Math.exp(logHyper[0])) return Math.exp(logHyper[1]);
		return Math.exp(logHyper[2]);
	}

	@Override
	public int hyperParameterCount() {
		return 3;
	}

}
