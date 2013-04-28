package maxSumController.continuous.linear;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import maxSumController.AbstractInternalFunction;
import maxSumController.Variable;
import maxSumController.continuous.ContinuousVariable;
import maxSumController.continuous.ContinuousVariableState;
import maxSumController.continuous.LineSegment;

import org.apache.commons.lang.Validate;

public class ContinuousInternalFunction extends AbstractInternalFunction {

	private MultiVariatePieceWiseLinearFunction function;

	private List<ContinuousVariable> variableList = new ArrayList<ContinuousVariable>();

	private MultiVariatePieceWiseLinearFunctionFactory functionFactory;

	private int variableCount;

	public ContinuousInternalFunction(String name,
			MultiVariatePieceWiseLinearFunction function) {
		super(name);
		this.function = function;
		variableCount = function.getDimensionCount();
	}

	public ContinuousInternalFunction(String name, int variableCount,
			MultiVariatePieceWiseLinearFunctionFactory factory) {
		super(name);
		this.variableCount = variableCount;
		this.functionFactory = factory;
	}

	@Override
	protected void validateVariableDependency(Variable<?, ?> v) {
		Validate.isTrue(!variableList.contains(v));

		Validate
				.isTrue(
						variableList.size() < variableCount,
						"Variable dependencies exceeds the number of variables defined in the function. Expected "
								+ variableCount + ". " + v + " " + variableList);

		ContinuousVariable variable = (ContinuousVariable) v;

		if (function != null) {
			int currentVariableCount = variableList.size();
			Validate.isTrue(function.getDomain().getDomainStart(
					currentVariableCount) == variable.getDomain()
					.getLowerBound());
			Validate.isTrue(function.getDomain().getDomainEnd(
					currentVariableCount) == variable.getDomain()
					.getUpperBound());
		}

		variableList.add(variable);

		if (variableCount == variableList.size() && functionFactory != null) {
			Validate.isTrue(function == null);
			function = functionFactory.create(variableList);
		}
	}

	public double evaluate(
			Map<ContinuousVariable, ContinuousVariableState> state) {
		ContinuousVariableState[] stateArray = new ContinuousVariableState[state
				.size()];

		for (ContinuousVariable variable : state.keySet()) {
			stateArray[variableList.indexOf(variable)] = state.get(variable);
		}

		return evaluate(stateArray);
	}

	public double evaluate(ContinuousVariableState[] states) {
		Validate.isTrue(states.length == variableList.size());

		NDimensionalPoint point = new NDimensionalPoint(states.length);

		for (int i = 0; i < states.length; i++) {
			point.setCoordinate(i, states[i].getValue());
		}

		return function.evaluate(point);
	}

	public List<LineSegment> project(ContinuousVariable variable) {
		return function.project(variableList.indexOf(variable));
	}

	public MultiVariatePieceWiseLinearFunction getFunction() {
		return function;
	}

	public List<ContinuousVariable> getVariableList() {
		return variableList;
	}
}