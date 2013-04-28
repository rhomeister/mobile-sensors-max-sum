package boundedMaxSum.treeformation.ghs.messages;

import boundedMaxSum.treeformation.ghs.GHSAgent;
import boundedMaxSum.treeformation.ghs.GHSEdge;
import boundedMaxSum.treeformation.ghs.GHSMessage;

public class GHSRejectMessage extends GHSMessage {
	
	public GHSRejectMessage(GHSEdge edge, GHSAgent sender) {
		super(edge, sender, GHSMessageType.REJECT);
	}
	
	@Override
	public String toString() {
		return "Reject";
	}

}
