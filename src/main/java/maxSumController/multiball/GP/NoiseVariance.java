package maxSumController.multiball.GP;

public interface NoiseVariance {
	public double calculateVariance(double[] logHyper, double T);
	public int hyperParameterCount();
}
