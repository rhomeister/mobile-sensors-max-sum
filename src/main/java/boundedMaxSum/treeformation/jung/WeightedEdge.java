package boundedMaxSum.treeformation.jung;

public class WeightedEdge {

	private double weight;

	public WeightedEdge(double weight) {
		this.weight = weight;
	}

	public double getWeight() {
		return weight;
	}

	@Override
	public String toString() {
		return "" + weight;
	}
}
