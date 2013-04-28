package maxSumController.multiball.nonlinearoptimiser;

public class HarmonicStepSize implements StepSizeGenerator {

	private double stepSize;
	private double updateVal;

	public HarmonicStepSize(double startingVal, double updateVal) {
		this.stepSize = startingVal;
		this.updateVal = updateVal;
	}
	
	@Override
	public double getStepSize() {
		double s = stepSize;
		stepSize /= ((stepSize * updateVal) + 1);
		return s;
	}

	@Override
	public StepSizeGenerator copy() {
		return new HarmonicStepSize(stepSize, updateVal);
	}

}
