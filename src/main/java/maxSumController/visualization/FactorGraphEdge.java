package maxSumController.visualization;

import maxSumController.Function;
import maxSumController.Variable;

public class FactorGraphEdge {

	private Function function;
	private Variable<?, ?> variable;

	public FactorGraphEdge(Function function, Variable<?, ?> variable) {
		this.function = function;
		this.variable = variable;
	}

	public Function getFunction() {
		return function;
	}

	public Variable<?, ?> getVariable() {
		return variable;
	}

}
