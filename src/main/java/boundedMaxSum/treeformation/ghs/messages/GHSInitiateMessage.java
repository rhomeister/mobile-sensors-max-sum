package boundedMaxSum.treeformation.ghs.messages;

import boundedMaxSum.treeformation.ghs.GHSAgent;
import boundedMaxSum.treeformation.ghs.GHSEdge;
import boundedMaxSum.treeformation.ghs.GHSFragmentID;
import boundedMaxSum.treeformation.ghs.GHSMessage;
import boundedMaxSum.treeformation.ghs.GHSNodeState;

public class GHSInitiateMessage extends GHSMessage {
	
	protected int level;
	protected GHSFragmentID fragmentID;
	protected GHSNodeState state;
	
	public GHSInitiateMessage(GHSEdge edge, GHSAgent sender, int level, GHSFragmentID fragmentID, GHSNodeState state) {
		super(edge, sender, GHSMessageType.INITIATE);
		this.level = level;
		this.fragmentID = fragmentID;
		this.state = state;
	}

	public int getLevel() {
		return level;
	}

	public GHSFragmentID getFragmentID() {
		return fragmentID;
	}

	public GHSNodeState getState() {
		return state;
	}
	
	@Override
	public String toString() {
		return "Initiate(" + level + "," + fragmentID + "," + state + ")";
	}

}
