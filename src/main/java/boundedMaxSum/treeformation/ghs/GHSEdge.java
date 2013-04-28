package boundedMaxSum.treeformation.ghs;

import java.util.Arrays;

import maxSumController.discrete.DiscreteInternalVariable;
import boundedMaxSum.BoundedInternalFunction;

public class GHSEdge {

	protected GHSAgent[] ends;
	GHSFragmentID fragmentID;
	protected GHSWeight weight = new GHSWeight(Double.NaN);
	BoundedInternalFunction function;
	protected DiscreteInternalVariable<?> variable;

	public GHSEdge(GHSAgent one, GHSAgent two, GHSWeight weight) {
		ends = new GHSAgent[]{one, two};
		fragmentID = new GHSFragmentID(this);
		this.weight = weight;
	}
	
	public GHSEdge(GHSAgent one, GHSAgent two, DiscreteInternalVariable<?> variable) {
		ends = new GHSAgent[]{one, two};
		fragmentID = new GHSFragmentID(this);
		this.variable = variable;
;	}

	public GHSEdge(GHSAgent one, GHSAgent two, BoundedInternalFunction function, DiscreteInternalVariable<?> variable) {
		ends = new GHSAgent[]{one, two};
		fragmentID = new GHSFragmentID(this);
		this.function = function;
		this.variable = variable;
	}

	public GHSAgent getOtherEnd(GHSAgent end) {
		assert(ends[0].equals(end) || ends[1].equals(end));
		if (ends[0].equals(end)) {
			return ends[1];
		} else {
			return ends[0];
		}
	}

	public GHSWeight getWeight() {
		// Change sign of bound difference to give a max spanning tree
		return new GHSWeight(ends, (Double.isNaN(weight.getWeight()) ? -1
				* (function.getMaximumBound() - function.getMinimumBound())
				: weight.getWeight()));
	}

	public GHSFragmentID getFragmentID() {
		return fragmentID;
	}

	@Override
	public String toString() {
		return ends[0].toString() + "->" + ends[1].toString() + ":" + getWeight();
	}

	public DiscreteInternalVariable<?> getVariable() {
		return variable;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(ends);
		result = prime * result
				+ ((function == null) ? 0 : function.hashCode());
		result = prime * result
				+ ((variable == null) ? 0 : variable.hashCode());
		result = prime * result + ((weight == null) ? 0 : weight.hashCode());
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
		GHSEdge other = (GHSEdge) obj;
		if (!Arrays.equals(ends, other.ends))
			return false;
		if (fragmentID == null) {
			if (other.fragmentID != null)
				return false;
		}
		if (function == null) {
			if (other.function != null)
				return false;
		} else if (!function.equals(other.function))
			return false;
		if (variable == null) {
			if (other.variable != null)
				return false;
		} else if (!variable.equals(other.variable))
			return false;
		if (weight == null) {
			if (other.weight != null)
				return false;
		} else if (!weight.equals(other.weight))
			return false;
		return true;
	}
	
}
