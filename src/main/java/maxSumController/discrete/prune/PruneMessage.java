package maxSumController.discrete.prune;

import maxSumController.FactorGraphNode;
import maxSumController.communication.Message;

public class PruneMessage extends Message<FactorGraphNode, FactorGraphNode, PruneMessageContents> {

	public PruneMessage(FactorGraphNode sender, FactorGraphNode receiver,
			PruneMessageContents contents) {
		super(sender, receiver, contents);
	}

	@Override
	public int getSize() {
		return 0;
	}

	public PruneMessageContents getStates() {
		return getContents();
	}
}
