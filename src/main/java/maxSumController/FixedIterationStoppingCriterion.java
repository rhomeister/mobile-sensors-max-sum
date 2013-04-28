package maxSumController;

public class FixedIterationStoppingCriterion implements StoppingCriterion {

	private int iterations;
	
	public FixedIterationStoppingCriterion(int iterations) {
		this.iterations = iterations;
	}

	@Override
	public boolean isDone(int simStep) {
		return simStep > iterations;
	}
}
