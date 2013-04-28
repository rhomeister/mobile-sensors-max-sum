package boundedMaxSum.treeformation.ghs.messages;

import boundedMaxSum.treeformation.ghs.GHSAgent;
import boundedMaxSum.treeformation.ghs.GHSEdge;
import boundedMaxSum.treeformation.ghs.GHSMessage;

public class GHSChangeRootMessage extends GHSMessage {
	
	public GHSChangeRootMessage(GHSEdge edge, GHSAgent sender) {
		super(edge, sender, GHSMessageType.CHANGEROOT);
	}
	
	@Override
	public String toString() {
		return "ChangeRoot";
	}

}
