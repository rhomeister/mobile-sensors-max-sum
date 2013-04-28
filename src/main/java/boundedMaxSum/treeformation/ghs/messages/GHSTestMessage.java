package boundedMaxSum.treeformation.ghs.messages;

import boundedMaxSum.treeformation.ghs.GHSAgent;
import boundedMaxSum.treeformation.ghs.GHSEdge;
import boundedMaxSum.treeformation.ghs.GHSFragmentID;
import boundedMaxSum.treeformation.ghs.GHSMessage;

public class GHSTestMessage extends GHSMessage {
	
	protected int level;
	protected GHSFragmentID fragmentID;
	
	public GHSTestMessage(GHSEdge edge, GHSAgent sender, int level, GHSFragmentID fragmentID) {
		super(edge, sender, GHSMessageType.TEST);
		this.level = level;
		this.fragmentID = fragmentID;
	}

	public int getLevel() {
		return level;
	}

	public GHSFragmentID getFragmentID() {
		return fragmentID;
	}
	
	@Override
	public String toString() {
		return "Test(" + level + "," + fragmentID + ")";
	}

}
