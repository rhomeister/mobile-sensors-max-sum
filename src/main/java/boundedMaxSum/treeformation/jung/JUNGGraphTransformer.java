package boundedMaxSum.treeformation.jung;

import maxSumController.FactorGraphNode;
import maxSumController.Function;
import maxSumController.discrete.DiscreteInternalVariable;

import org.apache.commons.collections15.Transformer;

import boundedMaxSum.BoundedDiscreteMaxSumController;
import boundedMaxSum.BoundedInternalFunction;
import boundedMaxSum.LinkBoundedInternalFunction;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

public class JUNGGraphTransformer
		implements
		Transformer<BoundedDiscreteMaxSumController, Graph<FactorGraphNode, WeightedEdge>> {

	@Override
	public Graph<FactorGraphNode, WeightedEdge> transform(
			BoundedDiscreteMaxSumController input) {

		Graph<FactorGraphNode, WeightedEdge> graph = new UndirectedSparseGraph<FactorGraphNode, WeightedEdge>();

		for (DiscreteInternalVariable<?> variable : input
				.getInternalVariables()) {
			graph.addVertex(variable);
		}

		for (BoundedInternalFunction function : input.getInternalFunctions()) {
			graph.addVertex(function);
		}

		// adding links

		for (DiscreteInternalVariable<?> variable : input
				.getInternalVariables()) {
			for (Function function : variable.getFunctionDependencies()) {
				double weight = ((LinkBoundedInternalFunction) function)
						.getWeight(variable.getName());

				graph.addEdge(new WeightedEdge(weight), variable, function);
			}
		}

		return graph;
	}
}
