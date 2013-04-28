package boundedMaxSum.treeformation.oldghs;

import boundedMaxSum.LinkBoundedInternalFunction;
import maxSumController.discrete.DiscreteInternalVariable;

public class LinkEdge extends Edge{

	
	/*
	 * the variable for this edge
	 * */
	protected DiscreteInternalVariable variable;
	
	public LinkEdge(GHSTreeFormingAgent endpoint, GHSTreeFormingAgent startpoint,
			LinkBoundedInternalFunction function, DiscreteInternalVariable var) {
		super(endpoint, startpoint, function);
		this.variable = var;
	}

	public double getWeight() {
		// Change sign of bound difference to give a max spanning tree
//		if (Double.isNaN(weight)){
//			System.out.println("computing weight for function "+function.getName() + " and variable " + variable.getName() + (-1
//				* ((LinkBoundedInternalFunction)function).getWeight(variable.getName())));
//			System.out.println("function " + function.getName() + " payoff = " + ((TwoVariablesRandomPayoffFunction) function).getPayoffMatrix());
//		}	
		return (Double.isNaN(weight) ? -1
				* ((LinkBoundedInternalFunction)function).getWeight(variable.getName())
				: weight);
	}

	
}
