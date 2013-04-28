package maxSumController.continuous.linear;

import java.util.List;

import maxSumController.continuous.ContinuousVariable;

public interface MultiVariatePieceWiseLinearFunctionFactory {

	MultiVariatePieceWiseLinearFunction create(
			List<ContinuousVariable> variableList);

}
