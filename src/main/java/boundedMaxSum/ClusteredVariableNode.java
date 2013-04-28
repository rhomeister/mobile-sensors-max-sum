package boundedMaxSum;

import java.util.Collection;

import maxSumController.VariableNode;
import maxSumController.discrete.VariableJointState;
import maxSumController.discrete.prune.PruneMessage;

/**
 * A variable Node that handles clustered variables
 * 
 * 
 * @author sandrof
 * 
 * @param <V>
 *            the type of variables handled by the variable node
 * @param <S>
 *            the type of variable states handled by the variable noded
 */

public class ClusteredVariableNode<V extends ClusteredVariable<S>, S extends VariableJointState>
		extends VariableNode<V, S> {

	public ClusteredVariableNode(V variable) {
		super(variable);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Collection<PruneMessage> handlePruneMessage(
			Collection<PruneMessage> messagesForNode) {
		// TODO Auto-generated method stub
		return null;
	}

}
