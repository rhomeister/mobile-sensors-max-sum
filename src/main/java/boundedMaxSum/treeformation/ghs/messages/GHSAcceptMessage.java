package boundedMaxSum.treeformation.ghs.messages;

import boundedMaxSum.treeformation.ghs.GHSAgent;
import boundedMaxSum.treeformation.ghs.GHSEdge;
import boundedMaxSum.treeformation.ghs.GHSMessage;

public class GHSAcceptMessage extends GHSMessage {
	
	public GHSAcceptMessage(GHSEdge edge, GHSAgent sender) {
		super(edge, sender, GHSMessageType.ACCEPT);
	}
	
	@Override
	public String toString() {
		return "Accept";
	}

}
