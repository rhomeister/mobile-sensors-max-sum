package boundedMaxSum;

public class TreeFormationMessage {

	public static enum MessageType {
		JOIN, ACCEPT, REJECT, NOTIFY
	};

	protected TreeFormingAgent sender;

	protected TreeFormingAgent receiver;

	protected MessageType type;

	protected int groupIDMin;

	protected int groupIDMax;

	public TreeFormationMessage(TreeFormingAgent sender,
			TreeFormingAgent receiver, MessageType type, int groupIDmax,
			int groupIDmin) {
		this.sender = sender;
		this.receiver = receiver;
		this.type = type;
		this.groupIDMax = groupIDmax;
		this.groupIDMin = groupIDmin;
	}

	public TreeFormingAgent getSender() {
		return sender;
	}

	public void setSender(TreeFormingAgent sender) {
		this.sender = sender;
	}

	public TreeFormingAgent getReceiver() {
		return receiver;
	}

	public void setReceiver(TreeFormingAgent receiver) {
		this.receiver = receiver;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public int getGroupIDMin() {
		return groupIDMin;
	}

	public void setGroupIDMin(int groupIDMin) {
		this.groupIDMin = groupIDMin;
	}

	public int getGroupIDMax() {
		return groupIDMax;
	}

	public void setGroupIDMax(int groupIDMax) {
		this.groupIDMax = groupIDMax;
	}

	public String toString() {
		return "From " + sender + " to " + receiver + " " + type + " groupid {"
				+ groupIDMin + "," + groupIDMax + "}\n";
	}

}
