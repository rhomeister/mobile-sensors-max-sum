package maxSumController.visualization;

import maxSumController.FactorGraphNode;
import maxSumController.Function;
import maxSumController.InternalFunction;
import maxSumController.InternalVariable;
import maxSumController.Variable;
import maxSumController.discrete.DiscreteMaxSumController;
import edu.uci.ics.jung.graph.SparseGraph;

public class JungFactorGraphAdapter extends
		SparseGraph<FactorGraphNode, FactorGraphEdge> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6700305409267989834L;
	private DiscreteMaxSumController<?> controller;

	public JungFactorGraphAdapter(DiscreteMaxSumController<?> controller) {
		super();

		this.controller = controller;

		for (Function function : controller.getFunctions()) {
			addVertex(function);
		}

		for (Variable<?, ?> variable : controller.getAllVariables()) {
			addVertex(variable);
		}

		for (InternalFunction function : controller.getInternalFunctions()) {
			for (Variable<?, ?> variable : function.getVariableDependencies()) {
				addEdge(new FactorGraphEdge(function, variable), function,
						variable);
			}
		}

		for (InternalVariable<?, ?> variable : controller
				.getInternalVariables()) {
			for (Function function : variable.getFunctionDependencies()) {
				addEdge(new FactorGraphEdge(function, variable), function,
						variable);
			}
		}
	}

	public DiscreteMaxSumController<?> getController() {
		return controller;
	}
}
