package boundedMaxSum.treeformation.ghs;

import java.util.Arrays;

public class GHSWeight implements Comparable<GHSWeight> {

	double weight;
	int[] ids;
	
	public GHSWeight(GHSAgent[] ends, double weight) {
		this.weight = weight;
		ids = new int[2];
		if (ends[0].getID() < ends[1].getID()) {
			ids[0] = ends[0].getID();
			ids[1] = ends[1].getID();
		} else {
			ids[0] = ends[1].getID();
			ids[1] = ends[0].getID();
		}
	}
	
	public GHSWeight(double weight) {
		ids = new int[]{0, 0};
		this.weight = weight;
	}
	
	public double getWeight() {
		return weight;
	}

	@Override
	public int compareTo(GHSWeight o) {
		double toreturn = getWeight() - o.getWeight();
		if (weight == Double.POSITIVE_INFINITY) {
			if (o.getWeight() == Double.POSITIVE_INFINITY) {
				return 0;
			}
		}
		
		if (toreturn != 0.0) {
			return toreturn > 0? 1 : -1;
		} else {
			if (o.ids[0] == ids[0]) {
				return ids[1] - o.ids[1];
			} else {
				return ids[0] - o.ids[0];
			}
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(ids);
		long temp;
		temp = Double.doubleToLongBits(weight);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		GHSWeight other = (GHSWeight) obj;
		if (!Arrays.equals(ids, other.ids))
			return false;
		if (Double.doubleToLongBits(weight) != Double
				.doubleToLongBits(other.weight))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getWeight() + "(" + ids[0] + "," + ids[1] + ")";
	}
	
}
