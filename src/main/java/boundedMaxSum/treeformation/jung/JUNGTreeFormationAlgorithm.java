package boundedMaxSum.treeformation.jung;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import maxSumController.DiscreteInternalFunction;
import maxSumController.FactorGraphNode;
import maxSumController.discrete.DiscreteInternalVariable;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.lang.Validate;

import boundedMaxSum.BoundedDiscreteMaxSumController;
import boundedMaxSum.TreeFormationAlgorithm;
import edu.uci.ics.jung.algorithms.shortestpath.MinimumSpanningForest2;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Tree;

public class JUNGTreeFormationAlgorithm implements TreeFormationAlgorithm {

	private BoundedDiscreteMaxSumController controller;
	private Tree<FactorGraphNode, WeightedEdge> tree;
	private Graph<FactorGraphNode, WeightedEdge> originalGraph;

	public JUNGTreeFormationAlgorithm(BoundedDiscreteMaxSumController controller) {
		this.controller = controller;
	}

	public void execute() {
		JUNGGraphTransformer transformer = new JUNGGraphTransformer();

		originalGraph = transformer.transform(controller);

		MinimumSpanningForest2<FactorGraphNode, WeightedEdge> prim = new MinimumSpanningForest2<FactorGraphNode, WeightedEdge>(
				originalGraph,
				new DelegateForest<FactorGraphNode, WeightedEdge>(),
				DelegateTree.<FactorGraphNode, WeightedEdge> getFactory(),
				new Transformer<WeightedEdge, Double>() {
					@Override
					public Double transform(WeightedEdge input) {
						return -input.getWeight();
					}
				});

		Forest<FactorGraphNode, WeightedEdge> forest = prim.getForest();
		Validate.isTrue(forest.getTrees().size() == 1);
		tree = forest.getTrees().iterator().next();
	}

	protected Tree<FactorGraphNode, WeightedEdge> getTree() {
		return tree;
	}

	@Override
	public Set<DiscreteInternalVariable> getKeptVariables(
			DiscreteInternalFunction function) {
		Collection<FactorGraphNode> neighbors = tree.getNeighbors(function);

		Set<DiscreteInternalVariable> result = new HashSet<DiscreteInternalVariable>();

		for (FactorGraphNode neighbour : neighbors) {
			result.add((DiscreteInternalVariable) neighbour);
		}

		return result;
	}

	@Override
	public Set<DiscreteInternalVariable> getRejectedVariables(
			DiscreteInternalFunction function) {
		return new HashSet(CollectionUtils.subtract(function
				.getDiscreteVariableDependencies(), getKeptVariables(function)));
	}

	@Override
	public double getWeight(DiscreteInternalFunction function,
			DiscreteInternalVariable variable) {
		Collection<WeightedEdge> findEdgeSet = originalGraph.findEdgeSet(
				function, variable);
		Validate.isTrue(findEdgeSet.size() == 1);
		WeightedEdge edge = originalGraph.findEdge(function, variable);
		WeightedEdge edge1 = originalGraph.findEdge(variable, function);
		Validate.isTrue(edge == edge1);
		return edge.getWeight();
	}

	@Override
	public boolean hasRejectBranch(DiscreteInternalFunction function) {
		return tree.getNeighborCount(function) < function
				.getVariableDependencies().size();
	}
}
