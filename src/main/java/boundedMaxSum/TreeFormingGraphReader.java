package boundedMaxSum;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.lang.Validate;

/*
 * @author mw08v
 * A class to read in data for the arbitrary graph colouring scenario, 
 * and perform bounded max-sum on it.
 */
public class TreeFormingGraphReader {

	protected static Map<Integer, TreeFormingAgent> nodes;

	public static void main(String[] args) throws IOException {
		File file = new File("src/main/resources/makemeatree.dat");

		Scanner scanner = new Scanner(file);

		nodes = new HashMap<Integer, TreeFormingAgent>();
		int numberOfNodes = readNumberOfNodes(scanner);
		int numberOfColors = readNumberOfColors(scanner);

		readNodesToColorMap(scanner, numberOfColors);
		readEdges(scanner);

		scanner.close();

		// Grow a tree!
		for (TreeFormingAgent node : nodes.values()) {
			node.initialise();
		}

		file = new File("src/main/resources/makemeatree.out");
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));

		writeHeader(writer, numberOfNodes, numberOfColors);
		writeNodeColourMap(writer);
		writeEdges(writer);
		writer.close();
	}

	private static void readEdges(Scanner scanner) {
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

	private static void addEdge(int node1Id, int node2Id) {
		nodes.get(node1Id).addNeighbour(nodes.get(node2Id));
		nodes.get(node2Id).addNeighbour(nodes.get(node1Id));
	}

	private static void readNodesToColorMap(Scanner scanner, int numberOfColors) {
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

			createNode(nodeId, colorId, numberOfColors);
		}
	}

	private static void createNode(int nodeId, int colorId, int numberOfColors) {
		TreeFormingAgent node = new TreeFormingAgent(
				new HashSet<TreeFormingAgent>(), nodeId, colorId);
		nodes.put(nodeId, node);
	}

	private static int readNumberOfColors(Scanner scanner) {
		Validate.isTrue(scanner.next().equals("NumberOfColors"));
		return scanner.nextInt();
	}

	private static int readNumberOfNodes(Scanner scanner) {
		Validate.isTrue(scanner.next().equals("NumberOfNodes"));
		return scanner.nextInt();
	}

	private static void writeHeader(Writer writer, int numberOfNodes,
			int numberOfColours) throws IOException {
		writer.write("NumberOfNodes " + numberOfNodes + "\n");
		writer.write("NumberOfColors " + numberOfColours + "\n");
	}

	private static void writeNodeColourMap(Writer writer) throws IOException {
		writer.write("NodesColorMap{\n");
		for (TreeFormingAgent node : nodes.values()) {
			writer.write(" ( " + node.getNodeid() + " , " + node.getColourid()
					+ " )");
		}
		writer.write("\n } \n");
	}

	private static void writeEdges(Writer writer) throws IOException {
		writer.write("Edges {\n");
		for (TreeFormingAgent node : nodes.values()) {
			for (TreeFormingAgent neighbour : node.getInternalNodes()) {
				writer.write(" < " + node.getNodeid() + " , "
						+ neighbour.getNodeid() + " >");
			}
			writer.write("\n");
		}
		writer.write("\n} \n");
	}
}
