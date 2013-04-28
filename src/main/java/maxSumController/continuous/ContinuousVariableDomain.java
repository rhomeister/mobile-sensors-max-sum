package maxSumController.continuous;

import maxSumController.VariableDomain;

public interface ContinuousVariableDomain extends
		VariableDomain<ContinuousVariableState> {

	public double getLowerBound();

	public double getUpperBound();
}
