package maxSumController.multiball.nonlinearoptimiser;

public interface StepSizeGenerator {
	public double getStepSize();
	public StepSizeGenerator copy();
}
