package maxSumController.multiball.nonlinearoptimiser;

public class GeometricStepSize implements StepSizeGenerator {

	private double stepSize;
	private double factor;

	public GeometricStepSize(double startingVal, double factor) {
		this.stepSize = (startingVal/factor);
		this.factor = factor;
	}
	
	@Override
	public double getStepSize() {
		stepSize *= factor;
		return stepSize;
	}

	@Override
	public StepSizeGenerator copy() {
		return new GeometricStepSize(stepSize, factor);
	}

}
