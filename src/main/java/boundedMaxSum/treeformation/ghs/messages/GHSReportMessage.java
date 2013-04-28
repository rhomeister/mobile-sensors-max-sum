package boundedMaxSum.treeformation.ghs.messages;

import boundedMaxSum.treeformation.ghs.GHSAgent;
import boundedMaxSum.treeformation.ghs.GHSEdge;
import boundedMaxSum.treeformation.ghs.GHSMessage;
import boundedMaxSum.treeformation.ghs.GHSWeight;

public class GHSReportMessage extends GHSMessage {
	
	protected GHSWeight weight;
	
	public GHSReportMessage(GHSEdge edge, GHSAgent sender, GHSWeight weight) {
		super(edge, sender, GHSMessageType.REPORT);
		this.weight = weight;
	}
	
	public GHSWeight getWeight() {
		return weight;
	}
	
	@Override
	public String toString() {
		return "Report(" + weight + ")";
	}

}
