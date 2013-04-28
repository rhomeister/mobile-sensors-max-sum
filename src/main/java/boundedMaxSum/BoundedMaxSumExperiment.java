package boundedMaxSum;

import graphManager.GraphManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import maxSumController.DiscreteVariableState;
import maxSumController.FixedIterationStoppingCriterion;
import maxSumController.InternalVariable;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteMaxSumController;
import maxSumController.discrete.DiscreteVariable;
import maxSumController.discrete.DiscreteVariableDomain;
import maxSumController.discrete.OptimisedDiscreteMarginalMaximisation;
import maxSumController.io.Color;
import maxSumController.io.ColorDomain;

import org.apache.commons.lang.Validate;

import boundedMaxSum.treeformation.oldghs.Edge;
import boundedMaxSum.treeformation.oldghs.GHSTreeFormationController;
import boundedMaxSum.treeformation.oldghs.GHSTreeFormingAgent;

/**
 * Class to perform experiments with bounded max sum. This code is essential a
 * modularised version of BoundedMaxSumGraphColourReader, designed to be run
 * with graphs generated on the fly.
 * 
 * @author mpw104
 * 
 */
public class BoundedMaxSumExperiment implements TreeFormationListener {

	private boolean debug = true;

	protected BoundedDiscreteMaxSumController controller;

	// protected DiscreteMaxSumController<DiscreteInternalFunction>
	// optimalController;

	protected TreeFormationController<GHSTreeFormingAgent> treeController;

	protected int numberOfNodes;

	protected int numberOfColours;

	protected boolean resultReady = false;

	protected Map<DiscreteInternalVariable<?>, DiscreteVariableState> latestResult;

	protected double utility;

	protected double bound;

	protected Set<BoundedInternalFunction> discardedFunctions;

	protected DiscreteVariableDomain<Color> domain;

	protected double treeUtility = 0;

	protected double upperBound;

	protected double simpleUpperBound;

	protected double commOverhead;

	protected double approxUtility;

	public Map<DiscreteInternalVariable<?>, DiscreteVariableState> runBoundedMaxSum()
			throws Exception {

		// Run tree formation algorithm
		treeController.execute();

		synchronized (this) {
			while (!resultReady) {
				try {
					wait();
					resultReady = false;
					return latestResult;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
		return null;
	}

	/**
	 * Builds two factors graphs and sets up a MaxSumController for each.
	 * controller is the graph to form a tree with. Adding functions is
	 * postponed until later. optimalController is the graph used to find the
	 * optimal result. Functions are added when the graph is built. Also builds
	 * graph for tree formation.
	 * 
	 * @param colourMap
	 * @param edgeMap
	 */
	public void buildFactorGraphs(HashMap<Integer, HashSet<Integer>> edgeMap) {
		// treeController = new KruskalTreeFormationController();
		treeController = new GHSTreeFormationController();
		treeController.addListener(this);
		controller = new BoundedDiscreteMaxSumController("agent",
				new OptimisedDiscreteMarginalMaximisation());

		domain = createVariableDomain(numberOfColours);

		// Create nodes
		for (Integer nodeId : edgeMap.keySet()) {
			DiscreteInternalVariable<Color> internalVariable = new DiscreteInternalVariable<Color>(
					"v" + nodeId, domain);
			controller.addInternalVariable(internalVariable);
			// Create node for tree forming algorithm

			// GHSTreeFormingAgent node = new GHSTreeFormingAgent(nodeId, null);
			GHSTreeFormingAgent node = new GHSTreeFormingAgent(nodeId,
					(GHSTreeFormationController) treeController,
					internalVariable.getName());
			treeController.addAgent(node);
		}

		// Initialise created edgemap
		HashMap<Integer, Set<Integer>> doneEdges = new HashMap<Integer, Set<Integer>>();
		for (int i = 0; i < numberOfNodes; i++) {
			doneEdges.put(i, new HashSet<Integer>());
		}

		// Create edges
		for (Integer node1Id : edgeMap.keySet()) {
			for (Integer node2Id : edgeMap.get(node1Id)) {
				if (doneEdges.get(node2Id).contains(node1Id)) {
					// Already added this edge the other way round - skip
				} else {
					doneEdges.get(node1Id).add(node2Id);

					DiscreteInternalVariable<?> variable1 = controller
							.getInternalVariable("v" + node1Id);
					DiscreteInternalVariable<?> variable2 = controller
							.getInternalVariable("v" + node2Id);

					// Pick the variable with the least function dependencies to
					// assign the new functionto
					DiscreteInternalVariable<?> assignedNode = null;
					int assignedId = -1;
					if (variable1.getFunctionDependencies().size() > variable2
							.getFunctionDependencies().size()) {
						assignedNode = variable2;
						assignedId = node2Id;
					} else {
						assignedNode = variable1;
						assignedId = node1Id;
					}

					BoundedInternalFunction func = new SingleVariablePayoffFunction(
							"f" + node1Id + "-" + node2Id, assignedNode);

					func.addVariableDependency(variable1);
					func.addVariableDependency(variable2);
					variable1.addFunctionDependency(func);
					variable2.addFunctionDependency(func);

					controller.addInternalFunction(func);

					// Create edges for tree forming algorithm
					GHSTreeFormingAgent agent1 = treeController.getAgent(""
							+ node1Id);
					GHSTreeFormingAgent agent2 = treeController.getAgent(""
							+ node2Id);

					agent1.addEdge(new Edge(agent2, agent1, func));
					agent2.addEdge(new Edge(agent1, agent2, func));
				}
			}
		}

		controller.synchroniseInitFactorGraph();

		for (GHSTreeFormingAgent agent : treeController.getAgents()) {
			Validate.notEmpty(agent.getEdgeMap());
		}

	}

	public Map<? extends DiscreteVariable<?>, DiscreteVariableState> calculateOptimalAllocation() {

		ArbitraryGraphColouringExhaustiveSolution exhaust = new ArbitraryGraphColouringExhaustiveSolution();
		// utility = exhaust.calculateOptimal(optimalController, domain);// /
		utility = controller.getOptimalValue();// /
		// optimalController.getInternalFunctions().size();
		return exhaust.getOptimalConfiguration().getVariableJointStates();
	}

	protected static DiscreteVariableDomain<Color> createVariableDomain(
			int numberOfColors) {
		DiscreteVariableDomain<Color> domain = new ColorDomain();

		for (int i = 0; i < numberOfColors; i++) {
			domain.add(new Color("" + i));
		}

		return domain;
	}

	@Override
	public void treeFormationComplete() {
		// Fiddle with factor graph
		discardedFunctions = new HashSet<BoundedInternalFunction>();

		for (GHSTreeFormingAgent node : treeController.getAgents()) {

			for (Edge edge : node.getEdgeMap().values()) {
				if (edge.getState() == GHSTreeFormingAgent.EdgeState.REJECTED) {
					// Edge not in tree - remove variable dependencies
					for (InternalVariable<?, ?> var : controller
							.getInternalVariables()) {
						var.removeFunctionDependency(edge.function.getName());
					}
					discardedFunctions.add(edge.function);

				} else if (edge.getState() == GHSTreeFormingAgent.EdgeState.BRANCH) {
					// Add function to controller
					controller.addInternalFunction(edge.function);

				}
			}

		}
		bound = 0;
		// Calculate bound
		for (BoundedInternalFunction f : discardedFunctions) {
			System.out.println("called minimum in discarded function");
			bound += f.getMaximumBound() - f.getMinimumBound();
		}

		// Run max-sum

		((DiscreteMaxSumController) controller)
				.setStoppingCriterion(new FixedIterationStoppingCriterion(100));

		int i = 0;
		while (!controller.stoppingCriterionIsMet()) {
			controller.calculateNewOutgoingMessages();
			i++;
		}

		double result = 0;

		// Calculate global utility for full factor graph using states returned
		// by masum
		Map<? extends DiscreteVariable<?>, DiscreteVariableState> state = controller
				.computeCurrentState();
		approxUtility = controller.getOriginalFactorGraphValue(state); // 

		synchronized (this) {
			latestResult = controller.computeCurrentState();
			resultReady = true;
			notify();
		}
	}

	public BoundedMaxSumResult runExperimentInstance(
			HashMap<Integer, HashSet<Integer>> edgeMap, int numColours,
			int numNodes, double averageConnectionsPerNode) {
		BoundedMaxSumResult result = new BoundedMaxSumResult();
		numberOfNodes = numNodes;
		numberOfColours = numColours;
		try {
			buildFactorGraphs(edgeMap);

			if (debug) {
				System.out.println("Factor graph built");
			}

			result.setAverageConnectionsPerNode(averageConnectionsPerNode);
			result.setNumColours(numColours);
			result.setNumNodes(numNodes);

			computeUtility(numNodes, result);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			result.setActualUtility(Double.NaN);
		}

		return result;
	}

	protected void computeUtility(int numNodes, BoundedMaxSumResult result)
			throws Exception {
		// Calculate optimal
		if (numNodes <= 15) {
			result.setOptimalConfiguration(calculateOptimalAllocation());
			result.setOptimalUtility(utility);
			if (debug) {
				System.out.println("optimal utility " + utility);
				System.out.println("optimal state = "
						+ result.getOptimalConfiguration());
			}
		} else {
			result.setOptimalUtility(0.0);
		}

		if (debug) {
			System.out.println("Complete factor graph: ");
			System.out.print(controller);
		}

		// Calculate bounded result
		long startTime = System.currentTimeMillis();
		result.setActualConfiguration(runBoundedMaxSum());
		long endTime = System.currentTimeMillis();
		result.setMSCompletionTime(endTime - startTime);
		result.setActualUtility(approxUtility);
		result.setCommOverhead(commOverhead);

		result.setTreeUtility(treeUtility);
		result.setUpperBound(upperBound);
		result.setSimpleUpperBound(simpleUpperBound);

		result.setBound(bound);
		result.setEdgesDeleted(discardedFunctions.size());
		result.setEdges(controller.initFunctions.size());

		if (debug) {
			System.out.println("Tree: ");
			System.out.print(controller);
		}
	}

	public BoundedMaxSumResult runExperimentRandomGraph(int numColours,
			int numNodes, double averageConnectionsPerNode) {
		GraphManager gm = new GraphManager(numNodes, numColours);

		// Generate graph
		gm.genConnectedGraph(averageConnectionsPerNode);

		return runExperimentInstance(gm.getEdgeMap(), numColours, numNodes,
				averageConnectionsPerNode);

	}

	public static void main(String[] args) {

		BoundedMaxSumExperiment exp = new BoundedMaxSumExperiment();
		/*
		 * Perform experiments using ADOPT graphs GraphManager gm = new
		 * GraphManager(); String adoptDir = "adoptProblmes"; // directory where
		 * the ADOPT graph files live for (File graphFile : new
		 * File(adoptDir).listFiles()) {
		 * gm.readADOPTGraph(graphFile.getAbsolutePath()); BoundedMaxSumResult
		 * res = exp.runExperimentInstance(gm.getEdgeMap(),
		 * gm.getNumberOfColurs(), gm.getNumberOfNodes(), 0); if
		 * (!Double.isNaN(res.getActualUtility())) {
		 * System.out.println(res.toCSV()); } }
		 */

		// Perform experiments using random graphs n = number of nodes c =
		// average connections/node
		for (;;) {

			for (double c = 1.5; c < 5; c++) {
				for (int n = 3; n < 15; n++) {
					BoundedMaxSumResult res = exp.runExperimentRandomGraph(2,
							n, c);
					if (!Double.isNaN(res.getActualUtility())) {
						System.out.println(res.toCSV());
					}
				}
			}

		}
	}
}