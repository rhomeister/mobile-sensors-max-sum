package boundedMaxSum;

import java.util.HashSet;
import java.util.Set;

/**
 * Class representing an agent which exists as a part of a dependency graph with
 * other agents. By exchanging messages, these agents form a spanning tree.
 * 
 * @author mpw104
 * 
 */
public class TreeFormingAgent {

	Set<TreeFormingAgent> neighbours;

	protected int groupIDMin;

	protected int groupIDMax;

	protected int nodeid;

	protected int colourid;

	protected Set<TreeFormingAgent> internalNodes;

	protected Set<TreeFormingAgent> loopyNodes;

	public TreeFormingAgent(Set<TreeFormingAgent> neighbours, int id,
			int colourid) {
		this.groupIDMax = this.groupIDMin = nodeid = id;
		this.colourid = colourid;

		this.neighbours = neighbours;
		internalNodes = new HashSet<TreeFormingAgent>();
		loopyNodes = new HashSet<TreeFormingAgent>();

	}

	public void addNeighbour(TreeFormingAgent neighbour) {
		neighbours.add(neighbour);
	}

	public void initialise() {
		for (TreeFormingAgent neighbour : neighbours) {
			if (!internalNodes.contains(neighbour)
					&& !loopyNodes.contains(neighbour)) {
				neighbour.send(new TreeFormationMessage(this, neighbour,
						TreeFormationMessage.MessageType.JOIN, groupIDMax,
						groupIDMin));
			}
		}
	}

	public void send(TreeFormationMessage msg) {
		// System.out.println(msg);
		if (msg.getType() == TreeFormationMessage.MessageType.JOIN) {
			if (groupIDMin == msg.getGroupIDMin()
					&& groupIDMax == msg.groupIDMax) {
				// Message sent from a node in the same group
				msg.sender.send(new TreeFormationMessage(this, msg.sender,
						TreeFormationMessage.MessageType.REJECT, groupIDMax,
						groupIDMin));
				loopyNodes.add(msg.sender);
			} else {
				// Not from same group; OK to join.
				groupIDMax = Math.max(groupIDMax, msg.getGroupIDMax());
				groupIDMin = Math.min(groupIDMin, msg.getGroupIDMin());
				msg.sender.send(new TreeFormationMessage(this, msg.sender,
						TreeFormationMessage.MessageType.ACCEPT, groupIDMax,
						groupIDMin));
				internalNodes.add(msg.sender);
			}
		} else if (msg.getType() == TreeFormationMessage.MessageType.REJECT) {
			loopyNodes.add(msg.sender);
		} else if (msg.getType() == TreeFormationMessage.MessageType.ACCEPT) {
			groupIDMax = Math.max(groupIDMax, msg.getGroupIDMax());
			groupIDMin = Math.min(groupIDMin, msg.getGroupIDMin());
			for (TreeFormingAgent node : internalNodes) {
				node.send(new TreeFormationMessage(this, node,
						TreeFormationMessage.MessageType.NOTIFY, groupIDMax,
						groupIDMin));
			}
			internalNodes.add(msg.sender);
		} else if (msg.getType() == TreeFormationMessage.MessageType.NOTIFY) {
			groupIDMax = msg.getGroupIDMax();
			groupIDMin = msg.getGroupIDMin();
		}

	}

	public String toString() {
		return "Node " + nodeid;
	}

	public int getColourid() {
		return colourid;
	}

	public void setColourid(int colourid) {
		this.colourid = colourid;
	}

	public int getNodeid() {
		return nodeid;
	}

	public void setNodeid(int nodeid) {
		this.nodeid = nodeid;
	}

	public Set<TreeFormingAgent> getInternalNodes() {
		return internalNodes;
	}
}
