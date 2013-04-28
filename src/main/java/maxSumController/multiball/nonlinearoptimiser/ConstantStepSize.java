package maxSumController.multiball.nonlinearoptimiser;

public class ConstantStepSize implements StepSizeGenerator {

	private double stepSize;

	public ConstantStepSize(double stepSize) {
		this.stepSize = stepSize;
	}
	
	@Override
	public double getStepSize() {
		return stepSize;
	}

	@Override
	public StepSizeGenerator copy() {
		return new ConstantStepSize(stepSize);
	}

}
