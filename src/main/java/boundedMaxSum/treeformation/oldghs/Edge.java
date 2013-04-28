package boundedMaxSum.treeformation.oldghs;

import boundedMaxSum.BoundedInternalFunction;

/**
 * Data structure for the GHS MST algorithm, used to store all the information a
 * node knows about one of its edges. Can be provided with a weight in two ways:
 * explicitly, or by a BoundedInternalFunction. In the latter case the sign of
 * the weight is change, which will yield a maximum spanning tree.
 * 
 * @author mpw104
 * 
 */
public class Edge implements Comparable<Edge> {

	/*
	 * The edge weight.
	 */
	protected double weight = Double.NaN;

	/*
	 * The node at the other end.
	 */
	public GHSTreeFormingAgent endpoint;

	/*
	 * The node at the this end
	 */
	public GHSTreeFormingAgent startpoint;

	/**
	 * The function belonging to this edge.
	 */
	public BoundedInternalFunction function;

	/*
	 * The state of the edge
	 */
	public GHSTreeFormingAgent.EdgeState state;

	public Edge(GHSTreeFormingAgent endpoint, GHSTreeFormingAgent startpoint,
			BoundedInternalFunction function) {
		this.endpoint = endpoint;
		this.function = function;
		this.startpoint = startpoint;
	}

	public Edge(GHSTreeFormingAgent endpoint, GHSTreeFormingAgent startpoint,
			double weight) {
		this.endpoint = endpoint;
		this.weight = weight;
		this.startpoint = startpoint;
	}

	public double getWeight() {
		// Change sign of bound difference to give a max spanning tree
		return (Double.isNaN(weight) ? -1
				* (function.getMaximumBound() - function.getMinimumBound())
				: weight);
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public GHSTreeFormingAgent getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(GHSTreeFormingAgent endpoint) {
		this.endpoint = endpoint;
	}

	public GHSTreeFormingAgent.EdgeState getState() {
		return state;
	}

	public void setState(GHSTreeFormingAgent.EdgeState state) {
		this.state = state;
	}

	public int compareTo(Edge another) {
		return (int) Math.round(getWeight() - another.getWeight());

	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Edge) {
			return getWeight() == ((Edge) obj).getWeight();
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "S " + startpoint + " -> E " + endpoint + " state " + state
				+ " weigth " + getWeight();
	}

}
