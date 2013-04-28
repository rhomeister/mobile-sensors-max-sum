package maxSumController;

public interface MarginalValues<S extends VariableState> {

	public MarginalValues<S> add(MarginalValues<S> otherFunction);

	public S argMax();

	public void normalise();

	/**
	 * Returns the size of this message in the number of doubles it can be
	 * represented with
	 * 
	 * @return
	 */
	public int getSize();
	
	public double min();
	
	public double max();
}
