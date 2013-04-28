package maxSumController;

import java.util.Collection;

import maxSumController.communication.Inbox;
import maxSumController.communication.PostOffice;

public class SpanningTreePlugin implements AlgorithmPlugin {

	private PostOffice postOffice;
	private AbstractMaxSumController<?, ?, ?> maxSumController;

	public SpanningTreePlugin(AbstractMaxSumController<?, ?, ?> maxSumController) {
		postOffice = maxSumController.getPostOffice();
		this.maxSumController = maxSumController;
	}

	@Override
	public void run() {
		// spanning tree algorithm
		for (InternalNodeRepresentation internalNode : maxSumController
				.getInternalNodes()) {
			Inbox inbox = postOffice.getInbox(internalNode);

			Collection<SpanningTreeMessage> messagesForNode = inbox
					.consumeMessages(SpanningTreeMessage.class);

			Collection<SpanningTreeMessage> messages = internalNode
					.handleSpanningTreeMessages(messagesForNode);
			
			postOffice.process(messages);
		}
	}
}
