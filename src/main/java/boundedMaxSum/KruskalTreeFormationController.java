package boundedMaxSum;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.commons.lang.Validate;

import boundedMaxSum.treeformation.oldghs.Edge;
import boundedMaxSum.treeformation.oldghs.GHSTreeFormingAgent;
import boundedMaxSum.treeformation.oldghs.GHSTreeFormingAgent.EdgeState;

/**
 * Form a minimum spanning tree using Kruskal's algorithm (centralised).
 * implemented based on the pseudocode at
 * http://en.wikipedia.org/wiki/Kruskal%27s_algorithm
 * 
 * The only change is that the main loop is extended to mark all remaining edges
 * as REJECTED after the tree has been formed.
 * 
 * 
 * @author mw08v
 * 
 */
public class KruskalTreeFormationController extends TreeFormationController<GHSTreeFormingAgent> {

	private Set<Set<GHSTreeFormingAgent>> trees;

	private PriorityQueue<Edge> edges;

	public KruskalTreeFormationController() {
		// The forest of trees.
		trees = new HashSet<Set<GHSTreeFormingAgent>>();
		edges = new PriorityQueue<Edge>();

	}

	@Override
	public void execute() throws Exception {
		// Initialisation - create edge queue and forest
		for (GHSTreeFormingAgent node : agents.values()) {
			HashSet<GHSTreeFormingAgent> set = new HashSet<GHSTreeFormingAgent>();
			set.add(node);
			trees.add(set);

			for (Edge edge : node.getEdgeMap().values()) {
				if (!edges.contains(edge)) {
					edges.add(edge);
				}
			}
		}

		int e = 0;
		while (!edges.isEmpty()) {
			Edge edge = edges.remove();

			if (e < agents.size() - 1) {
				// Find the trees containing each end of the edge.
				Set<GHSTreeFormingAgent> startTree = null;
				Set<GHSTreeFormingAgent> endTree = null;
				for (Set<GHSTreeFormingAgent> tree : trees) {
					if (tree.contains(edge.startpoint)) {
						startTree = tree;
					}
					if (tree.contains(edge.endpoint)) {
						endTree = tree;
					}
					if (startTree != null && endTree != null) {
						break;
					}
				}

				Validate.notNull(startTree);
				Validate.notNull(endTree);

				if (startTree != endTree) {
					// Edge joins two trees in the forest

					// Add to tree
					edge.startpoint.getEdgeMap().get(edge.endpoint).setState(
							EdgeState.BRANCH);
					edge.endpoint.getEdgeMap().get(edge.startpoint).setState(
							EdgeState.BRANCH);
					e++;
					// Merge the two trees
					startTree.addAll(endTree);
					trees.remove(endTree);

				} else {
					// Edge creates a cycle, not part of tree
					edge.startpoint.getEdgeMap().get(edge.endpoint).setState(
							EdgeState.REJECTED);
					edge.endpoint.getEdgeMap().get(edge.startpoint).setState(
							EdgeState.REJECTED);
				}
			} else {
				// Tree is complete - mark any remaining edges as rejected
				edge.setState(EdgeState.REJECTED);
			}
		}

		for (TreeFormationListener listener : listeners) {
			listener.treeFormationComplete();
		}
	}

	public static void main(String[] args) {
		final KruskalTreeFormationController controller = new KruskalTreeFormationController();
		GHSTreeFormingAgent node1 = new GHSTreeFormingAgent(1, null,""+1);
		GHSTreeFormingAgent node2 = new GHSTreeFormingAgent(2, null,""+2);
		GHSTreeFormingAgent node3 = new GHSTreeFormingAgent(3, null,""+3);
		GHSTreeFormingAgent node4 = new GHSTreeFormingAgent(4, null,""+4);
		GHSTreeFormingAgent node5 = new GHSTreeFormingAgent(5, null,""+5);
		controller.addAgent(node1);
		controller.addAgent(node2);
		controller.addAgent(node3);
		controller.addAgent(node4);
		controller.addAgent(node5);

		node1.addEdge(new Edge(node2, node1, -2));
		node1.addEdge(new Edge(node5, node1, -1));
		node2.addEdge(new Edge(node1, node2, -2));
		node2.addEdge(new Edge(node5, node2, -3));
		node2.addEdge(new Edge(node4, node2, -7));
		node2.addEdge(new Edge(node3, node2, -6));
		node3.addEdge(new Edge(node2, node3, -6));
		node3.addEdge(new Edge(node4, node3, -5));
		node4.addEdge(new Edge(node3, node4, -5));
		node4.addEdge(new Edge(node2, node4, -7));
		node4.addEdge(new Edge(node5, node4, -4));
		node5.addEdge(new Edge(node4, node5, -4));
		node5.addEdge(new Edge(node2, node5, -3));
		node5.addEdge(new Edge(node1, node5, -1));

		controller.addListener(new TreeFormationListener() {

			@Override
			public void treeFormationComplete() {
				controller.printEdges();

			}

		});

		try {
			controller.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getTotalMessages() {
		return 0;
	}

	@Override
	public int getStorageUsed() {
		// TODO Auto-generated method stub
		return 0;
	}

}
