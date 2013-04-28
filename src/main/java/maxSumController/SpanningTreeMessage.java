package maxSumController;

import maxSumController.communication.Message;

public class SpanningTreeMessage extends Message<FactorGraphNode, FactorGraphNode, Variable>{

	public SpanningTreeMessage(FactorGraphNode sender, FactorGraphNode receiver, Variable originator) {
		super(sender, receiver, originator);
	}
	
	public Variable getOriginator() {
		return getContents();
	}
	
	@Override
	public int getSize() {
		return 1;
	}

}
