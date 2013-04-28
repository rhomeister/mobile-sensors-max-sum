package maxSumController.io;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import maxSumController.DiscreteInternalFunction;
import maxSumController.PreferenceInternalFunctionSingleVariable;
import maxSumController.discrete.DiscreteInternalVariable;
import maxSumController.discrete.DiscreteMaxSumController;
import maxSumController.discrete.DiscreteVariableDomain;

import org.apache.commons.lang.Validate;

public class GraphColorReader {

	
	private Scanner scanner;
	private DiscreteMaxSumController<DiscreteInternalFunction> controller;
	private File file;
	private boolean usePreference = false; 

	public DiscreteMaxSumController getController() {
		return controller;
	}
	
	public Scanner getScanner() {
		return scanner;
	}
	
	public File getFile() {
		return file;
	}
	
	public GraphColorReader(String fileName, boolean usePreference) throws IOException {
		this.usePreference = usePreference;
		file = new File(fileName);
		scanner = new Scanner(file);
		
		controller = new DiscreteMaxSumController("agent");
		
		int numberOfNodes = readNumberOfNodes();
		int numberOfColors = readNumberOfColors();
		DiscreteVariableDomain<Color> domain = createVariableDomain(numberOfColors);
		
		System.out.println(domain.getStates());
		
		readNodesToColorMap(numberOfColors, domain);
		readEdges();
		
		// System.out.println(controller.getInternalFunctions());
		// System.out.println(controller.getInternalFunctions().size());
		// System.out.println(controller.getInternalVariables().size());
		
		// System.out.println(controller);
	}
	
	public static void main(String[] args) throws IOException {

		long timestart = System.currentTimeMillis();

		GraphColorReader gcr = new GraphColorReader("GCDataGraph-randomColorable.dat",false);
		
		DiscreteMaxSumController my_controller = gcr.getController();
		
		System.out.println(gcr.getController());

		for (int i = 0; i < 100; i++) {
			my_controller.calculateNewOutgoingMessages();
			System.out.println("iteration " + i);
			System.out.println(my_controller.computeCurrentState());
			// System.out.println(globalUtility(controller));
			System.out
					.println(new GlobalConflictsMetric().evaluate(my_controller));
		}

		long timestop = System.currentTimeMillis();

		// Map<VariableJointState, Double> values =
		// CachingInternalFunction.values;

		// System.out.println(values.size());

		System.out.println(my_controller.computeCurrentState());
		System.out.println("Execution time " + (timestop - timestart));

	}

	private static DiscreteVariableDomain<Color> createVariableDomain(
			int numberOfColors) {
		DiscreteVariableDomain<Color> domain = new ColorDomain();

		for (int i = 0; i < numberOfColors; i++) {
			domain.add(new Color("" + i));
		}

		return domain;
	}

	private void readEdges() {
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

			addEdge(node1Id, node2Id);
		}
	}

	private void addEdge(int node1Id, int node2Id) {
		DiscreteInternalFunction function1 = controller.getInternalFunction("f"
				+ node1Id);
		DiscreteInternalVariable<?> variable1 = controller
				.getInternalVariable("v" + node1Id);

		DiscreteInternalFunction function2 = controller.getInternalFunction("f"
				+ node2Id);
		DiscreteInternalVariable<?> variable2 = controller
				.getInternalVariable("v" + node2Id);

		function1.addVariableDependency(variable2);
		function2.addVariableDependency(variable1);
	}

	private void readNodesToColorMap(int numberOfColors,
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

			if (usePreference ){
				createPreferenceNode(nodeId, colorId, numberOfColors, domain);
			} else {
				createNode(nodeId, colorId, numberOfColors, domain);
			}	
		}
	}

	private void createNode(int nodeId, int colorId, int numberOfColors,
			DiscreteVariableDomain<Color> domain) {
		DiscreteInternalVariable<Color> internalVariable = new DiscreteInternalVariable<Color>(
				"v" + nodeId, domain);
		DiscreteInternalFunction internalFunction = new SingleVariableConflictFunction("f" + nodeId,
				internalVariable);
				

		
//		DiscreteInternalFunction internalFunction = new PreferenceInternalFunctionSingleVariable(
//				new SingleVariableConflictFunction("f" + nodeId,
//						internalVariable), internalVariable);
		
		
		internalFunction.addVariableDependency(internalVariable);

		controller.addInternalFunction(internalFunction);
		controller.addInternalVariable(internalVariable);
	}

	private void createPreferenceNode(int nodeId, int colorId, int numberOfColors,
			DiscreteVariableDomain<Color> domain) {
		DiscreteInternalVariable<Color> internalVariable = new DiscreteInternalVariable<Color>(
				"v" + nodeId, domain);
		
		
		DiscreteInternalFunction internalFunction = new PreferenceInternalFunctionSingleVariable(
				new SingleVariableConflictFunction("f" + nodeId,
						internalVariable), internalVariable);		
		internalFunction.addVariableDependency(internalVariable);

		controller.addInternalFunction(internalFunction);
		controller.addInternalVariable(internalVariable);
	}
	
	
	private int readNumberOfColors() {
		Validate.isTrue(scanner.next().equals("NumberOfColors"));
		return scanner.nextInt();
	}

	private int readNumberOfNodes() {
	 Validate.isTrue(scanner.next().equals("NumberOfNodes"));
	 return scanner.nextInt();
	}

}
