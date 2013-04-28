package boundedMaxSum;

import maxSumController.FunctionNode;
import maxSumController.InternalFunction;
import maxSumController.MarginalMaximisation;

/**
 * A function node that handles cluster of variables
 * 
 * @author sandrof
 * 
 */

public class ClusteredFunctionNode extends FunctionNode {

	public ClusteredFunctionNode(InternalFunction function,
			MarginalMaximisation maximiser) {
		super(function, maximiser);
	}

}
