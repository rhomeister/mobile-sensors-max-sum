package boundedMaxSum.treeformation.ghs;

import boundedMaxSum.LinkBoundedInternalFunction;
import maxSumController.discrete.DiscreteInternalVariable;

public class GHSLinkEdge extends GHSEdge {
	
	public GHSLinkEdge(GHSAgent one, GHSAgent two, LinkBoundedInternalFunction function, DiscreteInternalVariable<?> variable) {
		super(one, two, function, variable);
	}
	
	public GHSWeight getWeight() {
		// Change sign of bound difference to give a max spanning tree
		return new GHSWeight(ends, (Double.isNaN(weight.getWeight()) ? -1
				* ((LinkBoundedInternalFunction)function).getWeight(variable.getName())
				: weight.getWeight()));
	}

	public DiscreteInternalVariable<?> getVariable() {
		return variable;
	}

}
