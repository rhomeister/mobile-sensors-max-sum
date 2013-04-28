package boundedMaxSum;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import maxSumController.DiscreteVariableState;
import maxSumController.FixedIterationStoppingCriterion;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteMaxSumController;
import maxSumController.discrete.OptimisedDiscreteMarginalMaximisation;
import maxSumController.io.Color;

import org.apache.commons.lang.Validate;

import boundedMaxSum.treeformation.NodeType;
import boundedMaxSum.treeformation.oldghs.GHSTreeFormationController;
import boundedMaxSum.treeformation.oldghs.GHSTreeFormingAgent;
import boundedMaxSum.treeformation.oldghs.LinkEdge;

public class BoundedMaxSumLinkExperiments extends BoundedMaxSumExperiment {

	protected static int REPETITIONS = 10;
	protected Comparable<?> agent = "agent";
	protected boolean debug = true;

	@Override
	public void buildFactorGraphs(HashMap<Integer, HashSet<Integer>> edgeMap) {

		if (discardedFunctions != null) {
			discardedFunctions.clear();
		}

		if (debug) {
			System.out.println("Edge Map = " + edgeMap);
		}

		treeController = new GHSTreeFormationController();
		treeController.addListener(this);

		controller = new BoundedDiscreteMaxSumController("agent",
				new OptimisedDiscreteMarginalMaximisation());

		domain = createVariableDomain(numberOfColours);

		// Create factor Graph representation
		HashMap<Integer, Set<Integer>> variableToFactorsMap = new HashMap<Integer, Set<Integer>>();
		HashMap<Integer, Set<Integer>> factorToVariablesMap = new HashMap<Integer, Set<Integer>>();

		int fnodeId = edgeMap.keySet().size();
		for (Integer nodeId : edgeMap.keySet()) {
			DiscreteInternalVariable<Color> internalVariable = new DiscreteInternalVariable<Color>(
					"v" + nodeId, domain);
			controller.addInternalVariable(internalVariable);
			// GHSTreeFormingAgent node = new GHSTreeFormingAgent(nodeId, null);
			GHSTreeFormingAgent node = new GHSTreeFormingAgent(nodeId,
					(GHSTreeFormationController) treeController,
					internalVariable.getName());
			node.setType(NodeType.VARIABLE);
			treeController.addAgent(node);

			// add edges functions as agent as well
			for (Integer anotherNodeId : edgeMap.keySet()) {
				// avoids double counting of edges
				if ((nodeId < anotherNodeId)
						&& (edgeMap.get(anotherNodeId).contains(nodeId))) {
					// System.out.println("nodeId " + nodeId + " anothernodeid "
					// + anotherNodeId);
					fnodeId++;
					GHSTreeFormingAgent fAgent = new GHSTreeFormingAgent(
							fnodeId,
							(GHSTreeFormationController) treeController, "f"
									+ fnodeId);
					fAgent.setType(NodeType.FUNCTION);
					treeController.addAgent(fAgent);

					// update factor to variables node for nodeId
					if (!factorToVariablesMap.containsKey(fnodeId)) {
						HashSet<Integer> variableIds = new HashSet<Integer>();
						variableIds.add(nodeId);
						variableIds.add(anotherNodeId);
						factorToVariablesMap.put(fnodeId, variableIds);
					} else {
						factorToVariablesMap.get(fnodeId).add(nodeId);
						factorToVariablesMap.get(fnodeId).add(anotherNodeId);
					}

					// update variable to factor node for nodeId
					if (variableToFactorsMap.get(nodeId) == null) {
						HashSet<Integer> factorIds = new HashSet<Integer>();
						factorIds.add(fnodeId);
						variableToFactorsMap.put(nodeId, factorIds);
					} else {
						variableToFactorsMap.get(nodeId).add(fnodeId);
					}

					// update variable to factor node for nodeId
					if (variableToFactorsMap.get(anotherNodeId) == null) {
						HashSet<Integer> factorIds = new HashSet<Integer>();
						factorIds.add(fnodeId);
						variableToFactorsMap.put(anotherNodeId, factorIds);
					} else {
						variableToFactorsMap.get(anotherNodeId).add(fnodeId);
					}

				}
			}
		}

		if (debug) {
			System.out.println("variableToFactorsMap " + variableToFactorsMap);
			System.out.println("factorToVariablesMap " + factorToVariablesMap);
		}

		for (Integer node1Id : variableToFactorsMap.keySet()) {
			Set<Integer> factorSet = variableToFactorsMap.get(node1Id);
			DiscreteInternalVariable<Color> variable1 = (DiscreteInternalVariable<Color>) controller
					.getInternalVariable("v" + node1Id);

			// System.out.println("Node1 " + node1Id);
			for (Integer factor : factorSet) {
				for (Integer node2Id : factorToVariablesMap.get(factor)) {
					// System.out.println("Node2 " + node2Id);
					if (node2Id > node1Id) {
						DiscreteInternalVariable<Color> variable2 = (DiscreteInternalVariable<Color>) controller
								.getInternalVariable("v" + node2Id);

						LinkBoundedInternalFunction func = new TwoVariablesRandomPayoffFunction(
								"f" + factor, variable1, variable2);
						func.setOwningAgentIdentifier(agent);

						func.addVariableDependency(variable1);
						func.addVariableDependency(variable2);
						variable1.addFunctionDependency(func);
						variable2.addFunctionDependency(func);

						controller.addInternalFunction(func);

						// Create edges for tree forming algorithm
						GHSTreeFormingAgent agent1 = treeController
								.getAgent("v" + node1Id);
						GHSTreeFormingAgent functionAgent = treeController
								.getAgent("f" + factor);
						GHSTreeFormingAgent agent2 = treeController
								.getAgent("v" + node2Id);

						agent1.addEdge(new LinkEdge(functionAgent, agent1,
								func, variable1));
						functionAgent.addEdge(new LinkEdge(agent1,
								functionAgent, func, variable1));
						functionAgent.addEdge(new LinkEdge(agent2,
								functionAgent, func, variable2));
						agent2.addEdge(new LinkEdge(functionAgent, agent2,
								func, variable2));
					}
				}

			}

		}

		controller.synchroniseInitFactorGraph();

		for (GHSTreeFormingAgent agent : treeController.getAgents()) {
			Validate.notEmpty(agent.getEdgeMap());
		}

		if (debug) {
			System.out.println("treecontroller " + treeController);
			System.out.println("loopy controller" + controller);
		}

	}

	@Override
	public void treeFormationComplete() {

		// TODO this is where the max-sum is executed
		// change here for iterative bound computation
		// Fiddle with factor graph
		simpleUpperBound = 0;
		double lowerBound = 0.;
		// TODO check there are no problems when introducing the new functions
		simpleUpperBound = controller.computeSimpleUpperBound();
		lowerBound = controller.computeSimpleLowerBound();
		if (debug) {
			System.out.println("simple upper bound " + simpleUpperBound);
			System.out.println(" lower bound " + lowerBound);
		}

		bound = 0;

		// FactorGraphVisualisationUtils.visualise(controller);

		controller.updateFactorGraph(treeController);

		// FactorGraphVisualisationUtils.visualise(controller);

		bound = controller.getBound();
		discardedFunctions = controller.getUpdatedFunctions();

		if (debug) {
			System.out.println("controller cycle free " + controller);
			System.out.println("bound " + bound);
			System.out.println("rejected Functions = "
					+ discardedFunctions.size());
		}

		// Run max-sum

		((DiscreteMaxSumController) controller)
				.setStoppingCriterion(new FixedIterationStoppingCriterion(
						2 * numberOfNodes));

		int i = 0;
		while (!controller.stoppingCriterionIsMet()) {
			controller.calculateNewOutgoingMessages();
			i++;
		}
		commOverhead = controller.getMessageSize();

		double result = 0;

		// Calculate global utility for full factor graph using states returned
		// by masum
		Map<DiscreteInternalVariable<?>, DiscreteVariableState> state = controller
				.computeCurrentState();
		if (debug) {
			System.out.println("current state actual " + state);
		}

		approxUtility = controller.getOriginalFactorGraphValue(state); // 
		if (debug) {
			System.out.println("utility " + approxUtility);
		}

		treeUtility = controller.getSpanningTreeValue(state);

		if (debug) {
			System.out.println("treeutility " + treeUtility);
		}

		upperBound = treeUtility + bound;
		if (debug) {
			System.out.println("upperbound " + upperBound);
		}

		synchronized (this) {
			latestResult = controller.computeCurrentState();
			resultReady = true;
			notify();
		}

	}

	public static void main(String[] args) {
		BoundedMaxSumLinkExperiments exp = new BoundedMaxSumLinkExperiments();

		// Perform experiments using random graphs n = number of nodes c =
		// average connections/node
		int col = 3;
		String distro = "gamma-";
		String filename = distro + col + "Col";
		if (args.length < 2) {

			int n = 3;
			double c = 2.;
			// for (double c = 2; c <= 3; c++) {
			// for (int n = 5; n <= 50; n+=5) {
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(filename
						+ "-" + n + "-" + c + ".dat", true));
				for (int repetitions = 0; repetitions < REPETITIONS; repetitions++) {
					BoundedMaxSumResult res = exp.runExperimentRandomGraph(col,
							n, c);
					if (!Double.isNaN(res.getActualUtility())) {
						System.out.println(res.toCSV());
						out.write(res.toCSV() + "\n");
					}
				}
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// }
			// }
		} else {
			int n = Integer.parseInt(args[0]);
			double c = Double.parseDouble(args[1]);
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(filename
						+ "-" + n + "-" + c + ".dat", true));
				for (int repetitions = 0; repetitions < REPETITIONS; repetitions++) {
					BoundedMaxSumResult res = exp.runExperimentRandomGraph(col,
							n, c);
					if (!Double.isNaN(res.getActualUtility())) {
						System.out.println(res.toCSV());
						out.write(res.toCSV() + "\n");
					}
				}
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
