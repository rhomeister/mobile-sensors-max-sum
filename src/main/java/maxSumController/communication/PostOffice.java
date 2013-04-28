package maxSumController.communication;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import maxSumController.AbstractMaxSumController;
import maxSumController.InternalNodeRepresentation;
import maxSumController.FactorGraphNode;

public class PostOffice {

	private int messageCount;

	private AbstractMaxSumController controller;

	private Map<FactorGraphNode, Inbox> inboxes = new HashMap<FactorGraphNode, Inbox>();

	public PostOffice(AbstractMaxSumController<?, ?, ?> controller) {
		this.controller = controller;
	}

	private Set<Message> messagesForExternal = new HashSet<Message>();

	private int messageSize;

	public <T extends Message> void process(Collection<T> messages) {
		for (Message message : messages) {
			FactorGraphNode destination = message.getReceiver();
			
			if (controller.isOwner(destination)) {
				messageCount++;
				messageSize += message.getSize();

				getInbox(destination).deliver(message);
			} else {
				messagesForExternal.remove(message);
				messagesForExternal.add(message);
			}
		}
	}

	public int getMessageCount() {
		return messageCount;
	}

	public int getMessageSize() {
		return messageSize;
	}

	public Inbox getInbox(FactorGraphNode node) {
		if (!inboxes.containsKey(node)) {
			inboxes.put(node, new Inbox());
		}

		return inboxes.get(node);
	}

	public Inbox getInbox(InternalNodeRepresentation node) {
		return getInbox(node.getRepresentedNode());
	}

	public Set<Message> getMessagesForExternal() {
		return messagesForExternal;
	}
}
