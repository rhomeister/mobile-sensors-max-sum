package boundedMaxSum.treeformation.ghs.messages;

import boundedMaxSum.treeformation.ghs.GHSAgent;
import boundedMaxSum.treeformation.ghs.GHSEdge;
import boundedMaxSum.treeformation.ghs.GHSMessage;

public class GHSConnectMessage extends GHSMessage {
	
	protected int level;
	
	public GHSConnectMessage(GHSEdge edge, GHSAgent sender, int level) {
		super(edge, sender, GHSMessageType.CONNECT);
		this.level = level;
	}
	
	public int getLevel() {
		return level;
	}

	@Override
	public String toString() {
		return "Connect(" + level + ")";
	}

}
