package maxSumController.valuepropagation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import maxSumController.FactorGraphNode;
import maxSumController.SpanningTreeMessage;
import maxSumController.Variable;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;

public class DistributedSpanningTreeProtocol {

	private FactorGraphNode owner;

	private Set<? extends FactorGraphNode> neighbours;

	private FactorGraphNode parent;

	private Set<FactorGraphNode> activeNeighbours;

	private Variable root;

	public DistributedSpanningTreeProtocol(FactorGraphNode owner,
			Set<? extends FactorGraphNode> neighbours) {
		this.owner = owner;

		if (owner instanceof Variable) {
			root = (Variable) owner;
		}

		this.neighbours = neighbours;
		this.activeNeighbours = new HashSet<FactorGraphNode>(neighbours);
	}

	public Collection<SpanningTreeMessage> handleSpanningTreeMessages(
			Collection<SpanningTreeMessage> messages) {
		// function nodes just forward all messages from variables with a lower
		// originator than its current parent to all its neighbours
		
		Set<SpanningTreeMessage> result = new HashSet<SpanningTreeMessage>();
		
		if(messages.isEmpty())
			return result;

		Variable lowestReceivedOriginator = getLowestReceivedOriginator(messages);

		if (isBetterRoot(lowestReceivedOriginator)) {
			// we've found a better root
			// reactivate all neighbours
			activeNeighbours.addAll(neighbours);
			root = lowestReceivedOriginator;
			parent = null;
		}

		Collection<SpanningTreeMessage> messagesFromRoot = filterMessages(
				messages, root);

		// if we already have a parent, invalidate all links from which a
		// message was received, otherwise, pick a parent
		if (parent == null && !messagesFromRoot.isEmpty()) {
			parent = messagesFromRoot.iterator().next().getSender();
			activeNeighbours.remove(parent);
		}

		// forward the message to all active neighbours
		for (FactorGraphNode neighbour : activeNeighbours) {
			result.add(new SpanningTreeMessage(owner, neighbour, root));
		}

		for (SpanningTreeMessage message : messagesFromRoot) {
			activeNeighbours.remove(message.getSender());
		}

		return result;
	}

	private boolean isBetterRoot(Variable lowestReceivedOriginator) {
		if (lowestReceivedOriginator == null)
			return false;

		if (root == null)
			return true;

		return !root.equals(lowestReceivedOriginator)
				&& root.compareTo(lowestReceivedOriginator) > 0;

	}

	private Collection<SpanningTreeMessage> filterMessages(
			Collection<SpanningTreeMessage> messages, final Variable root) {
		return CollectionUtils.select(messages,
				new Predicate<SpanningTreeMessage>() {

					public boolean evaluate(SpanningTreeMessage message) {
						return message.getOriginator().equals(root);
					}
				});
	}

	private Variable getLowestReceivedOriginator(
			Collection<SpanningTreeMessage> messages) {
		Variable result = null;

		for (SpanningTreeMessage message : messages) {
			if (result == null)
				result = message.getOriginator();
			else
				result = result.compareTo(message.getOriginator()) > 0 ? message
						.getOriginator()
						: result;
		}

		return result;
	}

	public FactorGraphNode getRoot() {
		return root;
	}

	public FactorGraphNode getParent() {
		return parent;
	}

	public Set<FactorGraphNode> getActiveNeighbours() {
		return activeNeighbours;
	}

	public Collection<SpanningTreeMessage> startProtocol() {
		Set<SpanningTreeMessage> result = new HashSet<SpanningTreeMessage>();

		if (owner instanceof Variable) {
			// send a message to all active neighbours
			for (FactorGraphNode neighbour : neighbours) {
				result.add(new SpanningTreeMessage(owner, neighbour,
						(Variable) owner));
			}
		}

		return result;
	}
}
