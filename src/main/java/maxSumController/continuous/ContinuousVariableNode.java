package maxSumController.continuous;

import java.util.Collection;

import org.apache.commons.lang.NotImplementedException;

import maxSumController.VariableNode;
import maxSumController.discrete.prune.PruneMessage;

public class ContinuousVariableNode extends
		VariableNode<ContinuousInternalVariable, ContinuousVariableState> {

	public ContinuousVariableNode(ContinuousInternalVariable variable) {
		super(variable);
	}
	
	@Override
	public Collection<PruneMessage> handlePruneMessage(
			Collection<PruneMessage> messagesForNode) {
		throw new NotImplementedException();
	}
}
