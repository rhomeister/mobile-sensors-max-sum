package maxSumController;

public interface VariableDomain<S extends VariableState> {

	public MarginalValues<S> createZeroMarginalFunction();
}
