package maxSumController;

import java.util.Collection;

import maxSumController.communication.Inbox;
import maxSumController.communication.PostOffice;
import maxSumController.communication.ValuePropagationMessage;

public class ValuePropagationPlugin implements AlgorithmPlugin {
	private PostOffice postOffice;

	private AbstractMaxSumController<?, ?, ?> controller;

	public ValuePropagationPlugin(
			AbstractMaxSumController<?, ?, ?> maxSumController) {
		postOffice = maxSumController.getPostOffice();
		this.controller = maxSumController;
	}

	@Override
	public void run() {
		// value propagation
		for (InternalNodeRepresentation internalNode : controller
				.getInternalNodes()) {
			Inbox inbox = postOffice.getInbox(internalNode);

			Collection<ValuePropagationMessage> messagesForNode = inbox
					.consumeMessages(ValuePropagationMessage.class);

			postOffice.process(internalNode
					.handleValuePropagationMessages(messagesForNode));
		}
	}

}
