package maxSumController.communication;

import maxSumController.FactorGraphNode;

import org.apache.commons.lang.Validate;

public abstract class Message<S extends FactorGraphNode, R extends FactorGraphNode, C> {

	private S sender;

	private R receiver;

	private C contents;

	public Message(S sender, R receiver, C contents) {
		this.sender = sender;
		this.receiver = receiver;
		this.contents = contents;
		Validate.notNull(contents);
	}

	public S getSender() {
		return sender;
	}

	public R getReceiver() {
		return receiver;
	}

	public C getContents() {
		return contents;
	}

	public Object getReceivingAgentIdentifier() {
		/*
		 * if (receiver instanceof ExternalFunction) { return
		 * ((ExternalFunction) receiver).getOwningAgentIdentifier(); } else if
		 * (receiver instanceof AbstractExternalVariable) { return
		 * ((AbstractExternalVariable) receiver) .getOwningAgentIdentifier(); }
		 */

		if (receiver != null)
			return receiver.getOwningAgentIdentifier();
		return null;
	}

	public String toString() {

		String returnString = "";
		returnString += "<Message Sender: " + sender;
		returnString += ". Receiver: " + receiver + ". Contents: ";

		returnString += contents.toString() + ">";

		return returnString;
	}

	public abstract int getSize();
}
