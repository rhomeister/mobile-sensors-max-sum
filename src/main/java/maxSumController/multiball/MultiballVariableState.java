package maxSumController.multiball;

import maxSumController.DiscreteVariableState;

public class MultiballVariableState implements DiscreteVariableState {

	private double value;

	public MultiballVariableState(double value) {
		this.value = value;
	}

	public void update(Double valueChange) {
		// System.out.println("updated value of " + this + " from  " + value
		// + " to " + (value + valueChange));
		// System.out.println(valueChange);

		value += valueChange;
	}

	public double getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "MultiballState " + value;
	}

	public void setValue(double d) {
		value = d;
	}

}
