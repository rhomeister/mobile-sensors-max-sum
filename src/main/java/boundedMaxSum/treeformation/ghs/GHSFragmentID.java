package boundedMaxSum.treeformation.ghs;

public class GHSFragmentID {
	
	GHSEdge edge;
	
	public GHSFragmentID(GHSEdge edge) {
		this.edge = edge;
	}

	public double getValue() {
		return edge.getWeight().getWeight();
	}

	public GHSEdge getEdge() {
		return edge;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((edge == null) ? 0 : edge.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GHSFragmentID other = (GHSFragmentID) obj;
		if (edge == null) {
			if (other.edge != null)
				return false;
		} else if (!edge.equals(other.edge))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return getValue() + "(" + edge.ends[0].getID() + "," + edge.ends[1].getID() + ")";
	}

}
