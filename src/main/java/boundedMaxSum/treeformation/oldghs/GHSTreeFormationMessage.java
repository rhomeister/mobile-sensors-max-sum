package boundedMaxSum.treeformation.oldghs;

import boundedMaxSum.treeformation.oldghs.GHSTreeFormingAgent.NodeState;

/**
 * A message sent as part of the GHS algorithm.
 * 
 * @author mw08v
 * 
 */
public class GHSTreeFormationMessage {

	enum MessageType {
		CONNECT, INITIATE, TEST, ACCEPT, REJECT, CHANGE_ROOT, REPORT, COMPLETE, BOUND
	}

	protected int counter = 0;

	protected MessageType type;

	protected double fragmentID;

	protected int level;

	protected double bestWeight;

	protected GHSTreeFormingAgent.NodeState nodeState;

	protected GHSTreeFormingAgent sender;

	protected GHSTreeFormingAgent receiver;

	protected boolean printed = false;

	protected double minBound;

	protected double maxBound;

	public GHSTreeFormingAgent getReceiver() {
		return receiver;
	}

	public void setReceiver(GHSTreeFormingAgent receiver) {
		this.receiver = receiver;
	}

	public GHSTreeFormationMessage(MessageType type, double fragmentID,
			int level, GHSTreeFormingAgent.NodeState nodeState,
			GHSTreeFormingAgent sender, GHSTreeFormingAgent receiver) {
		this.type = type;
		this.fragmentID = fragmentID;
		this.level = level;
		this.nodeState = nodeState;
		this.sender = sender;
		this.receiver = receiver;
	}

	/**
	 * Construct a REPORT message
	 */
	public GHSTreeFormationMessage(GHSTreeFormingAgent sender,
			GHSTreeFormingAgent receiver, double bestWeight) {
		this.type = MessageType.REPORT;
		this.sender = sender;
		this.receiver = receiver;
		this.bestWeight = bestWeight;
	}

	public GHSTreeFormationMessage(GHSTreeFormingAgent sender,
			GHSTreeFormingAgent receiver, double minBound, double maxBound) {
		this.type = MessageType.BOUND;
		this.sender = sender;
		this.receiver = receiver;
		this.minBound = minBound;
		this.maxBound = maxBound;
	}

	/**
	 * Construct a COMPLETE message
	 * 
	 * @param sender
	 * @param receiver
	 */
	public GHSTreeFormationMessage(GHSTreeFormingAgent sender,
			GHSTreeFormingAgent receiver) {
		this.type = MessageType.COMPLETE;
		this.receiver = receiver;
		this.sender = sender;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public double getFragmentID() {
		return fragmentID;
	}

	public void setFragmentID(int fragmentID) {
		this.fragmentID = fragmentID;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public GHSTreeFormingAgent.NodeState getNodeState() {
		return nodeState;
	}

	public void setNodeState(GHSTreeFormingAgent.NodeState nodeState) {
		this.nodeState = nodeState;
	}

	public GHSTreeFormingAgent getSender() {
		return sender;
	}

	public void setSender(GHSTreeFormingAgent sender) {
		this.sender = sender;
	}

	public double getBestWeight() {
		return bestWeight;
	}

	public void setBestWeight(double bestWeight) {
		this.bestWeight = bestWeight;
	}

	public void setFragmentID(double fragmentID) {
		this.fragmentID = fragmentID;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("From ");
		sb.append(sender);
		sb.append(" to ");
		sb.append(receiver);
		sb.append(" ");
		sb.append(type);
		if (type == MessageType.CONNECT || type == MessageType.ACCEPT
				|| type == MessageType.CHANGE_ROOT
				|| type == MessageType.INITIATE || type == MessageType.REJECT
				|| type == MessageType.TEST) {
			sb.append(" FID: ");
			sb.append(fragmentID);
			sb.append(" L: ");
			sb.append(level);
			sb.append(" State: ");
			sb.append(nodeState);
		}

		if (type == MessageType.REPORT) {
			sb.append(" W: ");
			sb.append(bestWeight);
		}

		if (type == MessageType.BOUND) {
			sb.append(" Min: ");
			sb.append(minBound);
			sb.append(" Max: ");
			sb.append(maxBound);
		}
		return sb.toString();
	}

}
