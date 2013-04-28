package maxSumController.communication;

import maxSumController.FactorGraphNode;
import maxSumController.VariableState;

public class ValuePropagationMessage extends Message<FactorGraphNode, FactorGraphNode, VariableState> {

	public ValuePropagationMessage(FactorGraphNode sender, FactorGraphNode receiver,
			VariableState contents) {
		super(sender, receiver, contents);
	}

	@Override
	public int getSize() {
		return 1;
	}
	
	public VariableState getVariableState() {
		return getContents();
	}

}
