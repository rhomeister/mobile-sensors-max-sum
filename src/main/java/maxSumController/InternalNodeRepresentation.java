package maxSumController;

import java.util.Collection;
import java.util.Set;
import java.util.Vector;

import maxSumController.communication.ValuePropagationMessage;
import maxSumController.discrete.prune.PruneMessage;
import maxSumController.valuepropagation.DistributedSpanningTreeProtocol;

import org.apache.commons.lang.Validate;

public abstract class InternalNodeRepresentation {

	private DistributedSpanningTreeProtocol spanningTreeProtocol;

	public FactorGraphNode getParent() {
		return getDistributedSpanningTreeProtocol().getParent();
	}

	public FactorGraphNode getRoot() {
		return getDistributedSpanningTreeProtocol().getRoot();
	}

	public Set<FactorGraphNode> getActiveNeighbours() {
		return getDistributedSpanningTreeProtocol().getActiveNeighbours();
	}

	public Collection<SpanningTreeMessage> startSpanningTreeAlgorithm() {
		return getDistributedSpanningTreeProtocol().startProtocol();
	}

	private DistributedSpanningTreeProtocol getDistributedSpanningTreeProtocol() {
		if (spanningTreeProtocol == null) {
			spanningTreeProtocol = new DistributedSpanningTreeProtocol(
					getRepresentedNode(), getRepresentedNode()
							.getDependencies());
		}

		return spanningTreeProtocol;
	}

	protected Collection<SpanningTreeMessage> handleSpanningTreeMessages(
			Collection<SpanningTreeMessage> messages) {
		return getDistributedSpanningTreeProtocol().handleSpanningTreeMessages(
				messages);
	}

	public abstract FactorGraphNode getRepresentedNode();

	public boolean isRoot() {
		return getRoot().equals(getRepresentedNode());
	}

	public abstract Collection<ValuePropagationMessage> handleValuePropagationMessage(
			ValuePropagationMessage message);

	public final Collection<ValuePropagationMessage> handleValuePropagationMessages(
			Collection<ValuePropagationMessage> messagesForNode) {
		// there should never be more than one message: parent should just send
		// one
		Validate.isTrue(messagesForNode.size() <= 1);

		if (messagesForNode.isEmpty())
			return new Vector<ValuePropagationMessage>();

		ValuePropagationMessage message = messagesForNode.iterator().next();

		// message must have originated from parent
		Validate.isTrue(message.getSender().equals(getParent()));

		return handleValuePropagationMessage(message);
	}

	public abstract Collection<PruneMessage> handlePruneMessage(
			Collection<PruneMessage> messagesForNode);

	@Override
	public String toString() {
		return getRepresentedNode().toString();
	}

}
