package maxSumController.multiball.nonlinearoptimiser;

public class NonLinearOptimiserCloneFactory implements
		NonLinearOptimiserFactory {

	private NonLinearOptimiser original;
	
	public NonLinearOptimiserCloneFactory(NonLinearOptimiser original){
		this.original = original;
	}
	@Override
	public NonLinearOptimiser create() {
		return original.copy();
	}

}
