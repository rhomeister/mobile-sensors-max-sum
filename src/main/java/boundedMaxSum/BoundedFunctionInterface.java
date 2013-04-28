package boundedMaxSum;

public interface BoundedFunctionInterface {

	public abstract double getMinimumBound();

	public abstract double getMaximumBound();
	
	public abstract BoundedInternalFunction clone();

	
}
