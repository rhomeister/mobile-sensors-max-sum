package boundedMaxSum;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import maxSumController.DiscreteInternalFunction;
import maxSumController.FixedIterationStoppingCriterion;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteMaxSumController;
import maxSumController.discrete.DiscreteVariableDomain;
import maxSumController.discrete.OptimisedDiscreteMarginalMaximisation;
import maxSumController.io.Color;
import maxSumController.io.ColorDomain;

import org.apache.commons.lang.Validate;

import boundedMaxSum.treeformation.oldghs.Edge;
import boundedMaxSum.treeformation.oldghs.GHSTreeFormationController;
import boundedMaxSum.treeformation.oldghs.GHSTreeFormingAgent;

/*
 * @author mw08v
 * A class to read in data for the arbitrary graph colouring scenario, 
 * and perform bounded max-sum on it.
 * Function node and variable nodes for the complete factor graph are created
 * when the graph is first read in, but function nodes are not added to the MaxSumController
 * until the tree formation algorithm has determined which edges to keep.
 * 
 * This class is largely made redundant by BoundedMaxSumExperiment.
 */
public class BoundedTreeFormingGraphColourReader implements
		TreeFormationListener {

	protected GHSTreeFormationController treeController;

	protected Map<Integer, Set<Integer>> edgeMap;

	protected DiscreteMaxSumController<DiscreteInternalFunction> controller;

	// used to build factor graph for exhaustive search
	protected DiscreteMaxSumController<DiscreteInternalFunction> optimalController;

	public BoundedTreeFormingGraphColourReader() {
		File file = new File("src/main/resources/makemeatree.dat");

		Scanner scanner = null;
		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		treeController = new GHSTreeFormationController();
		treeController.addListener(this);
		controller = new DiscreteMaxSumController("agent",
				new OptimisedDiscreteMarginalMaximisation());
		optimalController = new DiscreteMaxSumController("agent",
				new OptimisedDiscreteMarginalMaximisation());

		int numberOfNodes = readNumberOfNodes(scanner, controller);
		int numberOfColors = readNumberOfColors(scanner);

		// Initialise edgemap
		edgeMap = new HashMap<Integer, Set<Integer>>();
		for (int i = 0; i < numberOfNodes; i++) {
			edgeMap.put(i, new HashSet<Integer>());
		}

		DiscreteVariableDomain<Color> domain = createVariableDomain(numberOfColors);

		// System.out.println(domain.getStates());

		readNodesToColorMap(scanner, controller, numberOfColors, domain);
		readEdges(scanner, controller);

		System.out.println();
		System.out.println("Graph read. Calculating optimal solution...");

		ArbitraryGraphColouringExhaustiveSolution exhaust = new ArbitraryGraphColouringExhaustiveSolution();
		double result = exhaust.calculateOptimal(optimalController, domain);

		System.out.println("Optimal allocation: "
				+ exhaust.getOptimalConfiguration() + " = " + result);

		System.out.println();

		System.out.println("Running tree formation algorithm....");
		try {
			treeController.execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Control flow resumes in treeFormationComplete()
	}

	public static void main(String[] args) throws IOException {
		new BoundedTreeFormingGraphColourReader();

	}

	private static DiscreteVariableDomain<Color> createVariableDomain(
			int numberOfColors) {
		DiscreteVariableDomain<Color> domain = new ColorDomain();

		for (int i = 0; i < numberOfColors; i++) {
			domain.add(new Color("" + i));
		}

		return domain;
	}

	private void readEdges(Scanner scanner, DiscreteMaxSumController controller) {
		scanner.nextLine();

		Validate.isTrue(scanner.nextLine().equals("Edges {"));

		String next = scanner.next();
		while (!next.equals("}")) {
			Validate.isTrue(next.equals("<"));
			int node1Id = scanner.nextInt();
			Validate.isTrue(scanner.next().equals(","));
			int node2Id = scanner.nextInt();
			Validate.isTrue(scanner.next().equals(">"));
			next = scanner.next();

			addEdge(controller, node1Id, node2Id);
		}
	}

	private void addEdge(DiscreteMaxSumController<DiscreteInternalFunction> controller, int node1Id,
			int node2Id) {

		if (edgeMap.get(node2Id).contains(node1Id)) {
			// Already added this edge the other way round - skip
		} else {
			edgeMap.get(node1Id).add(node2Id);

			DiscreteInternalVariable<?> variable1 = controller
					.getInternalVariable("v" + node1Id);
			DiscreteInternalVariable<?> variable2 = controller
					.getInternalVariable("v" + node2Id);

			// Pick the variable with the least function dependencies to assign
			// the new functionto
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

			BoundedInternalFunction func = new SingleVariablePayoffFunction("f"
					+ node1Id + "-" + node2Id, assignedNode);

			func.addVariableDependency(variable1);
			func.addVariableDependency(variable2);
			variable1.addFunctionDependency(func);
			variable2.addFunctionDependency(func);

			// Add function to complete factor graph for exhaustive solution.
			// Defer adding to factor graph for max-sum until later.
			optimalController.addInternalFunction(func);

			// Create edges for tree forming algorithm
			GHSTreeFormingAgent agent1 = treeController.getAgent(""+node1Id);
			GHSTreeFormingAgent agent2 = treeController.getAgent(""+node2Id);

			agent1.addEdge(new Edge(agent2, agent1, func));
			agent2.addEdge(new Edge(agent1, agent2, func));
		}
	}

	private void readNodesToColorMap(Scanner scanner,
			DiscreteMaxSumController controller, int numberOfColors,
			DiscreteVariableDomain<Color> domain) {
		Validate.isTrue(scanner.next().equals("NodesColorMap{"));
		// for(int i = 0; i < )

		String next = scanner.next();
		while (!next.equals("}")) {
			Validate.isTrue(next.equals("("));
			int nodeId = scanner.nextInt();
			Validate.isTrue(scanner.next().equals(","));
			int colorId = scanner.nextInt();
			Validate.isTrue(scanner.next().equals(")"));
			next = scanner.next();

			createNode(nodeId, colorId, numberOfColors, controller, domain);
		}
	}

	private void createNode(int nodeId, int colorId, int numberOfColors,
			DiscreteMaxSumController controller,
			DiscreteVariableDomain<Color> domain) {

		DiscreteInternalVariable<Color> internalVariable = new DiscreteInternalVariable<Color>(
				"v" + nodeId, domain);
		// Add variable also to complete factor graph for exhaustive solution
		controller.addInternalVariable(internalVariable);
		optimalController.addInternalVariable(internalVariable);

		// Create node for tree forming algorithm
		GHSTreeFormingAgent node = new GHSTreeFormingAgent(nodeId,
				treeController,"v"+nodeId);
		treeController.addAgent(node);
	}

	private static int readNumberOfColors(Scanner scanner) {
		Validate.isTrue(scanner.next().equals("NumberOfColors"));
		return scanner.nextInt();
	}

	private static int readNumberOfNodes(Scanner scanner,
			DiscreteMaxSumController controller) {
		Validate.isTrue(scanner.next().equals("NumberOfNodes"));
		return scanner.nextInt();
	}

	@Override
	public void treeFormationComplete() {
		// Build factor graph
		System.out.println();
		System.out.println("Tree formation complete, ");
		Set<DiscreteInternalFunction> discardedFunctions = new HashSet<DiscreteInternalFunction>();

		for (GHSTreeFormingAgent node : treeController.getAgents()) {
			System.out.println("Edges from Node " + node.getID());
			for (Edge edge : node.getEdgeMap().values()) {
				if (edge.getState() == GHSTreeFormingAgent.EdgeState.REJECTED) {
					// Edge not in tree - remove variable dependencies
					for (DiscreteInternalVariable<?> var : controller
							.getInternalVariables()) {
						var.removeFunctionDependency(edge.function.getName());
					}

				} else if (edge.getState() == GHSTreeFormingAgent.EdgeState.BRANCH) {
					// Add function to controller
					controller.addInternalFunction(edge.function);
					System.out.println(edge.getEndpoint());
				}
			}
			System.out.println();
		}

		// Run max-sum
		System.out.println("Running max-sum");
		// As we have a tree, max-sum is guaranteed to converge after a single
		// iteration
		controller.setStoppingCriterion(new FixedIterationStoppingCriterion(0));

		long timestart = System.currentTimeMillis();

		System.out.println(controller);

		int i = 0;
		while (!controller.stoppingCriterionIsMet()) {
			controller.calculateNewOutgoingMessages();
			System.out.println("iteration " + i);
			System.out.println(controller.computeCurrentState());
			// System.out.println(globalUtility(controller));

			i++;
		}

		long timestop = System.currentTimeMillis();

		// Map<VariableJointState, Double> values =
		// CachingInternalFunction.values;

		// System.out.println(values.size());

		System.out.println(controller.computeCurrentState());
		System.out.println("Execution time " + (timestop - timestart));
	}

}
