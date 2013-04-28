package graphManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;

public class GraphManager {

	private static final int UNIFORM_COLOUR = 1;

	// private static final int NOT_UNIFORM_COLOUR = 0;

	private HashMap<Integer, Integer> colourMap;

	private HashMap<Integer, HashSet<Integer>> edgeMap;

	private int numberOfNodes;

	private int numberOfColours;

	private int numberOfEdges;

	public void reset() {
		colourMap.clear();
		edgeMap.clear();
		numberOfNodes = 0;
		numberOfColours = 0;
		numberOfEdges = 0;
	}

	public int getNumberOfNodes() {
		return numberOfNodes;
	}

	public void setNumberOfNodes(int nn) {
		numberOfNodes = nn;
	}

	public int getNumberOfColurs() {
		return numberOfColours;
	}

	public void setNumberOfColours(int nc) {
		numberOfColours = nc;
	}

	public int getNumberOfEdges() {
		return numberOfEdges;
	}

	public HashMap<Integer, Integer> getColourMap() {
		return colourMap;
	}

	public void setColourMap(HashMap<Integer, Integer> cm) {
		colourMap = cm;
	}

	public HashMap<Integer, HashSet<Integer>> getEdgeMap() {
		return edgeMap;
	}

	public void setEdgeMap(HashMap<Integer, HashSet<Integer>> em) {
		edgeMap = em;
	}

	public GraphManager() {
		colourMap = new HashMap<Integer, Integer>();
		edgeMap = new HashMap<Integer, HashSet<Integer>>();
	};

	public GraphManager(int nn, int nc) {
		colourMap = new HashMap<Integer, Integer>();
		edgeMap = new HashMap<Integer, HashSet<Integer>>();
		numberOfNodes = nn;
		numberOfColours = nc;
	};

	public void writeGraph(String filename) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(filename));
			out.write(this.toString());
			out.close();
		} catch (IOException e) {
			System.out.println("Exception in file writing" + e);
		}
	}

	public void readADOPTGraph(String filename) {
		reset();
		// int[] stats = new int[10];
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(filename)));
			String line = in.readLine();
			while (line != null) {
				StringTokenizer t = new StringTokenizer(line);
				if (t.hasMoreTokens()) {
					t.nextToken();
				} else {
					System.out.println("WRONG FILE FORMAT");
					return;
				}
				if (line.startsWith("VARIABLE")) {
					int tid = new Integer(t.nextToken()).intValue(); // var
					// id
					colourMap.put(tid, 0); // initializing all nodes' colour to
					// 0
					edgeMap.put(tid, new HashSet<Integer>());
					int aid = new Integer(t.nextToken()).intValue(); // agent
					// id;
					// useless
					// for
					// us
					int size = new Integer(t.nextToken()).intValue(); // domain
					// size
					numberOfColours = size;
					numberOfNodes++;
					// randomly initialize all nodes
					double randomPosition = (Math.random() * numberOfColours);
					int indexPosition = (int) Math.floor(randomPosition);
					colourMap.put(tid, indexPosition);
				} else if (line.startsWith("CONSTRAINT")) {
					int node1 = new Integer(t.nextToken()).intValue();
					int node2 = new Integer(t.nextToken()).intValue();
					edgeMap.get(node1).add(node2);
					edgeMap.get(node2).add(node1);
					numberOfEdges++;
				}
				line = in.readLine();
			}
		} catch (IOException e) {
			System.out.println("Exception in file reading" + e);
		}

		/*
		 * int links =0; for (Iterator i = edgeMap.keySet().iterator();
		 * i.hasNext();){ ArrayList<Integer> elist = edgeMap.get(i.next());
		 * links+=elist.size(); } numberOfEdges = links;
		 */
		// debug
		/*
		 * System.out.println("NumberOfAgents = "+numberOfNodes);
		 * System.out.println("NumberOfColors = "+numberOfColours);
		 * System.out.println("NumberOfEdges = " + numberOfEdges);
		 * System.out.println("Loaded ColorMap: " + colourMap);
		 * System.out.println("Loaded EdgeMap: " + edgeMap);
		 */
	}

	public void readGraph(String filename) {
		reset();

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(filename)));
			String line = in.readLine();
			while (line != null) {
				StringTokenizer st = null;
				if (line.startsWith("NumberOfNodes")) {
					st = new StringTokenizer(line);
					st.nextToken();
					numberOfNodes = Integer.parseInt(st.nextToken());
				} else if (line.startsWith("NumberOfColors")) {
					st = new StringTokenizer(line);
					st.nextToken();
					numberOfColours = Integer.parseInt(st.nextToken());
				} else if (line.startsWith("NodesColorMap")) {
					line = in.readLine();
					parseColor(line, "(", ",", ")", colourMap);
				} else if (line.startsWith("Edges")) {
					line = in.readLine();
					while (!line.startsWith("}")) {
						parseEdges(line, "<", ",", ">", edgeMap);
						line = in.readLine();
					}
				}
				line = in.readLine();
			}
		} catch (IOException e) {
			System.out.println("Exception in file writing" + e);
		}

		int links = 0;
		for (Iterator i = edgeMap.keySet().iterator(); i.hasNext();) {
			HashSet<Integer> elist = edgeMap.get(i.next());
			links += elist.size();
		}
		numberOfEdges = links;

		// debug
		// System.out.println("NumberOfAgents = "+numberOfNodes);
		// System.out.println("NumberOfColors = "+numberOfColours);
		System.out.println("NumberOfEdges = " + numberOfEdges);
		// System.out.println("Loaded ColorMap: " + colourMap);
		// System.out.println("Loaded EdgeMap: " + edgeMap);
	}

	void initColourRandomMap() {
		// System.out.println("Colouring Random");
		for (int i = 0; i < numberOfNodes; i++) {
			int v = (int) (Math.random() * numberOfColours);
			colourMap.put(i, v);
			HashSet<Integer> edges = new HashSet<Integer>();
			edgeMap.put(i, edges);
		}
	}

	void initColourFixMap() {
		// System.out.println("Colouring Fixed");
		for (int i = 0; i < numberOfNodes; i++) {
			colourMap.put(i, 0);
			HashSet<Integer> edges = new HashSet<Integer>();
			edgeMap.put(i, edges);
		}
	}

	void initColourMap(int type) {
		switch (type) {
		case UNIFORM_COLOUR:
			initColourFixMap();
			break;
		default:
			initColourRandomMap();
			break;
		}
	}

	public void genGraph(boolean colourable, double averageConnectionsPerAgent,
			int colouringType) {

		colourMap.clear();

		edgeMap.clear();

		int links = 0;

		int outerCounter = 0;

		// init colour list.
		initColourMap(0);

		for (Iterator i = colourMap.keySet().iterator(); i.hasNext();) {

			Integer agentA = (Integer) i.next();

			int innerCounter = 0;

			for (Iterator j = colourMap.keySet().iterator(); j.hasNext();) {

				Integer agentB = (Integer) j.next();

				boolean link = false;

				if (colourable) {

					int colourA = colourMap.get(agentA);
					int colourB = colourMap.get(agentB);

					if (innerCounter > outerCounter
							&& colourA != colourB
							&& Math.random() < 2.0
									/ ((double) numberOfNodes - 1.0)
									* averageConnectionsPerAgent
									/ (1.0 - 1.0 / (double) numberOfColours)) {

						link = true;

					}

				} else {

					if (innerCounter > outerCounter
							&& Math.random() < 2.0
									/ ((double) numberOfNodes - 1.0)
									* averageConnectionsPerAgent) {

						link = true;

					}

				}

				if (link) {

					edgeMap.get(agentA).add(agentB);
					edgeMap.get(agentB).add(agentA);

					links += 1;

				}

				innerCounter += 1;

			}
			outerCounter += 1;
		}

		// change initial coluring
		for (int i = 0; i < numberOfNodes; i++) {
			int v = (int) (Math.random() * numberOfColours);
			;
			if (colouringType == UNIFORM_COLOUR) {
				v = 0;
			}
			colourMap.put(i, v);
		}

		numberOfEdges = links;

	}

	public void genConnectedGraph(double averageConnectionsPerAgent) {

		colourMap.clear();

		edgeMap.clear();

		int links = 0;

		int outerCounter = 0;

		// init colour list.
		initColourMap(0);

		// connect all nodes in a chain
		Iterator current = colourMap.keySet().iterator();
		Iterator next = colourMap.keySet().iterator();
		if (next.hasNext()) {
			next.next();
		}
		for (; next.hasNext();) {

			Integer currId = (Integer) current.next();

			Integer nextId = (Integer) next.next();

			edgeMap.get(currId).add(nextId);
			edgeMap.get(nextId).add(currId);

			links += 1;

		}

		for (Iterator i = colourMap.keySet().iterator(); i.hasNext();) {

			Integer agentA = (Integer) i.next();

			int innerCounter = 0;

			for (Iterator j = colourMap.keySet().iterator(); j.hasNext();) {

				Integer agentB = (Integer) j.next();

				boolean link = false;

				if (innerCounter > outerCounter
						&& Math.random() < 2.0 / ((double) numberOfNodes - 1.0)
								* averageConnectionsPerAgent
						&& (!edgeMap.get(agentA).contains(agentB))) {

					link = true;

				}

				if (link) {

					edgeMap.get(agentA).add(agentB);
					edgeMap.get(agentB).add(agentA);

					links += 1;

				}

				innerCounter += 1;

			}
			outerCounter += 1;
		}

		// change initial coluring
		for (int i = 0; i < numberOfNodes; i++) {
			int v = (int) (Math.random() * numberOfColours);
			;
			colourMap.put(i, v);
		}

		// check that all nodes are connected to at least another node
		/*
		 * for (Integer agentId : edgeMap.keySet()) { if
		 * (edgeMap.get(agentId).size() == 0){
		 * 
		 * int anotherAgentId = (int) (Math.random()numberOfNodes);
		 * 
		 * edgeMap.get(agentId).add(anotherAgentId);
		 * edgeMap.get(anotherAgentId).add(agentId);
		 * 
		 * links += 1; } }
		 */

		numberOfEdges = links;

	}

	private void parseColor(String line, String initSep, String middleSep,
			String finalSep, HashMap<Integer, Integer> map) {
		StringTokenizer st = new StringTokenizer(line);
		int node1 = -1;
		int node2 = -1;
		if (line.equals("")) {
			System.out.println("Empty Line");
			return;
		}
		String token = null;
		do {
			token = st.nextToken();
			if (token.equals(initSep)) {
				token = st.nextToken();
				node1 = Integer.parseInt(token);
			} else if (token.equals(middleSep)) {
				node2 = Integer.parseInt(st.nextToken());
			} else if (token.equals(finalSep)) {
				map.put(node1, node2);
			}
		} while (st.hasMoreTokens());
	}

	private void parseEdges(String line, String initSep, String middleSep,
			String finalSep, HashMap<Integer, HashSet<Integer>> map) {
		StringTokenizer st = new StringTokenizer(line);
		HashSet<Integer> neighbours = new HashSet<Integer>();
		int node1 = -1;
		int node2 = -1;
		if (line.equals("")) {
			return;
		}
		String token = null;
		do {
			token = st.nextToken();
			if (token.equals(initSep)) {
				token = st.nextToken();
				node1 = Integer.parseInt(token);
			} else if (token.equals(middleSep)) {
				node2 = Integer.parseInt(st.nextToken());
			} else if (token.equals(finalSep)) {
				neighbours.add(node2);
			}
		} while (st.hasMoreTokens());
		map.put(node1, neighbours);
	}

	public String toString() {
		String res = "NumberOfNodes " + numberOfNodes + "\nNumberOfColors "
				+ numberOfColours + " \nNodesColorMap{ \n";
		// write Nodes
		for (Iterator i = colourMap.keySet().iterator(); i.hasNext();) {
			Integer node = (Integer) i.next();
			res += "( " + node + " , " + colourMap.get(node) + " ) ";
		}
		res += " \n}\n";
		// write edges Edges
		res += "Edges {\n";
		for (Iterator i = edgeMap.keySet().iterator(); i.hasNext();) {
			Integer node = (Integer) i.next();
			HashSet<Integer> neighbours = edgeMap.get(node);
			for (Iterator j = neighbours.iterator(); j.hasNext();) {
				// Integer neighbour = j.next();
				res += "< " + node + " , " + j.next() + " > ";
			}
			res += "\n";
		}
		res += "}\n";
		/*
		 * //debug write variable node state res+="InitialNodesColourMap{"; for
		 * (Iterator<SensorNode> i =
		 * initialColourMap.keySet().iterator();i.hasNext();){ SensorNode sn =
		 * i.next(); res+="( " + sn.getIndex() + " , " +
		 * initialColourMap.get(sn) + " ) "; } res+="}\n";
		 */
		return res;
	}

	public static void main(String[] args) {
		int currentNodes = 5;
		int currentColours = 3;
		boolean colourable = true;
		double avgConnections = 3.0;
		int colouringType = 0;
		GraphManager gmGen = new GraphManager(currentNodes, currentColours);
		/*
		 * for (currentNodes = 10; currentNodes < 100; currentNodes+=5){ for
		 * (currentColours = 3; currentColours < 6; currentColours++){ for (int
		 * run = 0; run < 20; run++){ gmGen.numberOfNodes = currentNodes;
		 * gmGen.numberOfColours = currentColours; colouringType =
		 * UNIFORM_COLOUR;
		 * gmGen.genGraph(colourable,avgConnections,colouringType);
		 * System.out.println("Generating graph with "+currentNodes+" nodes
		 * "+currentColours+" colours");
		 * gmGen.writeGraph("/home/sandrof/dataGraphs/SumMaxProblems/smGraph-"
		 * +currentNodes
		 * +"-"+currentColours+"-"+avgConnections+"-"+(colourable?""
		 * :"not")+"colourable-"
		 * +((colouringType==UNIFORM_COLOUR)?"":"not")+"uniform-gr"+run+".dat");
		 * 
		 * colouringType = NOT_UNIFORM_COLOUR;
		 * gmGen.genGraph(colourable,avgConnections,colouringType);
		 * System.out.println("Generating graph with "+currentNodes+" nodes
		 * "+currentColours+" colours");
		 * gmGen.writeGraph("/home/sandrof/dataGraphs/SumMaxProblems/smGraph-"
		 * +currentNodes
		 * +"-"+currentColours+"-"+avgConnections+"-"+(colourable?""
		 * :"not")+"colourable-"
		 * +((colouringType==UNIFORM_COLOUR)?"":"not")+"uniform-gr"+run+".dat");
		 * } } }
		 */
		gmGen.readADOPTGraph(args[0]);
		System.out.println("number of links = " + gmGen.numberOfEdges);
		System.out.println(args[1]);
		gmGen.writeGraph(args[1]);
		/*
		 * StringTokenizer t = new StringTokenizer(args[0]); String token =
		 * t.nextToken("/"); token = t.nextToken("/"); token = t.nextToken("/");
		 * token = t.nextToken("/"); token = t.nextToken("-"); String filename =
		 * "/home/sandrof/dataGraphs/SumMaxProblems/new"+token; token =
		 * t.nextToken("-"); filename += "-" + token + "-" +
		 * (gmGen.numberOfEdges/2); filename += t.nextToken("\n");
		 * System.out.println(filename); gmGen.writeGraph(filename);
		 */

	}
}