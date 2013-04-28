package maxSumController.continuous;

public class ContinuousVariableStateImpl implements ContinuousVariableState {

	private double value;

	public ContinuousVariableStateImpl(double d) {
		this.value = d;
	}

	public double getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "" + value;
	}

}
