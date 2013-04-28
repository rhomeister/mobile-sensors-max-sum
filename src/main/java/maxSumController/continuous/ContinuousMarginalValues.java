package maxSumController.continuous;

import org.apache.commons.lang.NotImplementedException;

import maxSumController.MarginalValues;

public class ContinuousMarginalValues implements
		MarginalValues<ContinuousVariableState> {

	PieceWiseLinearFunction function;

	public ContinuousMarginalValues(PieceWiseLinearFunction function) {
		this.function = function;
	}

	public PieceWiseLinearFunction getFunction() {
		return function;
	}

	public ContinuousMarginalValues add(
			MarginalValues<ContinuousVariableState> values) {
		ContinuousMarginalValues otherFunction = (ContinuousMarginalValues) values;

		return new ContinuousMarginalValues(otherFunction.function
				.add(function));
	}

	public ContinuousVariableState argMax() {
		return new ContinuousVariableStateImpl(function.argMax());
	}

	public void normalise() {
		function.normalise();
	}

	@Override
	public String toString() {
		return function.toString();
	}

	@Override
	public int getSize() {
		return function.getIntervalEndpoints().size();
	}
	
	public double max() {
		throw new NotImplementedException();
	}
	
	public double min() {
		throw new NotImplementedException();
	}
}
