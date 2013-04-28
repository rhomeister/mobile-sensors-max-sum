package boundedMaxSum.treeformation.ghs;

import boundedMaxSum.treeformation.ghs.messages.GHSMessageType;

public abstract class GHSMessage {
	
	GHSEdge edge;
	GHSAgent sender;
	GHSMessageType type;
	
	int counter = 0;

	public GHSMessage(GHSEdge edge, GHSAgent sender, GHSMessageType type) {
		this.edge = edge;
		this.sender = sender;
		this.type = type;
	}

	public GHSEdge getEdge() {
		return edge;
	}

	public GHSAgent getSender() {
		return sender;
	}

	public GHSMessageType getType() {
		return type;
	}
	
	public void incrementCounter() {
		counter++;
	}
	
	public int getCounter() {
		return counter;
	}

}
